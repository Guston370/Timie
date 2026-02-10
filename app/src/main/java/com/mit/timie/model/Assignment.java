package com.mit.timie.model;

public class Assignment {
    private String subjectId;
    private String facultyId;
    private String roomId;
    private String classSectionId;
    private TimeSlot timeSlot;
    private boolean locked;

    public Assignment() {
        this.locked = false;
    }

    public Assignment(String subjectId, String facultyId, String roomId, String classSectionId, TimeSlot timeSlot, boolean locked) {
        this.subjectId = subjectId;
        this.facultyId = facultyId;
        this.roomId = roomId;
        this.classSectionId = classSectionId;
        this.timeSlot = timeSlot;
        this.locked = locked;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getClassSectionId() {
        return classSectionId;
    }

    public void setClassSectionId(String classSectionId) {
        this.classSectionId = classSectionId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
