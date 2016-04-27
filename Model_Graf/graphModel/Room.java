package graphModel;

import buildingParts.BuildingPart;
import buildingParts.Coordinates;
import buildingParts.Door;
import buildingParts.Window;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private List<BuildingPart> parts;

    private List<Room> adjacentRooms;

    private String name;

    private List<Coordinates> corners;

    private String level;

    public Room(String name, String level) {
        this.name = name;
        this.level = level;
        this.parts = new ArrayList<>();
        this.adjacentRooms = new ArrayList<>();
        this.corners = new ArrayList<>();
    }

    public List<BuildingPart> getParts() {
        return parts;
    }

    public void setParts(List<BuildingPart> parts) {
        this.parts = parts;
    }

    public List<Room> getAdjacentRooms() {
        return adjacentRooms;
    }

    public void setAdjacentRooms(List<Room> adjacentRooms) {
        this.adjacentRooms = adjacentRooms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Coordinates> getCorners() {
        return corners;
    }

    public void setCorners(List<Coordinates> corners) {
        this.corners = corners;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void addBuildingPart(BuildingPart part) {
        this.parts.add(part);
    }

    public void deleteBuildingPart(BuildingPart part) {
        this.parts.remove(part);
    }

    public List<Door> getDoors() {
        List<Door> doors = new ArrayList<>();
        for (BuildingPart b : parts) {
            if (b instanceof Door) {
                doors.add((Door) b);
            }
        }
        return doors;
    }

    public List<Window> getWindows() {
        List<Window> doors = new ArrayList<>();
        for (BuildingPart b : parts) {
            if (b instanceof Door) {
                doors.add((Window) b);
            }
        }
        return doors;
    }

}