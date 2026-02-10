package com.mit.timie.generators;

import com.mit.timie.model.Config;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigGenerator extends Generator<Config> {
    private static final String[] DAY_NAMES = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    public ConfigGenerator() {
        super(Config.class);
    }

    @Override
    public Config generate(SourceOfRandomness random, GenerationStatus status) {
        // Generate workingDays between 1-7
        int workingDays = Math.min(7, Math.max(1, random.nextInt(1, 8))); // Ensure 1-7 range
        
        // Generate day names (ensure we don't exceed array bounds)
        List<String> dayNames = new ArrayList<>();
        for (int i = 0; i < Math.min(workingDays, DAY_NAMES.length); i++) {
            dayNames.add(DAY_NAMES[i]);
        }
        
        // Generate periodsPerDay between 1-12
        int periodsPerDay = random.nextInt(1, 13);
        
        // Generate period duration between 30-90 minutes
        int periodDuration = random.nextInt(30, 91);
        
        // Generate break periods that leave at least one teaching period per day
        List<Integer> breakPeriods = generateBreakPeriods(random, periodsPerDay);
        
        return new Config(workingDays, dayNames, periodsPerDay, periodDuration, breakPeriods);
    }

    private List<Integer> generateBreakPeriods(SourceOfRandomness random, int periodsPerDay) {
        List<Integer> breakPeriods = new ArrayList<>();
        
        // Ensure at least one teaching period exists
        if (periodsPerDay <= 1) {
            return breakPeriods; // No breaks if only 1 period
        }
        
        // Generate 0 to (periodsPerDay - 1) break periods to ensure at least one teaching period
        int maxBreaks = periodsPerDay - 1;
        int numBreaks = random.nextInt(0, Math.max(1, maxBreaks));
        
        // Use a set to avoid duplicate break periods
        Set<Integer> breakSet = new HashSet<>();
        int attempts = 0;
        while (breakSet.size() < numBreaks && attempts < periodsPerDay * 2) {
            // Generate break period from 0 to periodsPerDay-1 (valid period indices)
            int breakPeriod = random.nextInt(0, periodsPerDay);
            breakSet.add(breakPeriod);
            attempts++;
        }
        
        breakPeriods.addAll(breakSet);
        return breakPeriods;
    }
}
