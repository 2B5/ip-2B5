package graphModel;

import buildingParts.Door;

import java.util.List;

public class GraphEdge {

  public List<Room> nodes;

  public Door door;

  public Door getDoor() {
    return door;
  }

  public void setDoor(Door door) {
    this.door = door;
  }

  public List<Room> getNodes() {
    return nodes;
  }

  public void setNodes(List<Room> nodes) {
    this.nodes = nodes;
  }
}