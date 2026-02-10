package com.mit.timie.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Faculty {
    private String id;
    private String name;
    private List<String> subjectIds;
    private int maxPeriodsPerDay;
    private Map<Integer, List<Integer>> availability;
    private boolean avoidConsecutive;

    public Faculty() {
        this.subjectIds = new ArrayList<>();
        this.availability = new HashMap<>();
        this.avoidConsecutive = false;
    }

    public Faculty(String id, String name, List<String> subjectIds, int maxPeriodsPerDay, 
                   Map<Integer, List<Integer>> availability, boolean avoidConsecutive) {
        this.id = id;
        this.name = name;
        this.subjectIds = subjectIds != null ? new ArrayList<>(subjectIds) : new ArrayList<>();
        this.maxPeriodsPerDay = maxPeriodsPerDay;
        this.availability = availability != null ? new HashMap<>(availability) : new HashMap<>();
        this.avoidConsecutive = avoidConsecutive;
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

    public List<String> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(List<String> subjectIds) {
        this.subjectIds = subjectIds != null ? new ArrayList<>(subjectIds) : new ArrayList<>();
    }

    public int getMaxPeriodsPerDay() {
        return maxPeriodsPerDay;
    }

    public void setMaxPeriodsPerDay(int maxPeriodsPerDay) {
        this.maxPeriodsPerDay = maxPeriodsPerDay;
    }

    public Map<Integer, List<Integer>> getAvailability() {
        return availability;
    }

    public void setAvailability(Map<Integer, List<Integer>> availability) {
        this.availability = availability != null ? new HashMap<>(availability) : new HashMap<>();
    }

    public boolean isAvoidConsecutive() {
        return avoidConsecutive;
    }

    public void setAvoidConsecutive(boolean avoidConsecutive) {
        this.avoidConsecutive = avoidConsecutive;
    }

    public boolean validate() {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (subjectIds == null || subjectIds.isEmpty()) {
            return false;
        }
        if (maxPeriodsPerDay < 1 || maxPeriodsPerDay > 12) {
            return false;
        }
        if (!hasAtLeastOneAvailableSlot()) {
            return false;
        }
        return true;
    }

    public boolean hasAtLeastOneAvailableSlot() {
        if (availability == null || availability.isEmpty()) {
            return false;
        }
        for (List<Integer> periods : availability.values()) {
            if (periods != null && !periods.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public String getValidationError() {
        if (name == null || name.trim().isEmpty()) {
            return "Faculty name cannot be empty";
        }
        if (subjectIds == null || subjectIds.isEmpty()) {
            return "Faculty must be assigned at least one subject";
        }
        if (maxPeriodsPerDay < 1 || maxPeriodsPerDay > 12) {
            return "Max periods per day must be between 1 and 12";
        }
        if (!hasAtLeastOneAvailableSlot()) {
            return "Faculty must have at least one available time slot";
        }
        return null;
    }
}
