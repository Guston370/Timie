package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Subject;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Balanced strategy that distributes subjects evenly across days.
 * Prioritizes labs first, then by subject priority (HIGH > MEDIUM > LOW).
 * Validates Requirements 7.3, 7.4, and 18.1: Balanced distribution strategy.
 */
public class BalancedStrategy implements HeuristicStrategy {
    
    private final TimetableRepository repository;
    private Timetable currentTimetable;
    
    public BalancedStrategy(TimetableRepository repository) {
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
                
                // Then by priority (HIGH > MEDIUM > LOW)
                int priorityCompare = comparePriority(s1.getPriority(), s2.getPriority());
                if (priorityCompare != 0) {
                    return priorityCompare;
                }
                
                return 0;
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
        
        // Calculate day load for the class section
        Map<Integer, Integer> dayLoad = calculateDayLoad(assignment.getClassSectionId());
        
        // Select slot with minimum day load to balance distribution
        TimeSlot bestSlot = null;
        int minLoad = Integer.MAX_VALUE;
        
        for (TimeSlot slot : availableSlots) {
            int load = dayLoad.getOrDefault(slot.getDay(), 0);
            if (load < minLoad) {
                minLoad = load;
                bestSlot = slot;
            }
        }
        
        return bestSlot != null ? bestSlot : availableSlots.get(0);
    }
    
    /**
     * Calculate the number of assignments per day for a class section.
     * 
     * @param classSectionId The class section ID
     * @return Map of day to assignment count
     */
    private Map<Integer, Integer> calculateDayLoad(String classSectionId) {
        Map<Integer, Integer> dayLoad = new HashMap<>();
        
        if (classSectionId == null || currentTimetable == null) {
            return dayLoad;
        }
        
        Map<TimeSlot, Assignment> classSchedule = currentTimetable.getClassTimetables().get(classSectionId);
        if (classSchedule == null) {
            return dayLoad;
        }
        
        for (TimeSlot slot : classSchedule.keySet()) {
            int day = slot.getDay();
            dayLoad.put(day, dayLoad.getOrDefault(day, 0) + 1);
        }
        
        return dayLoad;
    }
}
