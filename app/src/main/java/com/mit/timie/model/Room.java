package com.mit.timie.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    private String id;
    private String name;
    private RoomType type;
    private Map<Integer, List<Integer>> availability;

    public enum RoomType {
        CLASSROOM,
        LAB
    }

    public Room() {
        this.type = RoomType.CLASSROOM;
        this.availability = new HashMap<>();
    }

    public Room(String id, String name, RoomType type, Map<Integer, List<Integer>> availability) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.availability = availability != null ? new HashMap<>(availability) : new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public Map<Integer, List<Integer>> getAvailability() {
        return availability;
    }

    public void setAvailability(Map<Integer, List<Integer>> availability) {
        this.availability = availability != null ? new HashMap<>(availability) : new HashMap<>();
    }

    public boolean validate() {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (type == null) {
            return false;
        }
        return true;
    }

    public String getValidationError() {
        if (name == null || name.trim().isEmpty()) {
            return "Room name cannot be empty";
        }
        if (type == null) {
            return "Room type must be selected";
        }
        return null;
    }
}
