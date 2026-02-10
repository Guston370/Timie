package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Config;
import com.mit.timie.model.Constraint;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;

import java.util.List;

/**
 * Constraint that ensures assignments are not placed in break periods.
 * Validates Requirement 6.5: Break periods unassigned.
 */
public class BreakPeriodConstraint implements Constraint {
    private final ConfigRepository configRepository;
    
    public BreakPeriodConstraint(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }
    
    @Override
    public boolean isSatisfied(Assignment assignment, Timetable currentTimetable) {
        if (assignment == null || assignment.getTimeSlot() == null) {
            return true;
        }
        
        Config config = configRepository.getConfig();
        if (config == null) {
            return true;
        }
        
        List<Integer> breakPeriods = config.getBreakPeriods();
        if (breakPeriods == null || breakPeriods.isEmpty()) {
            return true;
        }
        
        int period = assignment.getTimeSlot().getPeriod();
        
        return !breakPeriods.contains(period);
    }
    
    @Override
    public String getViolationMessage() {
        return "Cannot assign teaching period during a break period";
    }
}
