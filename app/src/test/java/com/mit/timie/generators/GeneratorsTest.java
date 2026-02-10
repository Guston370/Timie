package com.mit.timie.generators;

import com.mit.timie.model.ClassSection;
import com.mit.timie.model.Config;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Room;
import com.mit.timie.model.Subject;
import com.mit.timie.model.Timetable;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Test to verify that all property-based test generators work correctly.
 * This test validates that generators produce valid objects that satisfy
 * their respective validation rules.
 */
@RunWith(JUnitQuickcheck.class)
public class GeneratorsTest {

    @Property(trials = 10)
    public void configGeneratorProducesValidConfigs(Config config) {
        assertNotNull("Config should not be null", config);
        assertTrue("Config should be valid", config.validate());
        assertTrue("Working days should be between 1-7", 
            config.getWorkingDays() >= 1 && config.getWorkingDays() <= 7);
        assertTrue("Periods per day should be between 1-12", 
            config.getPeriodsPerDay() >= 1 && config.getPeriodsPerDay() <= 12);
        assertTrue("Should have at least one teaching period per day", 
            config.hasAtLeastOneTeachingPeriodPerDay());
    }

    @Property(trials = 10)
    public void subjectGeneratorProducesValidSubjects(Subject subject) {
        assertNotNull("Subject should not be null", subject);
        assertTrue("Subject should be valid", subject.validate());
        assertNotNull("Subject name should not be null", subject.getName());
        assertTrue("Weekly periods should be between 1-30", 
            subject.getWeeklyPeriods() >= 1 && subject.getWeeklyPeriods() <= 30);
        assertNotNull("Subject type should not be null", subject.getType());
        assertNotNull("Priority should not be null", subject.getPriority());
    }

    @Property(trials = 10)
    public void facultyGeneratorProducesValidFaculty(Faculty faculty) {
        assertNotNull("Faculty should not be null", faculty);
        assertTrue("Faculty should be valid", faculty.validate());
        assertNotNull("Faculty name should not be null", faculty.getName());
        assertFalse("Faculty should have at least one subject", 
            faculty.getSubjectIds().isEmpty());
        assertTrue("Max periods per day should be between 1-12", 
            faculty.getMaxPeriodsPerDay() >= 1 && faculty.getMaxPeriodsPerDay() <= 12);
        assertTrue("Faculty should have at least one available slot", 
            faculty.hasAtLeastOneAvailableSlot());
    }

    @Property(trials = 10)
    public void roomGeneratorProducesValidRooms(Room room) {
        assertNotNull("Room should not be null", room);
        assertTrue("Room should be valid", room.validate());
        assertNotNull("Room name should not be null", room.getName());
        assertNotNull("Room type should not be null", room.getType());
    }

    @Property(trials = 10)
    public void classSectionGeneratorProducesValidClassSections(ClassSection classSection) {
        assertNotNull("ClassSection should not be null", classSection);
        assertTrue("ClassSection should be valid", classSection.validate());
        assertNotNull("Class name should not be null", classSection.getClassName());
        assertNotNull("Section name should not be null", classSection.getSectionName());
        assertTrue("Student strength should be non-negative", 
            classSection.getStudentStrength() >= 0);
    }

    @Property(trials = 10)
    public void timetableGeneratorProducesTimetables(Timetable timetable) {
        assertNotNull("Timetable should not be null", timetable);
        assertNotNull("Timetable should have class timetables map", 
            timetable.getClassTimetables());
        assertNotNull("Timetable should have faculty timetables map", 
            timetable.getFacultyTimetables());
        assertNotNull("Timetable should have all assignments list", 
            timetable.getAllAssignments());
    }
}
