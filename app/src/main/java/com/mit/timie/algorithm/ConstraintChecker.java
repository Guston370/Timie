package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Constraint;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Central constraint checker that validates all timetable constraints.
 * Validates Requirements 6.9, 13.1, and 13.2: Constraint checking and conflict reporting.
 */
public class ConstraintChecker {
    private final List<Constraint> constraints;
    
    /**
     * Initialize the constraint checker with all constraint implementations.
     * 
     * @param repository The timetable repository for accessing data
     * @param configRepository The config repository for accessing configuration
     */
    public ConstraintChecker(TimetableRepository repository, ConfigRepository configRepository) {
        constraints = new ArrayList<>();
        
        // Add all constraint implementations
        constraints.add(new NoFacultyDoubleBookingConstraint());
        constraints.add(new FacultyWorkloadConstraint(repository));
        constraints.add(new LabConsecutiveConstraint(repository, configRepository));
        constraints.add(new RoomDoubleBookingConstraint());
        constraints.add(new SubjectRepetitionConstraint(repository));
        constraints.add(new FacultyAvailabilityConstraint(repository));
        constraints.add(new BreakPeriodConstraint(configRepository));
    }
    
    /**
     * Check if all constraints are satisfied for the given assignment.
     * 
     * @param assignment The assignment to validate
     * @param timetable The current timetable state
     * @return true if all constraints are satisfied, false otherwise
     */
    public boolean checkAllConstraints(Assignment assignment, Timetable timetable) {
        if (assignment == null || timetable == null) {
            return false;
        }
        
        for (Constraint constraint : constraints) {
            if (!constraint.isSatisfied(assignment, timetable)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get a list of all constraint violations for the given assignment.
     * 
     * @param assignment The assignment to validate
     * @param timetable The current timetable state
     * @return List of violation messages for failed constraints
     */
    public List<String> getViolations(Assignment assignment, Timetable timetable) {
        List<String> violations = new ArrayList<>();
        
        if (assignment == null || timetable == null) {
            violations.add("Invalid assignment or timetable");
            return violations;
        }
        
        for (Constraint constraint : constraints) {
            if (!constraint.isSatisfied(assignment, timetable)) {
                violations.add(constraint.getViolationMessage());
            }
        }
        
        return violations;
    }
    
    /**
     * Get all registered constraints.
     * 
     * @return List of all constraints
     */
    public List<Constraint> getConstraints() {
        return new ArrayList<>(constraints);
    }
}
