package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Constraint;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.TimetableRepository;

import java.util.Map;

/**
 * Constraint that ensures faculty workload does not exceed their maximum periods per day.
 * Validates Requirement 6.2: Faculty workload limits.
 */
public class FacultyWorkloadConstraint implements Constraint {
    private final TimetableRepository repository;
    
    public FacultyWorkloadConstraint(TimetableRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public boolean isSatisfied(Assignment assignment, Timetable currentTimetable) {
        if (assignment == null || assignment.getFacultyId() == null || assignment.getTimeSlot() == null) {
            return true;
        }
        
        String facultyId = assignment.getFacultyId();
        int day = assignment.getTimeSlot().getDay();
        
        Faculty faculty = repository.getFaculty(facultyId);
        if (faculty == null) {
            return false;
        }
        
        Map<TimeSlot, Assignment> facultySchedule = currentTimetable.getFacultyTimetables().get(facultyId);
        
        if (facultySchedule == null) {
            return true;
        }
        
        long periodsToday = facultySchedule.keySet().stream()
            .filter(slot -> slot.getDay() == day)
            .count();
        
        return periodsToday < faculty.getMaxPeriodsPerDay();
    }
    
    @Override
    public String getViolationMessage() {
        return "Faculty workload exceeds maximum periods per day";
    }
}
