package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Constraint;
import com.mit.timie.model.Timetable;

/**
 * Constraint that ensures no room is double-booked at the same time slot.
 * Validates Requirement 6.6: No room double-booking.
 */
public class RoomDoubleBookingConstraint implements Constraint {
    
    @Override
    public boolean isSatisfied(Assignment assignment, Timetable currentTimetable) {
        if (assignment == null || assignment.getRoomId() == null || assignment.getTimeSlot() == null) {
            return true;
        }
        
        String roomId = assignment.getRoomId();
        
        for (Assignment existing : currentTimetable.getAllAssignments()) {
            if (existing.getRoomId() != null && existing.getRoomId().equals(roomId) 
                && existing.getTimeSlot() != null && existing.getTimeSlot().equals(assignment.getTimeSlot())) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String getViolationMessage() {
        return "Room is already booked for another class at this time slot";
    }
}
