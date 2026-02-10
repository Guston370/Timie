package com.mit.timie.generators;

import com.mit.timie.model.Faculty;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FacultyGenerator extends Generator<Faculty> {
    private static final String[] FACULTY_NAMES = {
        "Dr. Smith", "Prof. Johnson", "Dr. Williams", "Prof. Brown", "Dr. Jones",
        "Prof. Garcia", "Dr. Miller", "Prof. Davis", "Dr. Rodriguez", "Prof. Martinez",
        "Dr. Hernandez", "Prof. Lopez", "Dr. Gonzalez", "Prof. Wilson", "Dr. Anderson"
    };

    public FacultyGenerator() {
        super(Faculty.class);
    }

    @Override
    public Faculty generate(SourceOfRandomness random, GenerationStatus status) {
        // Generate unique ID
        String id = UUID.randomUUID().toString();
        
        // Generate faculty name
        String name = FACULTY_NAMES[random.nextInt(FACULTY_NAMES.length)];
        
        // Generate at least one subject ID (1-5 subjects)
        List<String> subjectIds = generateSubjectIds(random);
        
        // Generate maxPeriodsPerDay between 1-12
        int maxPeriodsPerDay = random.nextInt(1, 13);
        
        // Generate availability with at least some available time slots
        Map<Integer, List<Integer>> availability = generateAvailability(random);
        
        // Generate random avoidConsecutive flag
        boolean avoidConsecutive = random.nextBoolean();
        
        return new Faculty(id, name, subjectIds, maxPeriodsPerDay, availability, avoidConsecutive);
    }

    private List<String> generateSubjectIds(SourceOfRandomness random) {
        List<String> subjectIds = new ArrayList<>();
        int numSubjects = random.nextInt(1, 6); // 1-5 subjects
        
        for (int i = 0; i < numSubjects; i++) {
            subjectIds.add("subject-" + UUID.randomUUID().toString());
        }
        
        return subjectIds;
    }

    private Map<Integer, List<Integer>> generateAvailability(SourceOfRandomness random) {
        Map<Integer, List<Integer>> availability = new HashMap<>();
        
        // Generate availability for 1-7 days
        int numDays = random.nextInt(1, 8);
        
        for (int day = 0; day < numDays; day++) {
            List<Integer> periods = new ArrayList<>();
            
            // Generate at least 1 available period per day (1-12 periods)
            int maxPeriods = random.nextInt(1, 13);
            
            // Add periods 0 to maxPeriods-1
            for (int period = 0; period < maxPeriods; period++) {
                periods.add(period);
            }
            
            // Only add if there are periods
            if (!periods.isEmpty()) {
                availability.put(day, periods);
            }
        }
        
        // Ensure at least one day with at least one period
        if (availability.isEmpty()) {
            List<Integer> periods = new ArrayList<>();
            periods.add(0);
            availability.put(0, periods);
        }
        
        return availability;
    }
}
