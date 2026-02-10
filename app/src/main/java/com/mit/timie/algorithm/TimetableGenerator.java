package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.ConflictReport;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TimetableGenerator orchestrates the generation of multiple timetable variants.
 * Validates Requirements 7.8, 18.1, 18.2, 18.3, 18.4: Variant generation.
 * Validates Requirements 12.1, 12.2, 12.3, 12.4: Selective regeneration with locked slots.
 * Validates Requirements 7.7, 7.9: Retry logic with shuffling.
 */
public class TimetableGenerator {
    
    private final BacktrackingSolver solver;
    private final TimetableRepository repository;
    private static final int MAX_RETRIES = 5;
    
    /**
     * Constructor with dependency injection.
     * 
     * @param repository The timetable repository
     * @param configRepository The configuration repository
     */
    public TimetableGenerator(TimetableRepository repository, ConfigRepository configRepository) {
        this.repository = repository;
        this.solver = new BacktrackingSolver(repository, configRepository);
    }
    
    /**
     * Generate three timetable variants using different optimization strategies.
     * Validates Requirements 7.8, 18.1, 18.2, 18.3, 18.4.
     * 
     * @return List of successfully generated timetables (up to 3 variants)
     */
    public List<Timetable> generateVariants() {
        List<Timetable> variants = new ArrayList<>();
        
        // Generate Balanced variant
        Timetable balanced = generateWithRetry(new BalancedStrategy(repository), "Balanced");
        if (balanced != null) {
            variants.add(balanced);
        }
        
        // Generate Compact variant
        Timetable compact = generateWithRetry(new CompactStrategy(repository), "Compact");
        if (compact != null) {
            variants.add(compact);
        }
        
        // Generate Faculty-friendly variant
        Timetable facultyFriendly = generateWithRetry(new FacultyFriendlyStrategy(repository), "Faculty-friendly");
        if (facultyFriendly != null) {
            variants.add(facultyFriendly);
        }
        
        return variants;
    }

    /**
     * Generate a timetable with retry logic and shuffling.
     * Validates Requirements 7.7, 7.9: Retry logic with shuffling.
     * 
     * @param strategy The heuristic strategy to use
     * @param variantName The name for this variant
     * @return A valid timetable, or null if all retries fail
     */
    private Timetable generateWithRetry(HeuristicStrategy strategy, String variantName) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            Timetable timetable = solver.solve(strategy);
            
            if (timetable != null) {
                timetable.setVariantName(variantName);
                return timetable;
            }
            
            // If generation failed and we have more retries, shuffle will happen
            // in the next iteration (the solver's generateAssignments creates a new list)
        }
        
        // All retries failed
        return null;
    }
    
    /**
     * Regenerate timetable with locked slots preserved.
     * Validates Requirements 12.1, 12.2, 12.3, 12.4: Selective regeneration.
     * 
     * @param existingTimetable The existing timetable with locked assignments
     * @return A regenerated timetable with locked slots preserved, or null if regeneration fails
     */
    public Timetable regenerateWithLockedSlots(Timetable existingTimetable) {
        if (existingTimetable == null) {
            return null;
        }
        
        // Create new timetable with locked assignments
        Timetable newTimetable = new Timetable();
        newTimetable.setVariantName(existingTimetable.getVariantName());
        
        // Copy locked assignments
        for (Assignment assignment : existingTimetable.getAllAssignments()) {
            if (assignment.isLocked()) {
                newTimetable.addAssignment(assignment);
            }
        }
        
        // Generate assignments for unlocked slots only
        List<Assignment> unlockedAssignments = solver.generateUnlockedAssignments(existingTimetable);
        
        // Try to solve with locked constraints using retry logic
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            // Create a copy of the timetable with locked assignments for this attempt
            Timetable attemptTimetable = copyTimetableWithLockedOnly(newTimetable);
            
            // Shuffle unlocked assignments for retry attempts
            if (attempt > 0) {
                Collections.shuffle(unlockedAssignments);
            }
            
            // Solve with fixed locked assignments
            Timetable result = solver.solveWithFixed(attemptTimetable, new ArrayList<>(unlockedAssignments));
            
            if (result != null) {
                return result;
            }
        }
        
        // All retries failed
        return null;
    }
    
    /**
     * Create a copy of timetable containing only locked assignments.
     * 
     * @param timetable The source timetable
     * @return A new timetable with only locked assignments
     */
    private Timetable copyTimetableWithLockedOnly(Timetable timetable) {
        Timetable copy = new Timetable();
        copy.setVariantName(timetable.getVariantName());
        
        for (Assignment assignment : timetable.getAllAssignments()) {
            if (assignment.isLocked()) {
                copy.addAssignment(assignment);
            }
        }
        
        return copy;
    }
    
    /**
     * Generate timetable variants and return conflict report if all fail.
     * Validates Requirements 7.9: Conflict reporting on generation failure.
     * 
     * @return A result containing either generated timetables or a conflict report
     */
    public GenerationResult generateVariantsWithConflictReport() {
        List<Timetable> variants = generateVariants();
        
        if (variants.isEmpty()) {
            ConflictReport report = new ConflictReport();
            report.addConflict("Unable to generate valid timetable with current constraints");
            report.setSuggestion("Try adjusting faculty availability, reducing subject weekly periods, or adding more rooms");
            return new GenerationResult(null, report);
        }
        
        return new GenerationResult(variants, null);
    }
    
    /**
     * Result class to hold either generated timetables or conflict report.
     */
    public static class GenerationResult {
        private final List<Timetable> timetables;
        private final ConflictReport conflictReport;
        
        public GenerationResult(List<Timetable> timetables, ConflictReport conflictReport) {
            this.timetables = timetables;
            this.conflictReport = conflictReport;
        }
        
        public List<Timetable> getTimetables() {
            return timetables;
        }
        
        public ConflictReport getConflictReport() {
            return conflictReport;
        }
        
        public boolean isSuccess() {
            return timetables != null && !timetables.isEmpty();
        }
    }
}
