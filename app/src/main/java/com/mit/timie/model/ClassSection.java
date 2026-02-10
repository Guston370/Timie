package com.mit.timie.model;

public class ClassSection {
    private String id;
    private String className;
    private String sectionName;
    private int studentStrength;

    public ClassSection() {
    }

    public ClassSection(String id, String className, String sectionName, int studentStrength) {
        this.id = id;
        this.className = className;
        this.sectionName = sectionName;
        this.studentStrength = studentStrength;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getStudentStrength() {
        return studentStrength;
    }

    public void setStudentStrength(int studentStrength) {
        this.studentStrength = studentStrength;
    }

    public boolean validate() {
        if (className == null || className.trim().isEmpty()) {
            return false;
        }
        if (sectionName == null || sectionName.trim().isEmpty()) {
            return false;
        }
        if (studentStrength < 0) {
            return false;
        }
        return true;
    }

    public String getValidationError() {
        if (className == null || className.trim().isEmpty()) {
            return "Class name cannot be empty";
        }
        if (sectionName == null || sectionName.trim().isEmpty()) {
            return "Section name cannot be empty";
        }
        if (studentStrength < 0) {
            return "Student strength cannot be negative";
        }
        return null;
    }
}
