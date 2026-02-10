package com.mit.timie.generators;

import com.mit.timie.model.Config;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class ConfigGeneratorDebugTest {
    
    @Test
    public void testConfigGeneratorWithSeed() {
        ConfigGenerator generator = new ConfigGenerator();
        
        // Use the failing seed from the error
        long seed = -6990421087453949204L;
        SourceOfRandomness random = new SourceOfRandomness(new Random(seed));
        GenerationStatus status = null; // Not used in our generator
        
        Config config = generator.generate(random, status);
        
        System.out.println("Working Days: " + config.getWorkingDays());
        System.out.println("Day Names Size: " + config.getDayNames().size());
        System.out.println("Day Names: " + config.getDayNames());
        System.out.println("Periods Per Day: " + config.getPeriodsPerDay());
        System.out.println("Period Duration: " + config.getPeriodDuration());
        System.out.println("Break Periods: " + config.getBreakPeriods());
        System.out.println("Break Periods Size: " + config.getBreakPeriods().size());
        System.out.println("Has At Least One Teaching Period: " + config.hasAtLeastOneTeachingPeriodPerDay());
        System.out.println("Validation Error: " + config.getValidationError());
        
        assertTrue("Config should be valid", config.validate());
    }
}
