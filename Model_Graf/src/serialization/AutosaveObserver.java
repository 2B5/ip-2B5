package serialization;
import graphModel.Graph;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AutosaveObserver
{

 // private List<File> previousVersions;

  private Map<Integer,File> previousVersions;  //Am inlocuit cu un tree map, care tine intrarile sortate in functie de key, adica de Integer in cazul nostru
  private static int count=0; // de fiecare data cand salvam un fisier incrementam contorul si il bagam ca si cheie in tree map

  public FileChooser previousFileChooser;  //tine de logica GUI-ului, nu prea are treaba aici


  private Integer autosaveInterval; //Setati aici cum vreti voi, dar mi se pare prea mult asta si irelevant pana la urma

  private Object objectToBeSaved;  //Puneti aici ce obiect vreti sa serializati in loc de Object, duh

  public AutosaveObserver()
  {
    objectToBeSaved=new Object();
    previousVersions =new TreeMap<>();
  }

  public void save() throws IOException
  {
      File file=new File("version"+count+".xml");
      Serializer serializer=new Serializer(file);
      serializer.saveXML(objectToBeSaved);
      previousVersions.put(count,serializer.getFile());
      count++;
  }

  public void restorePrevious(File file) throws IOException
  {
      Deserializer deserializer=new Deserializer(file);
      deserializer.setFile(previousVersions.get(previousVersions.size()-1));
      deserializer.loadXML();
  }

  public void restorePrevious(Integer versionID) throws IOException
  {

      for(Map.Entry<Integer,File> entry: previousVersions.entrySet())
      {
          if(entry.getKey().equals(versionID))
          {
              Deserializer deserializer=new Deserializer(entry.getValue());
              deserializer.setFile(previousVersions.get(versionID));
              deserializer.loadXML();
          }
      }
  }

  public Integer getAutosaveInterval() {
  return null;
  }

  public Boolean setAutosaveInterval(Integer interval) {
  return null;
  }
}