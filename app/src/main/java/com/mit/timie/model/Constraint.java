package com.mit.timie.model;

/**
 * Interface for timetable constraints.
 * Each constraint validates a specific scheduling rule.
 */
public interface Constraint {
    /**
     * Check if the constraint is satisfied for the given assignment in the current timetable.
     * 
     * @param assignment The assignment to validate
     * @param currentTimetable The current state of the timetable
     * @return true if the constraint is satisfied, false otherwise
     */
    boolean isSatisfied(Assignment assignment, Timetable currentTimetable);
    
    /**
     * Get a human-readable message describing the constraint violation.
     * 
     * @return The violation message
     */
    String getViolationMessage();
}
