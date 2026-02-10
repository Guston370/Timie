package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Constraint;
import com.mit.timie.model.Subject;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.TimetableRepository;

import java.util.Map;

/**
 * Constraint that prevents same-day repetition of subjects when not allowed.
 * Validates Requirements 6.7 and 2.6: Subject repetition constraint.
 */
public class SubjectRepetitionConstraint implements Constraint {
    private final TimetableRepository repository;
    
    public SubjectRepetitionConstraint(TimetableRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public boolean isSatisfied(Assignment assignment, Timetable currentTimetable) {
        if (assignment == null || assignment.getSubjectId() == null || assignment.getTimeSlot() == null) {
            return true;
        }
        
        Subject subject = repository.getSubject(assignment.getSubjectId());
        if (subject == null) {
            return false;
        }
        
        // If repetition is allowed, constraint is always satisfied
        if (subject.isAllowRepetition()) {
            return true;
        }
        
        // Check if subject is already scheduled for this class on the same day
        int day = assignment.getTimeSlot().getDay();
        String classSectionId = assignment.getClassSectionId();
        
        Map<TimeSlot, Assignment> classSchedule = currentTimetable.getClassTimetables().get(classSectionId);
        
        if (classSchedule == null) {
            return true;
        }
        
        for (Map.Entry<TimeSlot, Assignment> entry : classSchedule.entrySet()) {
            TimeSlot slot = entry.getKey();
            Assignment existingAssignment = entry.getValue();
            
            if (slot.getDay() == day && existingAssignment.getSubjectId().equals(assignment.getSubjectId())) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String getViolationMessage() {
        return "Subject does not allow same-day repetition, but is already scheduled for this class today";
    }
}
