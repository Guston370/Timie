package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Constraint;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.TimetableRepository;

import java.util.List;
import java.util.Map;

/**
 * Constraint that ensures faculty are only assigned during their available time slots.
 * Validates Requirements 6.8 and 3.5: Faculty availability constraint.
 */
public class FacultyAvailabilityConstraint implements Constraint {
    private final TimetableRepository repository;
    
    public FacultyAvailabilityConstraint(TimetableRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public boolean isSatisfied(Assignment assignment, Timetable currentTimetable) {
        if (assignment == null || assignment.getFacultyId() == null || assignment.getTimeSlot() == null) {
            return true;
        }
        
        Faculty faculty = repository.getFaculty(assignment.getFacultyId());
        if (faculty == null) {
            return false;
        }
        
        TimeSlot slot = assignment.getTimeSlot();
        Map<Integer, List<Integer>> availability = faculty.getAvailability();
        
        if (availability == null || !availability.containsKey(slot.getDay())) {
            return false;
        }
        
        List<Integer> availablePeriods = availability.get(slot.getDay());
        if (availablePeriods == null) {
            return false;
        }
        
        return availablePeriods.contains(slot.getPeriod());
    }
    
    @Override
    public String getViolationMessage() {
        return "Faculty is not available at this time slot";
    }
}
