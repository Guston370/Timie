package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Constraint;
import com.mit.timie.model.Subject;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;

import java.util.Map;

/**
 * Constraint that ensures Lab subjects are allocated in consecutive periods.
 * Validates Requirements 6.4 and 2.7: Lab consecutive periods.
 */
public class LabConsecutiveConstraint implements Constraint {
    private final TimetableRepository repository;
    private final ConfigRepository configRepository;
    
    public LabConsecutiveConstraint(TimetableRepository repository, ConfigRepository configRepository) {
        this.repository = repository;
        this.configRepository = configRepository;
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
        
        // Only Lab subjects need consecutive periods
        if (subject.getType() != Subject.SubjectType.LAB) {
            return true;
        }
        
        TimeSlot currentSlot = assignment.getTimeSlot();
        TimeSlot nextSlot = new TimeSlot(currentSlot.getDay(), currentSlot.getPeriod() + 1);
        
        // Check if next period exists (not beyond periods per day)
        if (configRepository.getConfig() != null) {
            if (nextSlot.getPeriod() >= configRepository.getConfig().getPeriodsPerDay()) {
                return false; // Lab cannot be placed at last period
            }
        }
        
        // Check if next slot is available for the same class, faculty, and room
        return isSlotAvailable(nextSlot, assignment, currentTimetable);
    }
    
    private boolean isSlotAvailable(TimeSlot slot, Assignment assignment, Timetable timetable) {
        // Check if the slot is free for the class
        Map<TimeSlot, Assignment> classSchedule = timetable.getClassTimetables().get(assignment.getClassSectionId());
        if (classSchedule != null && classSchedule.containsKey(slot)) {
            return false;
        }
        
        // Check if the faculty is free at this slot
        Map<TimeSlot, Assignment> facultySchedule = timetable.getFacultyTimetables().get(assignment.getFacultyId());
        if (facultySchedule != null && facultySchedule.containsKey(slot)) {
            return false;
        }
        
        // Check if the room is free at this slot
        for (Assignment existing : timetable.getAllAssignments()) {
            if (existing.getRoomId() != null && existing.getRoomId().equals(assignment.getRoomId()) 
                && existing.getTimeSlot() != null && existing.getTimeSlot().equals(slot)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String getViolationMessage() {
        return "Lab subject requires consecutive periods, but next period is not available";
    }
}
