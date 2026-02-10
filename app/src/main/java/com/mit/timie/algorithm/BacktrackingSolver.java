package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.ClassSection;
import com.mit.timie.model.Config;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Room;
import com.mit.timie.model.Subject;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Backtracking solver for timetable generation using constraint satisfaction.
 * Implements recursive backtracking with constraint checking at each step.
 * Validates Requirements 6.9, 7.1, 7.2, 7.6, 7.7, and 15.7: Backtracking algorithm.
 */
public class BacktrackingSolver {
    
    private final ConstraintChecker constraintChecker;
    private final TimetableRepository repository;
    private final ConfigRepository configRepository;
    private HeuristicStrategy strategy;
    private Config config;
    
    public BacktrackingSolver(TimetableRepository repository, ConfigRepository configRepository) {
        this.repository = repository;
        this.configRepository = configRepository;
        this.constraintChecker = new ConstraintChecker(repository, configRepository);
        this.config = configRepository.getConfig();
    }
    
    /**
     * Solve the timetable generation problem using the given heuristic strategy.
     * 
     * @param strategy The heuristic strategy to use
     * @return A valid timetable, or null if no solution found
     */
    public Timetable solve(HeuristicStrategy strategy) {
        if (strategy == null) {
            return null;
        }
        
        this.strategy = strategy;
        this.config = configRepository.getConfig();
        
        if (config == null) {
            return null;
        }
        
        // Initialize empty timetable
        Timetable timetable = new Timetable();
        
        // Get all assignments to place
        List<Assignment> assignmentsToPlace = generateAssignments();
        
        if (assignmentsToPlace.isEmpty()) {
            return timetable;
        }
        
        // Sort by strategy heuristic
        assignmentsToPlace = strategy.sortAssignments(assignmentsToPlace);
        
        // Update strategy with current timetable for slot selection
        if (strategy instanceof BalancedStrategy) {
            ((BalancedStrategy) strategy).setCurrentTimetable(timetable);
        } else if (strategy instanceof CompactStrategy) {
            ((CompactStrategy) strategy).setCurrentTimetable(timetable);
        } else if (strategy instanceof FacultyFriendlyStrategy) {
            ((FacultyFriendlyStrategy) strategy).setCurrentTimetable(timetable);
        }
        
        // Backtracking search
        if (backtrack(timetable, assignmentsToPlace, 0)) {
            return timetable;
        }
        
        return null;
    }
    
    /**
     * Recursive backtracking method to place assignments.
     * 
     * @param timetable The current timetable state
     * @param assignments The list of assignments to place
     * @param index The current assignment index
     * @return true if all assignments placed successfully, false otherwise
     */
    private boolean backtrack(Timetable timetable, List<Assignment> assignments, int index) {
        // Base case: all assignments placed
        if (index >= assignments.size()) {
            return true;
        }
        
        Assignment assignment = assignments.get(index);
        
        // Get all possible time slots
        List<TimeSlot> possibleSlots = getAllPossibleSlots();
        
        // Use strategy to select best slots first
        if (strategy != null) {
            TimeSlot bestSlot = strategy.selectBestSlot(assignment, possibleSlots);
            if (bestSlot != null) {
                // Move best slot to front
                possibleSlots.remove(bestSlot);
                possibleSlots.add(0, bestSlot);
            }
        }
        
        // Try each possible time slot
        for (TimeSlot slot : possibleSlots) {
            assignment.setTimeSlot(slot);
            
            // Check if assignment satisfies all constraints
            if (constraintChecker.checkAllConstraints(assignment, timetable)) {
                // Place assignment
                timetable.addAssignment(assignment);
                
                // Update strategy with current timetable
                updateStrategyTimetable(timetable);
                
                // Recurse to next assignment
                if (backtrack(timetable, assignments, index + 1)) {
                    return true;
                }
                
                // Backtrack: remove assignment
                timetable.removeAssignment(assignment);
                updateStrategyTimetable(timetable);
            }
        }
        
        // No valid slot found for this assignment
        return false;
    }
    
    /**
     * Generate all required assignments from subjects and classes.
     * 
     * @return List of assignments to place
     */
    private List<Assignment> generateAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        
        List<ClassSection> classSections = repository.getAllClassSections();
        List<Subject> subjects = repository.getAllSubjects();
        
        if (classSections.isEmpty() || subjects.isEmpty()) {
            return assignments;
        }
        
        for (ClassSection classSection : classSections) {
            for (Subject subject : subjects) {
                // Create assignments for each required period
                int periodsNeeded = subject.getWeeklyPeriods();
                
                // For lab subjects, create pairs of consecutive assignments
                if (subject.getType() == Subject.SubjectType.LAB) {
                    // Labs need consecutive periods, so create assignments in pairs
                    int labSessions = (periodsNeeded + 1) / 2; // Round up for odd numbers
                    
                    for (int i = 0; i < labSessions; i++) {
                        Assignment assignment = new Assignment();
                        assignment.setSubjectId(subject.getId());
                        assignment.setClassSectionId(classSection.getId());
                        
                        // Find faculty who can teach this subject
                        Faculty faculty = findAvailableFaculty(subject);
                        if (faculty != null) {
                            assignment.setFacultyId(faculty.getId());
                        }
                        
                        // Find appropriate room
                        Room room = findAppropriateRoom(subject);
                        if (room != null) {
                            assignment.setRoomId(room.getId());
                        }
                        
                        assignments.add(assignment);
                    }
                } else {
                    // Theory subjects: create individual assignments
                    for (int i = 0; i < periodsNeeded; i++) {
                        Assignment assignment = new Assignment();
                        assignment.setSubjectId(subject.getId());
                        assignment.setClassSectionId(classSection.getId());
                        
                        // Find faculty who can teach this subject
                        Faculty faculty = findAvailableFaculty(subject);
                        if (faculty != null) {
                            assignment.setFacultyId(faculty.getId());
                        }
                        
                        // Find appropriate room
                        Room room = findAppropriateRoom(subject);
                        if (room != null) {
                            assignment.setRoomId(room.getId());
                        }
                        
                        assignments.add(assignment);
                    }
                }
            }
        }
        
        return assignments;
    }
    
    /**
     * Get all possible time slots (excluding break periods).
     * 
     * @return List of valid time slots
     */
    private List<TimeSlot> getAllPossibleSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        
        if (config == null) {
            return slots;
        }
        
        List<Integer> breakPeriods = config.getBreakPeriods();
        if (breakPeriods == null) {
            breakPeriods = new ArrayList<>();
        }
        
        for (int day = 0; day < config.getWorkingDays(); day++) {
            for (int period = 0; period < config.getPeriodsPerDay(); period++) {
                // Skip break periods
                if (!breakPeriods.contains(period)) {
                    slots.add(new TimeSlot(day, period));
                }
            }
        }
        
        return slots;
    }
    
    /**
     * Find an available faculty member who can teach the given subject.
     * 
     * @param subject The subject to teach
     * @return A faculty member who can teach the subject, or null if none found
     */
    private Faculty findAvailableFaculty(Subject subject) {
        if (subject == null) {
            return null;
        }
        
        List<Faculty> allFaculties = repository.getAllFaculties();
        
        for (Faculty faculty : allFaculties) {
            if (faculty.getSubjectIds().contains(subject.getId())) {
                return faculty;
            }
        }
        
        return null;
    }
    
    /**
     * Find an appropriate room for the given subject.
     * Matches room type to subject type (LAB subjects need LAB rooms).
     * 
     * @param subject The subject
     * @return An appropriate room, or null if none found
     */
    private Room findAppropriateRoom(Subject subject) {
        if (subject == null) {
            return null;
        }
        
        List<Room> allRooms = repository.getAllRooms();
        
        // For lab subjects, prefer lab rooms
        if (subject.getType() == Subject.SubjectType.LAB) {
            for (Room room : allRooms) {
                if (room.getType() == Room.RoomType.LAB) {
                    return room;
                }
            }
        }
        
        // For theory subjects or if no lab room found, use any classroom
        for (Room room : allRooms) {
            if (room.getType() == Room.RoomType.CLASSROOM) {
                return room;
            }
        }
        
        // If no specific room type found, return first available
        if (!allRooms.isEmpty()) {
            return allRooms.get(0);
        }
        
        return null;
    }
    
    /**
     * Update the strategy's current timetable reference.
     * 
     * @param timetable The current timetable
     */
    private void updateStrategyTimetable(Timetable timetable) {
        if (strategy instanceof BalancedStrategy) {
            ((BalancedStrategy) strategy).setCurrentTimetable(timetable);
        } else if (strategy instanceof CompactStrategy) {
            ((CompactStrategy) strategy).setCurrentTimetable(timetable);
        } else if (strategy instanceof FacultyFriendlyStrategy) {
            ((FacultyFriendlyStrategy) strategy).setCurrentTimetable(timetable);
        }
    }
    
    /**
     * Solve with fixed (locked) assignments.
     * Treats locked assignments as fixed constraints and only generates unlocked assignments.
     * Validates Requirements 12.1, 12.2, and 12.3: Selective regeneration with locked slots.
     * 
     * @param existingTimetable Timetable with locked assignments already placed
     * @param unlockedAssignments List of assignments to place (unlocked only)
     * @return A valid timetable with locked and unlocked assignments, or null if no solution found
     */
    public Timetable solveWithFixed(Timetable existingTimetable, List<Assignment> unlockedAssignments) {
        if (existingTimetable == null || unlockedAssignments == null) {
            return null;
        }
        
        this.config = configRepository.getConfig();
        
        if (config == null) {
            return null;
        }
        
        // Use default balanced strategy if none set
        if (strategy == null) {
            strategy = new BalancedStrategy(repository);
        }
        
        // Sort unlocked assignments by strategy heuristic
        List<Assignment> sortedUnlocked = strategy.sortAssignments(unlockedAssignments);
        
        // Update strategy with existing timetable
        updateStrategyTimetable(existingTimetable);
        
        // Backtracking search for unlocked assignments only
        if (backtrack(existingTimetable, sortedUnlocked, 0)) {
            return existingTimetable;
        }
        
        return null;
    }
    
    /**
     * Generate only unlocked assignments based on existing timetable.
     * Compares required assignments with existing locked assignments.
     * 
     * @param existingTimetable The existing timetable with locked assignments
     * @return List of unlocked assignments to place
     */
    public List<Assignment> generateUnlockedAssignments(Timetable existingTimetable) {
        if (existingTimetable == null) {
            return new ArrayList<>();
        }
        
        // Get all required assignments
        List<Assignment> allRequired = generateAssignments();
        
        // Get locked assignments
        List<Assignment> locked = new ArrayList<>();
        for (Assignment assignment : existingTimetable.getAllAssignments()) {
            if (assignment.isLocked()) {
                locked.add(assignment);
            }
        }
        
        // Count locked assignments per subject per class
        List<Assignment> unlocked = new ArrayList<>();
        
        for (Assignment required : allRequired) {
            // Check if this assignment is already covered by a locked assignment
            boolean isCovered = false;
            
            for (Assignment lockedAssignment : locked) {
                if (lockedAssignment.getSubjectId().equals(required.getSubjectId()) &&
                    lockedAssignment.getClassSectionId().equals(required.getClassSectionId())) {
                    isCovered = true;
                    locked.remove(lockedAssignment); // Remove to avoid double-counting
                    break;
                }
            }
            
            if (!isCovered) {
                unlocked.add(required);
            }
        }
        
        return unlocked;
    }
}
