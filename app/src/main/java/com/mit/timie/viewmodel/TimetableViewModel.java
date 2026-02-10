package com.mit.timie.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mit.timie.algorithm.ConstraintChecker;
import com.mit.timie.model.Assignment;
import com.mit.timie.model.ConflictReport;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;

import java.util.List;

/**
 * ViewModel for managing timetable viewing and editing operations.
 * Handles manual edits with constraint validation.
 */
public class TimetableViewModel extends ViewModel {
    
    private final TimetableRepository repository;
    private final ConstraintChecker constraintChecker;
    
    private final MutableLiveData<Timetable> currentTimetable;
    private final MutableLiveData<ConflictReport> editConflicts;
    
    public TimetableViewModel() {
        this.repository = TimetableRepository.getInstance();
        ConfigRepository configRepository = ConfigRepository.getInstance();
        this.constraintChecker = new ConstraintChecker(repository, configRepository);
        
        this.currentTimetable = new MutableLiveData<>();
        this.editConflicts = new MutableLiveData<>();
    }
    
    /**
     * Sets the current timetable for viewing/editing.
     * 
     * @param timetable The timetable to set as current
     */
    public void setCurrentTimetable(Timetable timetable) {
        this.currentTimetable.setValue(timetable);
    }
    
    /**
     * Swaps two assignments after validating the swap doesn't violate constraints.
     * If validation fails, reverts the swap and posts a conflict report.
     * 
     * @param assignment1 The first assignment to swap
     * @param assignment2 The second assignment to swap
     */
    public void swapAssignments(Assignment assignment1, Assignment assignment2) {
        Timetable timetable = currentTimetable.getValue();
        
        if (timetable == null) {
            ConflictReport report = new ConflictReport();
            report.addConflict("No timetable is currently loaded");
            editConflicts.setValue(report);
            return;
        }
        
        // Store original time slots
        TimeSlot originalSlot1 = assignment1.getTimeSlot();
        TimeSlot originalSlot2 = assignment2.getTimeSlot();
        
        // Temporarily remove both assignments from timetable
        timetable.removeAssignment(assignment1);
        timetable.removeAssignment(assignment2);
        
        // Swap time slots
        assignment1.setTimeSlot(originalSlot2);
        assignment2.setTimeSlot(originalSlot1);
        
        // Validate both assignments with new time slots
        boolean valid1 = constraintChecker.checkAllConstraints(assignment1, timetable);
        boolean valid2 = constraintChecker.checkAllConstraints(assignment2, timetable);
        
        if (valid1 && valid2) {
            // Swap is valid - add assignments back with new slots
            timetable.addAssignment(assignment1);
            timetable.addAssignment(assignment2);
            
            // Notify observers
            currentTimetable.setValue(timetable);
            editConflicts.setValue(null);
        } else {
            // Swap is invalid - revert to original slots
            assignment1.setTimeSlot(originalSlot1);
            assignment2.setTimeSlot(originalSlot2);
            
            // Add assignments back with original slots
            timetable.addAssignment(assignment1);
            timetable.addAssignment(assignment2);
            
            // Create conflict report
            ConflictReport report = new ConflictReport();
            
            if (!valid1) {
                List<String> violations = constraintChecker.getViolations(assignment1, timetable);
                for (String violation : violations) {
                    report.addConflict("Assignment 1: " + violation);
                }
                report.addConflictingSlot(originalSlot2);
            }
            
            if (!valid2) {
                List<String> violations = constraintChecker.getViolations(assignment2, timetable);
                for (String violation : violations) {
                    report.addConflict("Assignment 2: " + violation);
                }
                report.addConflictingSlot(originalSlot1);
            }
            
            report.setSuggestion("The swap violates scheduling constraints. Try swapping with a different slot.");
            editConflicts.setValue(report);
        }
    }
    
    /**
     * Locks an assignment to prevent it from being modified during regeneration.
     * 
     * @param assignment The assignment to lock
     */
    public void lockAssignment(Assignment assignment) {
        if (assignment == null) {
            return;
        }
        
        assignment.setLocked(true);
        
        Timetable timetable = currentTimetable.getValue();
        if (timetable != null) {
            currentTimetable.setValue(timetable);
        }
    }
    
    /**
     * Unlocks an assignment to allow it to be modified during regeneration.
     * 
     * @param assignment The assignment to unlock
     */
    public void unlockAssignment(Assignment assignment) {
        if (assignment == null) {
            return;
        }
        
        assignment.setLocked(false);
        
        Timetable timetable = currentTimetable.getValue();
        if (timetable != null) {
            currentTimetable.setValue(timetable);
        }
    }
    
    /**
     * Exposes the current timetable as LiveData for UI observation.
     * 
     * @return LiveData containing the current timetable
     */
    public LiveData<Timetable> getCurrentTimetable() {
        return currentTimetable;
    }
    
    /**
     * Exposes edit conflicts as LiveData for UI observation.
     * 
     * @return LiveData containing conflict reports when edits fail validation
     */
    public LiveData<ConflictReport> getEditConflicts() {
        return editConflicts;
    }
    
    /**
     * Clears any current edit conflicts.
     */
    public void clearConflicts() {
        editConflicts.setValue(null);
    }
}
