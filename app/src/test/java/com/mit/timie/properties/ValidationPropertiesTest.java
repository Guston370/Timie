package com.mit.timie.properties;

import com.mit.timie.model.Config;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Subject;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Property-based tests for validation logic.
 * Each property test runs with minimum 100 iterations to verify correctness across random inputs.
 */
@RunWith(JUnitQuickcheck.class)
public class ValidationPropertiesTest {

    // Feature: timetable-generator, Property 9: Configuration Validation
    /**
     * Property 9: Configuration Validation
     * For any configuration with break periods, at least one non-break teaching period 
     * should exist per day.
     * Validates: Requirements 1.7
     */
    @Property(trials = 100)
    public void configurationValidation(Config config) {
        // Test the validation logic
        boolean isValid = config.validate();
        boolean hasTeachingPeriod = config.hasAtLeastOneTeachingPeriodPerDay();
        
        // If config is valid, it must have at least one teaching period per day
        if (isValid) {
            assertTrue("Valid config must have at least one teaching period per day", 
                      hasTeachingPeriod);
        }
        
        // Verify the specific validation for teaching periods
        if (config.getPeriodsPerDay() > 0 && config.getBreakPeriods() != null) {
            int teachingPeriods = config.getPeriodsPerDay() - config.getBreakPeriods().size();
            
            if (teachingPeriods <= 0) {
                // If all periods are breaks, validation should fail
                assertFalse("Config with no teaching periods should be invalid", isValid);
                assertNotNull("Config with no teaching periods should have validation error", 
                            config.getValidationError());
            }
        }
        
        // Verify other validation constraints
        if (config.getWorkingDays() < 1 || config.getWorkingDays() > 7) {
            assertFalse("Config with invalid working days should be invalid", isValid);
        }
        
        if (config.getPeriodsPerDay() < 1 || config.getPeriodsPerDay() > 12) {
            assertFalse("Config with invalid periods per day should be invalid", isValid);
        }
        
        if (config.getPeriodDuration() <= 0) {
            assertFalse("Config with invalid period duration should be invalid", isValid);
        }
        
        if (config.getDayNames() == null || 
            config.getDayNames().size() != config.getWorkingDays()) {
            assertFalse("Config with mismatched day names should be invalid", isValid);
        }
    }

    // Feature: timetable-generator, Property 10: Subject Data Validation
    /**
     * Property 10: Subject Data Validation
     * For any subject with incomplete data (missing name, invalid weekly period count, 
     * or missing type), the validation should reject the subject and prevent proceeding.
     * Validates: Requirements 2.8
     */
    @Property(trials = 100)
    public void subjectDataValidation(Subject subject) {
        boolean isValid = subject.validate();
        
        // If subject is valid, all required fields must be present
        if (isValid) {
            assertNotNull("Valid subject must have a name", subject.getName());
            assertFalse("Valid subject name must not be empty", 
                       subject.getName().trim().isEmpty());
            assertTrue("Valid subject must have weekly periods between 1 and 30", 
                      subject.getWeeklyPeriods() >= 1 && subject.getWeeklyPeriods() <= 30);
            assertNotNull("Valid subject must have a type", subject.getType());
            assertNotNull("Valid subject must have a priority", subject.getPriority());
        }
        
        // Verify specific validation rules
        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            assertFalse("Subject with empty name should be invalid", isValid);
            assertNotNull("Subject with empty name should have validation error", 
                        subject.getValidationError());
            assertTrue("Validation error should mention name", 
                      subject.getValidationError().toLowerCase().contains("name"));
        }
        
        if (subject.getWeeklyPeriods() < 1 || subject.getWeeklyPeriods() > 30) {
            assertFalse("Subject with invalid weekly periods should be invalid", isValid);
            assertNotNull("Subject with invalid weekly periods should have validation error", 
                        subject.getValidationError());
        }
        
        if (subject.getType() == null) {
            assertFalse("Subject with null type should be invalid", isValid);
            assertNotNull("Subject with null type should have validation error", 
                        subject.getValidationError());
        }
        
        if (subject.getPriority() == null) {
            assertFalse("Subject with null priority should be invalid", isValid);
            assertNotNull("Subject with null priority should have validation error", 
                        subject.getValidationError());
        }
    }

    // Feature: timetable-generator, Property 11: Faculty Data Validation
    /**
     * Property 11: Faculty Data Validation
     * For any faculty with incomplete data (missing name, empty subject list, 
     * or invalid max periods), the validation should reject the faculty and prevent proceeding.
     * Validates: Requirements 3.7
     */
    @Property(trials = 100)
    public void facultyDataValidation(Faculty faculty) {
        boolean isValid = faculty.validate();
        
        // If faculty is valid, all required fields must be present
        if (isValid) {
            assertNotNull("Valid faculty must have a name", faculty.getName());
            assertFalse("Valid faculty name must not be empty", 
                       faculty.getName().trim().isEmpty());
            assertNotNull("Valid faculty must have subject IDs", faculty.getSubjectIds());
            assertFalse("Valid faculty must have at least one subject", 
                       faculty.getSubjectIds().isEmpty());
            assertTrue("Valid faculty must have max periods between 1 and 12", 
                      faculty.getMaxPeriodsPerDay() >= 1 && 
                      faculty.getMaxPeriodsPerDay() <= 12);
            assertTrue("Valid faculty must have at least one available slot", 
                      faculty.hasAtLeastOneAvailableSlot());
        }
        
        // Verify specific validation rules
        if (faculty.getName() == null || faculty.getName().trim().isEmpty()) {
            assertFalse("Faculty with empty name should be invalid", isValid);
            assertNotNull("Faculty with empty name should have validation error", 
                        faculty.getValidationError());
            assertTrue("Validation error should mention name", 
                      faculty.getValidationError().toLowerCase().contains("name"));
        }
        
        if (faculty.getSubjectIds() == null || faculty.getSubjectIds().isEmpty()) {
            assertFalse("Faculty with no subjects should be invalid", isValid);
            assertNotNull("Faculty with no subjects should have validation error", 
                        faculty.getValidationError());
            assertTrue("Validation error should mention subject", 
                      faculty.getValidationError().toLowerCase().contains("subject"));
        }
        
        if (faculty.getMaxPeriodsPerDay() < 1 || faculty.getMaxPeriodsPerDay() > 12) {
            assertFalse("Faculty with invalid max periods should be invalid", isValid);
            assertNotNull("Faculty with invalid max periods should have validation error", 
                        faculty.getValidationError());
        }
        
        if (!faculty.hasAtLeastOneAvailableSlot()) {
            assertFalse("Faculty with no available slots should be invalid", isValid);
            assertNotNull("Faculty with no available slots should have validation error", 
                        faculty.getValidationError());
            assertTrue("Validation error should mention availability", 
                      faculty.getValidationError().toLowerCase().contains("available"));
        }
    }
}
