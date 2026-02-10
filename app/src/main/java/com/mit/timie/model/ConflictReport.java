package com.mit.timie.model;

import java.util.ArrayList;
import java.util.List;

public class ConflictReport {
    private List<String> conflicts;
    private List<TimeSlot> conflictingSlots;
    private String suggestion;

    public ConflictReport() {
        this.conflicts = new ArrayList<>();
        this.conflictingSlots = new ArrayList<>();
    }

    public ConflictReport(List<String> conflicts, List<TimeSlot> conflictingSlots, String suggestion) {
        this.conflicts = conflicts != null ? new ArrayList<>(conflicts) : new ArrayList<>();
        this.conflictingSlots = conflictingSlots != null ? new ArrayList<>(conflictingSlots) : new ArrayList<>();
        this.suggestion = suggestion;
    }

    public List<String> getConflicts() {
        return conflicts;
    }

    public void setConflicts(List<String> conflicts) {
        this.conflicts = conflicts != null ? new ArrayList<>(conflicts) : new ArrayList<>();
    }

    public List<TimeSlot> getConflictingSlots() {
        return conflictingSlots;
    }

    public void setConflictingSlots(List<TimeSlot> conflictingSlots) {
        this.conflictingSlots = conflictingSlots != null ? new ArrayList<>(conflictingSlots) : new ArrayList<>();
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public void addConflict(String conflict) {
        if (conflict != null && !conflict.trim().isEmpty()) {
            this.conflicts.add(conflict);
        }
    }

    public void addConflictingSlot(TimeSlot slot) {
        if (slot != null) {
            this.conflictingSlots.add(slot);
        }
    }

    public boolean hasConflicts() {
        return conflicts != null && !conflicts.isEmpty();
    }
}
