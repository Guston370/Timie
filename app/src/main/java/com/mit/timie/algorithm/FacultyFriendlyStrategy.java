package com.mit.timie.algorithm;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Subject;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Faculty-friendly strategy that respects faculty preferences.
 * Filters out slots adjacent to existing faculty assignments when avoidConsecutive is true.
 * Validates Requirements 18.3 and 3.6: Faculty-friendly scheduling.
 */
public class FacultyFriendlyStrategy implements HeuristicStrategy {
    
    private final TimetableRepository repository;
    private Timetable currentTimetable;
    
    public FacultyFriendlyStrategy(TimetableRepository repository) {
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
        
        if (currentTimetable == null || assignment.getFacultyId() == null) {
            return availableSlots.get(0);
        }
        
        Faculty faculty = repository.getFaculty(assignment.getFacultyId());
        if (faculty == null) {
            return availableSlots.get(0);
        }
        
        // If faculty wants to avoid consecutive periods, filter out adjacent slots
        if (faculty.isAvoidConsecutive()) {
            List<TimeSlot> nonConsecutiveSlots = new ArrayList<>();
            
            for (TimeSlot slot : availableSlots) {
                if (!hasAdjacentFacultyAssignment(slot, faculty.getId())) {
                    nonConsecutiveSlots.add(slot);
                }
            }
            
            // If we found non-consecutive slots, use them; otherwise fall back to all available
            if (!nonConsecutiveSlots.isEmpty()) {
                return nonConsecutiveSlots.get(0);
            }
        }
        
        return availableSlots.get(0);
    }
    
    /**
     * Check if a faculty member has an assignment adjacent to the given slot.
     * 
     * @param slot The time slot to check
     * @param facultyId The faculty ID
     * @return true if there's an adjacent assignment, false otherwise
     */
    private boolean hasAdjacentFacultyAssignment(TimeSlot slot, String facultyId) {
        if (slot == null || facultyId == null || currentTimetable == null) {
            return false;
        }
        
        Map<TimeSlot, Assignment> facultySchedule = currentTimetable.getFacultyTimetables().get(facultyId);
        if (facultySchedule == null) {
            return false;
        }
        
        // Check previous period
        TimeSlot previousSlot = new TimeSlot(slot.getDay(), slot.getPeriod() - 1);
        if (facultySchedule.containsKey(previousSlot)) {
            return true;
        }
        
        // Check next period
        TimeSlot nextSlot = new TimeSlot(slot.getDay(), slot.getPeriod() + 1);
        if (facultySchedule.containsKey(nextSlot)) {
            return true;
        }
        
        return false;
    }
}
