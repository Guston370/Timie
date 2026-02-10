package com.mit.timie.generators;

import com.mit.timie.model.Room;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RoomGenerator extends Generator<Room> {
    private static final String[] ROOM_PREFIXES = {
        "Room", "Hall", "Lab", "Classroom", "Auditorium", "Studio", "Workshop"
    };

    public RoomGenerator() {
        super(Room.class);
    }

    @Override
    public Room generate(SourceOfRandomness random, GenerationStatus status) {
        // Generate unique ID
        String id = UUID.randomUUID().toString();
        
        // Generate room name
        String prefix = ROOM_PREFIXES[random.nextInt(ROOM_PREFIXES.length)];
        String name = prefix + " " + random.nextInt(1, 100);
        
        // Generate random room type (CLASSROOM or LAB)
        Room.RoomType type = random.nextBoolean() ? 
            Room.RoomType.CLASSROOM : Room.RoomType.LAB;
        
        // Generate availability with valid time slots
        Map<Integer, List<Integer>> availability = generateAvailability(random);
        
        return new Room(id, name, type, availability);
    }

    private Map<Integer, List<Integer>> generateAvailability(SourceOfRandomness random) {
        Map<Integer, List<Integer>> availability = new HashMap<>();
        
        // Generate availability for 1-7 days
        int numDays = random.nextInt(1, 8);
        
        for (int day = 0; day < numDays; day++) {
            List<Integer> periods = new ArrayList<>();
            
            // Generate 0-12 available periods per day
            int numPeriods = random.nextInt(0, 13);
            
            for (int period = 0; period < numPeriods; period++) {
                periods.add(period);
            }
            
            availability.put(day, periods);
        }
        
        return availability;
    }
}
