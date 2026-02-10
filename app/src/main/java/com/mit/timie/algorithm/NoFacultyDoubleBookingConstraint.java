package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Constraint;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;

import java.util.Map;

/**
 * Constraint that ensures no faculty member is assigned to teach two classes at the same time.
 * Validates Requirement 6.1: No faculty double-booking.
 */
public class NoFacultyDoubleBookingConstraint implements Constraint {
    
    @Override
    public boolean isSatisfied(Assignment assignment, Timetable currentTimetable) {
        if (assignment == null || assignment.getFacultyId() == null || assignment.getTimeSlot() == null) {
            return true;
        }
        
        String facultyId = assignment.getFacultyId();
        TimeSlot timeSlot = assignment.getTimeSlot();
        
        Map<TimeSlot, Assignment> facultySchedule = currentTimetable.getFacultyTimetables().get(facultyId);
        
        if (facultySchedule == null) {
            return true;
        }
        
        return !facultySchedule.containsKey(timeSlot);
    }
    
    @Override
    public String getViolationMessage() {
        return "Faculty is already assigned to teach another class at this time slot";
    }
}
