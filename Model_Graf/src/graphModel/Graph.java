package graphModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Graph {

    private List<Room> nodes;

    private List<GraphEdge> edges;




  public Graph(List<Room> nodes, List<GraphEdge> edges) {
    this.nodes = nodes;
    this.edges = edges;
  }
  public Graph(){
    this.nodes=new ArrayList<>();
    this.edges=new ArrayList<>();
  }

  public void addNode(Room r) {
    this.nodes.add(r);
  }

  public void deleteNode( Room r) {
    this.nodes.remove(r);
  }

  public void addEdge( GraphEdge e) {
    this.edges.add(e);
  }

  public void deleteEdge( GraphEdge e) {
    this.edges.remove(e);
  }

  public List<Room> getNodes() {
    return nodes;
  }

  public void setNodes(List<Room> nodes) {
    this.nodes = nodes;
  }

  public List<GraphEdge> getEdges() {
    return edges;
  }

  public void setEdges(List<GraphEdge> edges) {
    this.edges = edges;
  }

  public List getListOfRoomsString(){
    List<String> rooms=new ArrayList<>();
    for (Room r:this.nodes){
      rooms.add(r.toString());
    }
    return rooms;
  }

    public List<Room> bfs(Room rootNode)
    {
        List <Room> connectedPart=new ArrayList<>();
        Queue queue = new LinkedList();
        queue.add(rootNode);
        connectedPart.add(rootNode);
        rootNode.setVisited(true);
        while(!queue.isEmpty()) {
            Room node = (Room)queue.remove();
            Room child=null;
            while((child=node.getUnvisitedChildNode())!=null) {
                child.setVisited(true);
                connectedPart.add(child);
                queue.add(child);
            }
        }
        return connectedPart;

    }

    public int splitGraph(){
        int nrParts=0;
        List<Room> connectedPart= new ArrayList<>();
        connectedPart=bfs(this.nodes.get(0));
        List<Room> part=new ArrayList<>();
        nrParts++;
        if (connectedPart.size()!=this.nodes.size()){

            for (Room r:nodes){
                if (!r.isVisited()){
                    part.add(r);
                }
            }
            this.nodes=connectedPart;
            while(!part.isEmpty()){
                List<Room> newPart=bfs(part.get(0));
                nrParts++;
                Graph g=new Graph();
                g.setNodes(newPart);
                List <Room> roomL=new ArrayList<>();
                for (Room r:part){
                    if (r.isVisited()){
                        roomL.add(r);
                    }
                }
                for(Room r: roomL){
                    part.remove(r);
                }

            }
        }
        return nrParts;
    }


    public boolean splitRoom(Room r,int nrParts){
        this.nodes.remove(r);
        double intitialArea=r.area();
        double newArea=0;
        for (int i=0;i<nrParts;i++){
            Room newRoom= new Room(r.getName()+"_"+(i+1),r.getLevel());
            this.nodes.add(newRoom);
            //newArea=newArea+newRoom.area();
        }
        //if (intitialArea!=newArea) return  false;

        return true;
    }


}