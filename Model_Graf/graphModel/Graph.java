package graphModel;

import java.util.ArrayList;
import java.util.List;

public class Graph {

  public List<Room> nodes;

  public List<GraphEdge> edges;

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

}