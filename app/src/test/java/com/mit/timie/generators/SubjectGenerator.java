package com.mit.timie.generators;

import com.mit.timie.model.Subject;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.UUID;

public class SubjectGenerator extends Generator<Subject> {
    private static final String[] SUBJECT_NAMES = {
        "Mathematics", "Physics", "Chemistry", "Biology", "English",
        "History", "Geography", "Computer Science", "Economics", "Psychology",
        "Art", "Music", "Physical Education", "Literature", "Statistics"
    };

    public SubjectGenerator() {
        super(Subject.class);
    }

    @Override
    public Subject generate(SourceOfRandomness random, GenerationStatus status) {
        // Generate unique ID
        String id = UUID.randomUUID().toString();
        
        // Generate subject name
        String name = SUBJECT_NAMES[random.nextInt(SUBJECT_NAMES.length)];
        
        // Generate weekly periods between 1-30
        int weeklyPeriods = random.nextInt(1, 31);
        
        // Generate random subject type (THEORY or LAB)
        Subject.SubjectType type = random.nextBoolean() ? 
            Subject.SubjectType.THEORY : Subject.SubjectType.LAB;
        
        // Generate random priority (HIGH, MEDIUM, or LOW)
        Subject.Priority priority = generateRandomPriority(random);
        
        // Generate random allowRepetition flag
        boolean allowRepetition = random.nextBoolean();
        
        return new Subject(id, name, weeklyPeriods, type, priority, allowRepetition);
    }

    private Subject.Priority generateRandomPriority(SourceOfRandomness random) {
        int choice = random.nextInt(3);
        switch (choice) {
            case 0:
                return Subject.Priority.HIGH;
            case 1:
                return Subject.Priority.MEDIUM;
            case 2:
            default:
                return Subject.Priority.LOW;
        }
    }
}
