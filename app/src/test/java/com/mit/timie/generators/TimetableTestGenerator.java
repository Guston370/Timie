package com.mit.timie.generators;

import com.mit.timie.model.Assignment;
import com.mit.timie.model.ClassSection;
import com.mit.timie.model.Config;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Room;
import com.mit.timie.model.Subject;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Generator for creating random valid Timetable objects for property-based testing.
 * This generator creates timetables that satisfy basic constraints to enable testing
 * of timetable properties.
 */
public class TimetableTestGenerator extends Generator<Timetable> {

    public TimetableTestGenerator() {
        super(Timetable.class);
    }

    @Override
    public Timetable generate(SourceOfRandomness random, GenerationStatus status) {
        // Generate a simple configuration
        Config config = generateSimpleConfig(random);
        
        // Generate subjects, faculty, rooms, and class sections
        List<Subject> subjects = generateSubjects(random, 2, 5);
        List<Faculty> faculties = generateFaculties(random, subjects, 2, 4);
        List<Room> rooms = generateRooms(random, 2, 4);
        List<ClassSection> classSections = generateClassSections(random, 1, 3);
        
        // Create timetable
        Timetable timetable = new Timetable(UUID.randomUUID().toString(), "Test");
        
        // Generate assignments that satisfy basic constraints
        generateValidAssignments(random, timetable, config, subjects, faculties, rooms, classSections);
        
        return timetable;
    }

    private Config generateSimpleConfig(SourceOfRandomness random) {
        int workingDays = random.nextInt(3, 6); // 3-5 days
        List<String> dayNames = new ArrayList<>();
        String[] names = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < workingDays; i++) {
            dayNames.add(names[i]);
        }
        
        int periodsPerDay = random.nextInt(4, 9); // 4-8 periods
        int periodDuration = 45;
        
        // Generate 0-1 break periods
        List<Integer> breakPeriods = new ArrayList<>();
        if (periodsPerDay > 2 && random.nextBoolean()) {
            breakPeriods.add(random.nextInt(1, periodsPerDay - 1));
        }
        
        return new Config(workingDays, dayNames, periodsPerDay, periodDuration, breakPeriods);
    }

    private List<Subject> generateSubjects(SourceOfRandomness random, int min, int max) {
        List<Subject> subjects = new ArrayList<>();
        int count = random.nextInt(min, max + 1);
        
        String[] names = {"Math", "Physics", "Chemistry", "English", "History"};
        for (int i = 0; i < count && i < names.length; i++) {
            String id = "subject-" + i;
            Subject.SubjectType type = random.nextBoolean() ? Subject.SubjectType.THEORY : Subject.SubjectType.LAB;
            Subject.Priority priority = Subject.Priority.MEDIUM;
            int weeklyPeriods = random.nextInt(2, 6); // 2-5 periods per week
            
            subjects.add(new Subject(id, names[i], weeklyPeriods, type, priority, true));
        }
        
        return subjects;
    }

    private List<Faculty> generateFaculties(SourceOfRandomness random, List<Subject> subjects, int min, int max) {
        List<Faculty> faculties = new ArrayList<>();
        int count = random.nextInt(min, max + 1);
        
        String[] names = {"Dr. Smith", "Prof. Johnson", "Dr. Williams", "Prof. Brown"};
        for (int i = 0; i < count && i < names.length; i++) {
            String id = "faculty-" + i;
            
            // Assign 1-2 subjects to each faculty
            List<String> subjectIds = new ArrayList<>();
            int numSubjects = Math.min(random.nextInt(1, 3), subjects.size());
            for (int j = 0; j < numSubjects; j++) {
                subjectIds.add(subjects.get(j % subjects.size()).getId());
            }
            
            int maxPeriodsPerDay = random.nextInt(4, 9);
            
            // Full availability for simplicity
            Map<Integer, List<Integer>> availability = new HashMap<>();
            for (int day = 0; day < 5; day++) {
                List<Integer> periods = new ArrayList<>();
                for (int period = 0; period < 8; period++) {
                    periods.add(period);
                }
                availability.put(day, periods);
            }
            
            faculties.add(new Faculty(id, names[i], subjectIds, maxPeriodsPerDay, availability, false));
        }
        
        return faculties;
    }

    private List<Room> generateRooms(SourceOfRandomness random, int min, int max) {
        List<Room> rooms = new ArrayList<>();
        int count = random.nextInt(min, max + 1);
        
        for (int i = 0; i < count; i++) {
            String id = "room-" + i;
            String name = "Room " + (i + 1);
            Room.RoomType type = random.nextBoolean() ? Room.RoomType.CLASSROOM : Room.RoomType.LAB;
            
            // Full availability for simplicity
            Map<Integer, List<Integer>> availability = new HashMap<>();
            for (int day = 0; day < 5; day++) {
                List<Integer> periods = new ArrayList<>();
                for (int period = 0; period < 8; period++) {
                    periods.add(period);
                }
                availability.put(day, periods);
            }
            
            rooms.add(new Room(id, name, type, availability));
        }
        
        return rooms;
    }

    private List<ClassSection> generateClassSections(SourceOfRandomness random, int min, int max) {
        List<ClassSection> sections = new ArrayList<>();
        int count = random.nextInt(min, max + 1);
        
        String[] classes = {"Grade 10", "Grade 11", "Grade 12"};
        String[] sectionNames = {"A", "B", "C"};
        
        for (int i = 0; i < count && i < classes.length; i++) {
            String id = "class-" + i;
            int studentStrength = random.nextInt(20, 41);
            sections.add(new ClassSection(id, classes[i], sectionNames[i % sectionNames.length], studentStrength));
        }
        
        return sections;
    }

    private void generateValidAssignments(SourceOfRandomness random, Timetable timetable, Config config,
                                         List<Subject> subjects, List<Faculty> faculties, 
                                         List<Room> rooms, List<ClassSection> classSections) {
        if (subjects.isEmpty() || faculties.isEmpty() || rooms.isEmpty() || classSections.isEmpty()) {
            return;
        }
        
        // Track used slots to avoid conflicts
        Map<String, Set<TimeSlot>> facultyUsedSlots = new HashMap<>();
        Map<String, Set<TimeSlot>> roomUsedSlots = new HashMap<>();
        Map<String, Map<Integer, Set<String>>> classSubjectsByDay = new HashMap<>();
        
        // Generate a few random assignments (not a complete timetable)
        int numAssignments = random.nextInt(3, 10);
        
        for (int i = 0; i < numAssignments; i++) {
            // Pick random entities
            Subject subject = subjects.get(random.nextInt(subjects.size()));
            ClassSection classSection = classSections.get(random.nextInt(classSections.size()));
            Room room = rooms.get(random.nextInt(rooms.size()));
            
            // Find a faculty that can teach this subject
            Faculty faculty = findFacultyForSubject(faculties, subject.getId());
            if (faculty == null) {
                continue;
            }
            
            // Find an available time slot
            TimeSlot slot = findAvailableSlot(random, config, faculty, room, classSection, 
                                             facultyUsedSlots, roomUsedSlots, classSubjectsByDay, subject);
            
            if (slot != null) {
                Assignment assignment = new Assignment(
                    subject.getId(),
                    faculty.getId(),
                    room.getId(),
                    classSection.getId(),
                    slot,
                    false
                );
                
                timetable.addAssignment(assignment);
                
                // Track usage
                facultyUsedSlots.computeIfAbsent(faculty.getId(), k -> new HashSet<>()).add(slot);
                roomUsedSlots.computeIfAbsent(room.getId(), k -> new HashSet<>()).add(slot);
                classSubjectsByDay.computeIfAbsent(classSection.getId(), k -> new HashMap<>())
                    .computeIfAbsent(slot.getDay(), k -> new HashSet<>()).add(subject.getId());
            }
        }
    }

    private Faculty findFacultyForSubject(List<Faculty> faculties, String subjectId) {
        for (Faculty faculty : faculties) {
            if (faculty.getSubjectIds().contains(subjectId)) {
                return faculty;
            }
        }
        return faculties.isEmpty() ? null : faculties.get(0);
    }

    private TimeSlot findAvailableSlot(SourceOfRandomness random, Config config, Faculty faculty, Room room,
                                      ClassSection classSection, Map<String, Set<TimeSlot>> facultyUsedSlots,
                                      Map<String, Set<TimeSlot>> roomUsedSlots,
                                      Map<String, Map<Integer, Set<String>>> classSubjectsByDay,
                                      Subject subject) {
        // Build list of valid (non-break) periods
        List<Integer> validPeriods = new ArrayList<>();
        for (int period = 0; period < config.getPeriodsPerDay(); period++) {
            if (!config.getBreakPeriods().contains(period)) {
                validPeriods.add(period);
            }
        }
        
        // If no valid periods, return null
        if (validPeriods.isEmpty()) {
            return null;
        }
        
        // Try a few random slots
        for (int attempt = 0; attempt < 20; attempt++) {
            int day = random.nextInt(config.getWorkingDays());
            int period = validPeriods.get(random.nextInt(validPeriods.size()));
            
            TimeSlot slot = new TimeSlot(day, period);
            
            // Check if faculty is available
            if (facultyUsedSlots.containsKey(faculty.getId()) && 
                facultyUsedSlots.get(faculty.getId()).contains(slot)) {
                continue;
            }
            
            // Check if room is available
            if (roomUsedSlots.containsKey(room.getId()) && 
                roomUsedSlots.get(room.getId()).contains(slot)) {
                continue;
            }
            
            // Check subject repetition (if not allowed)
            if (!subject.isAllowRepetition()) {
                Map<Integer, Set<String>> daySubjects = classSubjectsByDay.get(classSection.getId());
                if (daySubjects != null && daySubjects.containsKey(day) && 
                    daySubjects.get(day).contains(subject.getId())) {
                    continue;
                }
            }
            
            return slot;
        }
        
        return null;
    }
}
