/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imgprocessor.processor;

import com.imggraph.Algorithm;
import com.imggraph.Connector;
import com.imgmodel.graphModel.Graph;
import com.imgmodel.graphModel.Room;
import com.imgmodel.main.GraphMain;
import com.imgmodel.serialization.Serializer;
import com.imgprocessor.controller.DetailsAppendAction;
import com.imgprocessor.controller.DetailsAppendListener;
import com.imgprocessor.controller.ImageUpdateAction;
import com.imgprocessor.controller.ImageUpdateListener;
import com.imgprocessor.controller.ProgressChangedAction;
import com.imgprocessor.controller.ProgressChangedListener;
import com.imgprocessor.model.Line;
import com.imgprocessor.model.Representation;
import com.imgprocessor.opencvtest.HoughLineDetection;

import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
/**
 *
 * @author tifuivali
 */
public class ImageProcessorImpl implements ImageProcessor {
    
    private ImagePreProcessor imagePreProcessor;
    
    private Vector<DetailsAppendListener> detailsAppendListeners;
    private Vector<ProgressChangedListener> progressChangedListeners;
    private Vector<ImageUpdateListener> imageChangedListeners;
    
    private File ImageFile;
    private ImageProcessorImpl thisReff = this;
    public static boolean DETECT_ONLY_WALLS = false;
    public volatile static String SAVED_XML_PATH = null;
    
    public Representation imageRepresentation;
    
    
    
    public ImageProcessorImpl(File imageFile) throws FileNotFoundException {
    	
        this.imagePreProcessor = new ImagePreProcessorImpl(imageFile);
        this.detailsAppendListeners = new Vector<>();
        this.progressChangedListeners = new Vector<>();
        this.imageChangedListeners = new Vector<>();
    	this.ImageFile = imageFile;
    	
    }
    
    /**
     * @return the imagePreprocesor
     */
    public ImagePreProcessor getImagePreprocessor() {
        return imagePreProcessor;
    }

    /**
     * @param imagePreprocesor the imagePreprocesor to set
     */
    public void setImagePreprocessor(ImagePreProcessor imagePreprocessor) {
        this.imagePreProcessor = imagePreprocessor;
    }

    @Override
    public void process() 
    		throws 	ValidatingException, TruncatingException, ProcessingException {
    	
    	// here, order kindof matters
    	//// loads the opencv 249 library for features2d
		System.loadLibrary("opencv_java249");
		// loads the opencv 310 library for fillConvexPoly and all the others
		System.loadLibrary("opencv_java310");
		
		imageRepresentation = new Representation();
		
		
		// run template detection && line detection in another thread
		new Thread(){
			
			public void run() {
				
				// object detection
				if(!DETECT_ONLY_WALLS){
					DetectObject objectDetector = new DetectObject(ImageFile.getAbsolutePath(), thisReff);
			        objectDetector.detectAllObject();
		        }
		        
				// line detection
		        HoughLineDetection houghLineDetection = new HoughLineDetection(DetectObject.TEMPLATE_OUTPUT_PATH, thisReff);
		        List<Line> detectedWalls = houghLineDetection.detectLines();
		        imageRepresentation.populateWalls(detectedWalls);
		        
		        thisReff.appendDetail("Walls detected: " + detectedWalls.size());
		        for (Line line : detectedWalls) {
					thisReff.appendDetail("(" + (int)line.x1 + ", " + (int)line.y1 + ") --> " + "(" + (int)line.x2 + ", " + (int)line.y2 + ")");
				}
		        
		        //xml encode
		        thisReff.setProgress(0);
		        thisReff.appendDetail("Serializing the representation into 'Representation.xml'...");
				try {
					  
					SAVED_XML_PATH = "Representation.xml";
					XMLEncoder  myEncoder = new XMLEncoder(new FileOutputStream (SAVED_XML_PATH));
					myEncoder.writeObject(imageRepresentation);
					myEncoder.flush();
					myEncoder.close();

			        thisReff.setProgress(100);
			        thisReff.appendDetail("Finished serialization.");
			        
			        
			        // RUN THE Graph Module Algorithm
			        runGraphALgorithm();
			        
					
				} catch (FileNotFoundException e) {
					thisReff.appendDetail("FAILED!");
					e.printStackTrace();
				}
			};
			
		}.start();
		
    }
    
    public void runGraphALgorithm(){
    	
    	// HERE, RUNNING ALGORITHM
    	thisReff.appendDetail("Running the rooms graph detection...");
    	
        Algorithm algorithm = new Algorithm();// initialized and read data from the file
        List<Room> rooms = algorithm.getRoomList();
        
        if(rooms.size() == 0){
        	
        	thisReff.appendDetail("No rooms detected.");
        }
        else{
        	
        	thisReff.appendDetail("Rooms: ");
        	
			for(int i=0;i<rooms.size();i++){
				String strin = "Camera " + (i+1) + ": ";
				
				for(int j = 0; j < rooms.get(i).getParts().size(); j++){
					strin += rooms.get(i).getParts().get(j).toString() + "(" + 
				rooms.get(i).getParts().get(j).getStart().getX()+"," + 
				rooms.get(i).getParts().get(j).getStart().getY() + "-->" + 
				rooms.get(i).getParts().get(j).getEnd().getX()+"," + 
				rooms.get(i).getParts().get(j).getEnd().getY() + ") ";
					
					appendDetail(strin);
				}
				
			}
			
        }
        
        Connector c = new Connector(rooms);
		c.connect();
		algorithm.getStairsMatch();
		
		rooms = c.getRooms();
		
		Graph g = new Graph(rooms, c.getGraphEdges());
		GraphMain.graph = g;
		
		Serializer serializer = new Serializer(new File("Graph.xml"));
		try {
			serializer.saveXML(g);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		thisReff.appendDetail("Finished. Now you can close this window.");
    }

    
    public void updateImage(BufferedImage img)
    {
    	for(ImageUpdateListener listener: imageChangedListeners)
        {
            listener.onUpdatePerformed(new ImageUpdateAction(img));
        }
    }
    
    
    public void appendDetail(String detail)
    {
       for(DetailsAppendListener listener:detailsAppendListeners)
       {
           listener.onAppendPerformed(new DetailsAppendAction(detail + "\r\n"));
       }
    }
    
    public void setProgress(double value)
    {
       for(ProgressChangedListener listener:progressChangedListeners)
       {
           listener.onValueChanged(new ProgressChangedAction(value));
       }
    }

    @Override
    public void addProgressChangedListener(ProgressChangedListener listener) {
        this.progressChangedListeners.add(listener);
        this.getImagePreprocessor().addProgressChangedListener(listener);
    }

    @Override
    public void removeProgressChangedListener(ProgressChangedListener listener) {
        this.progressChangedListeners.remove(listener);
        this.getImagePreprocessor().removeProgressChangedListener(listener);
    }

    @Override
    public void addDetailsAppendListener(DetailsAppendListener listener) {
    	
       this.detailsAppendListeners.add(listener);
       this.getImagePreprocessor().addDetailsAppendListener(listener);
    }
    
    public void addImageChangeListers(ImageUpdateListener listener) {
    	
        this.imageChangedListeners.add(listener);
     }

    @Override
    public void removeDetailsAppendListener(DetailsAppendListener listener) {
       this.detailsAppendListeners.remove(listener);
       this.getImagePreprocessor().removeDetailsAppendListener(listener);
    }

    
}
