package com.mit.timie.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Timetable {
    private String id;
    private String variantName;
    private Map<String, Map<TimeSlot, Assignment>> classTimetables;
    private Map<String, Map<TimeSlot, Assignment>> facultyTimetables;
    private List<Assignment> allAssignments;

    public Timetable() {
        this.classTimetables = new HashMap<>();
        this.facultyTimetables = new HashMap<>();
        this.allAssignments = new ArrayList<>();
    }

    public Timetable(String id, String variantName) {
        this.id = id;
        this.variantName = variantName;
        this.classTimetables = new HashMap<>();
        this.facultyTimetables = new HashMap<>();
        this.allAssignments = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public Map<String, Map<TimeSlot, Assignment>> getClassTimetables() {
        return classTimetables;
    }

    public void setClassTimetables(Map<String, Map<TimeSlot, Assignment>> classTimetables) {
        this.classTimetables = classTimetables;
    }

    public Map<String, Map<TimeSlot, Assignment>> getFacultyTimetables() {
        return facultyTimetables;
    }

    public void setFacultyTimetables(Map<String, Map<TimeSlot, Assignment>> facultyTimetables) {
        this.facultyTimetables = facultyTimetables;
    }

    public List<Assignment> getAllAssignments() {
        return allAssignments;
    }

    public void setAllAssignments(List<Assignment> allAssignments) {
        this.allAssignments = allAssignments;
    }

    public void addAssignment(Assignment assignment) {
        if (assignment == null || assignment.getTimeSlot() == null) {
            return;
        }

        allAssignments.add(assignment);

        String classSectionId = assignment.getClassSectionId();
        if (classSectionId != null) {
            classTimetables.putIfAbsent(classSectionId, new HashMap<>());
            classTimetables.get(classSectionId).put(assignment.getTimeSlot(), assignment);
        }

        String facultyId = assignment.getFacultyId();
        if (facultyId != null) {
            facultyTimetables.putIfAbsent(facultyId, new HashMap<>());
            facultyTimetables.get(facultyId).put(assignment.getTimeSlot(), assignment);
        }
    }

    public void removeAssignment(Assignment assignment) {
        if (assignment == null || assignment.getTimeSlot() == null) {
            return;
        }

        allAssignments.remove(assignment);

        String classSectionId = assignment.getClassSectionId();
        if (classSectionId != null && classTimetables.containsKey(classSectionId)) {
            classTimetables.get(classSectionId).remove(assignment.getTimeSlot());
        }

        String facultyId = assignment.getFacultyId();
        if (facultyId != null && facultyTimetables.containsKey(facultyId)) {
            facultyTimetables.get(facultyId).remove(assignment.getTimeSlot());
        }
    }

    public Assignment getAssignment(String classSectionId, TimeSlot timeSlot) {
        if (classSectionId == null || timeSlot == null) {
            return null;
        }
        Map<TimeSlot, Assignment> classSchedule = classTimetables.get(classSectionId);
        if (classSchedule == null) {
            return null;
        }
        return classSchedule.get(timeSlot);
    }

    public Assignment getFacultyAssignment(String facultyId, TimeSlot timeSlot) {
        if (facultyId == null || timeSlot == null) {
            return null;
        }
        Map<TimeSlot, Assignment> facultySchedule = facultyTimetables.get(facultyId);
        if (facultySchedule == null) {
            return null;
        }
        return facultySchedule.get(timeSlot);
    }
}
