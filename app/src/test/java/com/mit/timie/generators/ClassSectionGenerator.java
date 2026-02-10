package com.mit.timie.generators;

import com.mit.timie.model.ClassSection;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.UUID;

public class ClassSectionGenerator extends Generator<ClassSection> {
    private static final String[] CLASS_NAMES = {
        "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5",
        "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10",
        "Grade 11", "Grade 12", "Year 1", "Year 2", "Year 3"
    };

    private static final String[] SECTION_NAMES = {
        "A", "B", "C", "D", "E", "F", "G", "H"
    };

    public ClassSectionGenerator() {
        super(ClassSection.class);
    }

    @Override
    public ClassSection generate(SourceOfRandomness random, GenerationStatus status) {
        // Generate unique ID
        String id = UUID.randomUUID().toString();
        
        // Generate class name
        String className = CLASS_NAMES[random.nextInt(CLASS_NAMES.length)];
        
        // Generate section name
        String sectionName = SECTION_NAMES[random.nextInt(SECTION_NAMES.length)];
        
        // Generate student strength between 10-60
        int studentStrength = random.nextInt(10, 61);
        
        return new ClassSection(id, className, sectionName, studentStrength);
    }
}
