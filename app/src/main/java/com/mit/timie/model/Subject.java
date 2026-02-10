package com.mit.timie.model;

public class Subject {
    private String id;
    private String name;
    private int weeklyPeriods;
    private SubjectType type;
    private Priority priority;
    private boolean allowRepetition;

    public enum SubjectType {
        THEORY,
        LAB
    }

    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

    public Subject() {
        this.type = SubjectType.THEORY;
        this.priority = Priority.MEDIUM;
        this.allowRepetition = true;
    }

    public Subject(String id, String name, int weeklyPeriods, SubjectType type, Priority priority, boolean allowRepetition) {
        this.id = id;
        this.name = name;
        this.weeklyPeriods = weeklyPeriods;
        this.type = type;
        this.priority = priority;
        this.allowRepetition = allowRepetition;
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

    public int getWeeklyPeriods() {
        return weeklyPeriods;
    }

    public void setWeeklyPeriods(int weeklyPeriods) {
        this.weeklyPeriods = weeklyPeriods;
    }

    public SubjectType getType() {
        return type;
    }

    public void setType(SubjectType type) {
        this.type = type;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isAllowRepetition() {
        return allowRepetition;
    }

    public void setAllowRepetition(boolean allowRepetition) {
        this.allowRepetition = allowRepetition;
    }

    public boolean validate() {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (weeklyPeriods < 1 || weeklyPeriods > 30) {
            return false;
        }
        if (type == null) {
            return false;
        }
        if (priority == null) {
            return false;
        }
        return true;
    }

    public String getValidationError() {
        if (name == null || name.trim().isEmpty()) {
            return "Subject name cannot be empty";
        }
        if (weeklyPeriods < 1 || weeklyPeriods > 30) {
            return "Weekly periods must be between 1 and 30";
        }
        if (type == null) {
            return "Subject type must be selected";
        }
        if (priority == null) {
            return "Priority must be selected";
        }
        return null;
    }
}
