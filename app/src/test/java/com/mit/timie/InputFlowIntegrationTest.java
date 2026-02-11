package com.mit.timie;

import com.mit.timie.model.ClassSection;
import com.mit.timie.model.Config;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Room;
import com.mit.timie.model.Subject;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Integration test for the input flow.
 * Tests that data flows correctly through the repositories and is persisted.
 */
public class InputFlowIntegrationTest {
    
    private ConfigRepository configRepository;
    private TimetableRepository timetableRepository;
    
    @Before
    public void setUp() {
        // Get repository instances
        configRepository = ConfigRepository.getInstance();
        timetableRepository = TimetableRepository.getInstance();
        
        // Clear any existing data
        configRepository.clearConfig();
        timetableRepository.clearAll();
    }
    
    @Test
    public void testConfigurationPersistence() {
        // Create a configuration
        List<String> dayNames = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        List<Integer> breakPeriods = Arrays.asList(3, 6);
        Config config = new Config(5, dayNames, 8, 45, breakPeriods);
        
        // Save configuration
        configRepository.saveConfig(config);
        
        // Retrieve configuration
        Config retrievedConfig = configRepository.getConfig();
        
        // Verify configuration was saved correctly
        assertNotNull("Configuration should not be null", retrievedConfig);
        assertEquals("Working days should match", 5, retrievedConfig.getWorkingDays());
        assertEquals("Periods per day should match", 8, retrievedConfig.getPeriodsPerDay());
        assertEquals("Period duration should match", 45, retrievedConfig.getPeriodDuration());
        assertEquals("Day names should match", dayNames, retrievedConfig.getDayNames());
        assertEquals("Break periods should match", breakPeriods, retrievedConfig.getBreakPeriods());
    }
    
    @Test
    public void testSubjectPersistence() {
        // Create a subject
        Subject subject = new Subject();
        subject.setId("sub1");
        subject.setName("Mathematics");
        subject.setWeeklyPeriods(5);
        subject.setType(Subject.SubjectType.THEORY);
        subject.setPriority(Subject.Priority.HIGH);
        subject.setAllowRepetition(false);
        
        // Add subject to repository
        timetableRepository.addSubject(subject);
        
        // Retrieve subject
        Subject retrievedSubject = timetableRepository.getSubject("sub1");
        
        // Verify subject was saved correctly
        assertNotNull("Subject should not be null", retrievedSubject);
        assertEquals("Subject name should match", "Mathematics", retrievedSubject.getName());
        assertEquals("Weekly periods should match", 5, retrievedSubject.getWeeklyPeriods());
        assertEquals("Subject type should match", Subject.SubjectType.THEORY, retrievedSubject.getType());
        assertEquals("Priority should match", Subject.Priority.HIGH, retrievedSubject.getPriority());
        assertFalse("Allow repetition should be false", retrievedSubject.isAllowRepetition());
        
        // Verify subject appears in list
        List<Subject> allSubjects = timetableRepository.getAllSubjects();
        assertEquals("Should have 1 subject", 1, allSubjects.size());
        assertEquals("Subject in list should match", "Mathematics", allSubjects.get(0).getName());
    }
    
    @Test
    public void testFacultyPersistence() {
        // Create a faculty member
        Faculty faculty = new Faculty();
        faculty.setId("fac1");
        faculty.setName("Dr. Smith");
        faculty.setSubjectIds(Arrays.asList("sub1", "sub2"));
        faculty.setMaxPeriodsPerDay(6);
        
        Map<Integer, List<Integer>> availability = new HashMap<>();
        availability.put(0, Arrays.asList(0, 1, 2, 3, 4));
        availability.put(1, Arrays.asList(0, 1, 2, 3, 4));
        faculty.setAvailability(availability);
        faculty.setAvoidConsecutive(true);
        
        // Add faculty to repository
        timetableRepository.addFaculty(faculty);
        
        // Retrieve faculty
        Faculty retrievedFaculty = timetableRepository.getFaculty("fac1");
        
        // Verify faculty was saved correctly
        assertNotNull("Faculty should not be null", retrievedFaculty);
        assertEquals("Faculty name should match", "Dr. Smith", retrievedFaculty.getName());
        assertEquals("Max periods should match", 6, retrievedFaculty.getMaxPeriodsPerDay());
        assertEquals("Subject IDs should match", 2, retrievedFaculty.getSubjectIds().size());
        assertTrue("Should avoid consecutive", retrievedFaculty.isAvoidConsecutive());
        
        // Verify faculty appears in list
        List<Faculty> allFaculties = timetableRepository.getAllFaculties();
        assertEquals("Should have 1 faculty", 1, allFaculties.size());
    }
    
    @Test
    public void testRoomPersistence() {
        // Create a room
        Room room = new Room();
        room.setId("room1");
        room.setName("Lab 101");
        room.setType(Room.RoomType.LAB);
        
        Map<Integer, List<Integer>> availability = new HashMap<>();
        availability.put(0, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
        availability.put(1, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
        room.setAvailability(availability);
        
        // Add room to repository
        timetableRepository.addRoom(room);
        
        // Retrieve room
        Room retrievedRoom = timetableRepository.getRoom("room1");
        
        // Verify room was saved correctly
        assertNotNull("Room should not be null", retrievedRoom);
        assertEquals("Room name should match", "Lab 101", retrievedRoom.getName());
        assertEquals("Room type should match", Room.RoomType.LAB, retrievedRoom.getType());
        assertNotNull("Availability should not be null", retrievedRoom.getAvailability());
        
        // Verify room appears in list
        List<Room> allRooms = timetableRepository.getAllRooms();
        assertEquals("Should have 1 room", 1, allRooms.size());
    }
    
    @Test
    public void testClassSectionPersistence() {
        // Create a class section
        ClassSection classSection = new ClassSection();
        classSection.setId("class1");
        classSection.setClassName("Grade 10");
        classSection.setSectionName("A");
        classSection.setStudentStrength(40);
        
        // Add class section to repository
        timetableRepository.addClassSection(classSection);
        
        // Retrieve class section
        ClassSection retrievedClass = timetableRepository.getClassSection("class1");
        
        // Verify class section was saved correctly
        assertNotNull("Class section should not be null", retrievedClass);
        assertEquals("Class name should match", "Grade 10", retrievedClass.getClassName());
        assertEquals("Section name should match", "A", retrievedClass.getSectionName());
        assertEquals("Student strength should match", 40, retrievedClass.getStudentStrength());
        
        // Verify class section appears in list
        List<ClassSection> allClasses = timetableRepository.getAllClassSections();
        assertEquals("Should have 1 class section", 1, allClasses.size());
    }
    
    @Test
    public void testCompleteInputFlow() {
        // Simulate the complete input flow
        
        // Step 1: Save configuration
        Config config = new Config(5, 
                Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri"), 
                8, 45, Arrays.asList(3));
        configRepository.saveConfig(config);
        
        // Step 2: Add subjects
        Subject math = new Subject();
        math.setId("math");
        math.setName("Mathematics");
        math.setWeeklyPeriods(5);
        math.setType(Subject.SubjectType.THEORY);
        math.setPriority(Subject.Priority.HIGH);
        timetableRepository.addSubject(math);
        
        Subject physics = new Subject();
        physics.setId("physics");
        physics.setName("Physics Lab");
        physics.setWeeklyPeriods(4);
        physics.setType(Subject.SubjectType.LAB);
        physics.setPriority(Subject.Priority.MEDIUM);
        timetableRepository.addSubject(physics);
        
        // Step 3: Add faculty
        Faculty faculty1 = new Faculty();
        faculty1.setId("fac1");
        faculty1.setName("Dr. Johnson");
        faculty1.setSubjectIds(Arrays.asList("math"));
        faculty1.setMaxPeriodsPerDay(6);
        Map<Integer, List<Integer>> avail1 = new HashMap<>();
        avail1.put(0, Arrays.asList(0, 1, 2, 4, 5, 6, 7));
        faculty1.setAvailability(avail1);
        timetableRepository.addFaculty(faculty1);
        
        // Step 4: Add rooms
        Room classroom = new Room();
        classroom.setId("room1");
        classroom.setName("Room 101");
        classroom.setType(Room.RoomType.CLASSROOM);
        Map<Integer, List<Integer>> roomAvail = new HashMap<>();
        roomAvail.put(0, Arrays.asList(0, 1, 2, 4, 5, 6, 7));
        classroom.setAvailability(roomAvail);
        timetableRepository.addRoom(classroom);
        
        // Step 5: Add class sections
        ClassSection section = new ClassSection();
        section.setId("class1");
        section.setClassName("Grade 10");
        section.setSectionName("A");
        section.setStudentStrength(35);
        timetableRepository.addClassSection(section);
        
        // Verify all data is accessible
        assertNotNull("Config should be saved", configRepository.getConfig());
        assertEquals("Should have 2 subjects", 2, timetableRepository.getAllSubjects().size());
        assertEquals("Should have 1 faculty", 1, timetableRepository.getAllFaculties().size());
        assertEquals("Should have 1 room", 1, timetableRepository.getAllRooms().size());
        assertEquals("Should have 1 class section", 1, timetableRepository.getAllClassSections().size());
    }
    
    @Test
    public void testUpdateOperations() {
        // Add a subject
        Subject subject = new Subject();
        subject.setId("sub1");
        subject.setName("Original Name");
        subject.setWeeklyPeriods(5);
        subject.setType(Subject.SubjectType.THEORY);
        subject.setPriority(Subject.Priority.MEDIUM);
        timetableRepository.addSubject(subject);
        
        // Update the subject
        subject.setName("Updated Name");
        subject.setWeeklyPeriods(6);
        timetableRepository.updateSubject(subject);
        
        // Retrieve and verify
        Subject updated = timetableRepository.getSubject("sub1");
        assertEquals("Name should be updated", "Updated Name", updated.getName());
        assertEquals("Weekly periods should be updated", 6, updated.getWeeklyPeriods());
    }
    
    @Test
    public void testDeleteOperations() {
        // Add multiple subjects
        Subject sub1 = new Subject();
        sub1.setId("sub1");
        sub1.setName("Subject 1");
        sub1.setWeeklyPeriods(5);
        sub1.setType(Subject.SubjectType.THEORY);
        sub1.setPriority(Subject.Priority.HIGH);
        timetableRepository.addSubject(sub1);
        
        Subject sub2 = new Subject();
        sub2.setId("sub2");
        sub2.setName("Subject 2");
        sub2.setWeeklyPeriods(4);
        sub2.setType(Subject.SubjectType.LAB);
        sub2.setPriority(Subject.Priority.MEDIUM);
        timetableRepository.addSubject(sub2);
        
        // Verify both exist
        assertEquals("Should have 2 subjects", 2, timetableRepository.getAllSubjects().size());
        
        // Delete one subject
        timetableRepository.deleteSubject("sub1");
        
        // Verify deletion
        assertEquals("Should have 1 subject", 1, timetableRepository.getAllSubjects().size());
        assertNull("Deleted subject should not exist", timetableRepository.getSubject("sub1"));
        assertNotNull("Other subject should still exist", timetableRepository.getSubject("sub2"));
    }
}
