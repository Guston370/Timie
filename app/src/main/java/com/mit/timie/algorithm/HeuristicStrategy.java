package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.TimeSlot;

import java.util.List;

/**
 * Interface for heuristic strategies used in timetable generation.
 * Different strategies optimize for different goals (balanced, compact, faculty-friendly).
 * Validates Requirements 7.3, 7.4, 7.5, and 18.1: Heuristic-based timetable generation.
 */
public interface HeuristicStrategy {
    
    /**
     * Sort assignments based on the strategy's heuristic.
     * For example, prioritize labs first, then by subject priority.
     * 
     * @param assignments The list of assignments to sort
     * @return The sorted list of assignments
     */
    List<Assignment> sortAssignments(List<Assignment> assignments);
    
    /**
     * Select the best time slot for an assignment based on the strategy's heuristic.
     * For example, select slots that balance distribution or minimize gaps.
     * 
     * @param assignment The assignment to place
     * @param availableSlots The list of available time slots
     * @return The best time slot according to the strategy, or null if no slots available
     */
    TimeSlot selectBestSlot(Assignment assignment, List<TimeSlot> availableSlots);
}
