package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Subject;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Compact strategy that minimizes gaps between periods.
 * Prefers slots that are adjacent to existing assignments on the same day.
 * Validates Requirements 18.2: Compact schedule generation.
 */
public class CompactStrategy implements HeuristicStrategy {
    
    private final TimetableRepository repository;
    private Timetable currentTimetable;
    
    public CompactStrategy(TimetableRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Set the current timetable for slot selection calculations.
     * 
     * @param timetable The current timetable state
     */
    public void setCurrentTimetable(Timetable timetable) {
        this.currentTimetable = timetable;
    }
    
    @Override
    public List<Assignment> sortAssignments(List<Assignment> assignments) {
        if (assignments == null) {
            return new ArrayList<>();
        }
        
        List<Assignment> sorted = new ArrayList<>(assignments);
        
        // Sort by labs first, then by priority
        sorted.sort(new Comparator<Assignment>() {
            @Override
            public int compare(Assignment a1, Assignment a2) {
                Subject s1 = repository.getSubject(a1.getSubjectId());
                Subject s2 = repository.getSubject(a2.getSubjectId());
                
                if (s1 == null || s2 == null) {
                    return 0;
                }
                
                // Labs first
                if (s1.getType() == Subject.SubjectType.LAB && s2.getType() != Subject.SubjectType.LAB) {
                    return -1;
                }
                if (s1.getType() != Subject.SubjectType.LAB && s2.getType() == Subject.SubjectType.LAB) {
                    return 1;
                }
                
                // Then by priority
                return comparePriority(s1.getPriority(), s2.getPriority());
            }
            
            private int comparePriority(Subject.Priority p1, Subject.Priority p2) {
                int rank1 = getPriorityRank(p1);
                int rank2 = getPriorityRank(p2);
                return Integer.compare(rank1, rank2);
            }
            
            private int getPriorityRank(Subject.Priority priority) {
                switch (priority) {
                    case HIGH:
                        return 1;
                    case MEDIUM:
                        return 2;
                    case LOW:
                        return 3;
                    default:
                        return 4;
                }
            }
        });
        
        return sorted;
    }
    
    @Override
    public TimeSlot selectBestSlot(Assignment assignment, List<TimeSlot> availableSlots) {
        if (availableSlots == null || availableSlots.isEmpty()) {
            return null;
        }
        
        if (currentTimetable == null) {
            return availableSlots.get(0);
        }
        
        // Prefer slots adjacent to existing assignments to minimize gaps
        TimeSlot bestSlot = null;
        int bestScore = -1;
        
        for (TimeSlot slot : availableSlots) {
            int score = calculateCompactnessScore(slot, assignment.getClassSectionId());
            if (score > bestScore) {
                bestScore = score;
                bestSlot = slot;
            }
        }
        
        return bestSlot != null ? bestSlot : availableSlots.get(0);
    }
    
    /**
     * Calculate compactness score for a time slot.
     * Higher score means more adjacent assignments (more compact).
     * 
     * @param slot The time slot to evaluate
     * @param classSectionId The class section ID
     * @return Compactness score
     */
    private int calculateCompactnessScore(TimeSlot slot, String classSectionId) {
        if (slot == null || classSectionId == null || currentTimetable == null) {
            return 0;
        }
        
        Map<TimeSlot, Assignment> classSchedule = currentTimetable.getClassTimetables().get(classSectionId);
        if (classSchedule == null || classSchedule.isEmpty()) {
            return 0;
        }
        
        int score = 0;
        
        // Check if there's an assignment on the same day
        boolean hasSameDayAssignment = false;
        for (TimeSlot existingSlot : classSchedule.keySet()) {
            if (existingSlot.getDay() == slot.getDay()) {
                hasSameDayAssignment = true;
                break;
            }
        }
        
        if (hasSameDayAssignment) {
            score += 10; // Prefer days with existing assignments
        }
        
        // Check for adjacent periods (before or after)
        TimeSlot previousSlot = new TimeSlot(slot.getDay(), slot.getPeriod() - 1);
        TimeSlot nextSlot = new TimeSlot(slot.getDay(), slot.getPeriod() + 1);
        
        if (classSchedule.containsKey(previousSlot)) {
            score += 5; // Adjacent to previous period
        }
        
        if (classSchedule.containsKey(nextSlot)) {
            score += 5; // Adjacent to next period
        }
        
        return score;
    }
}
