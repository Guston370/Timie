package com.mit.timie.repository;

import com.mit.timie.model.ClassSection;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Room;
import com.mit.timie.model.Subject;
import com.mit.timie.model.Timetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for managing timetable-related data using in-memory storage.
 * Implements singleton pattern to ensure single instance across the application.
 */
public class TimetableRepository {
    private static TimetableRepository instance;
    
    private Map<String, Subject> subjects;
    private Map<String, Faculty> faculties;
    private Map<String, Room> rooms;
    private Map<String, ClassSection> classSections;
    private List<Timetable> generatedTimetables;
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private TimetableRepository() {
        subjects = new HashMap<>();
        faculties = new HashMap<>();
        rooms = new HashMap<>();
        classSections = new HashMap<>();
        generatedTimetables = new ArrayList<>();
    }
    
    /**
     * Get the singleton instance of TimetableRepository.
     * @return The singleton instance
     */
    public static synchronized TimetableRepository getInstance() {
        if (instance == null) {
            instance = new TimetableRepository();
        }
        return instance;
    }
    
    // Subject CRUD operations
    
    /**
     * Add a subject to the repository.
     * @param subject The subject to add
     */
    public void addSubject(Subject subject) {
        if (subject != null && subject.getId() != null) {
            subjects.put(subject.getId(), subject);
        }
    }
    
    /**
     * Get a subject by ID.
     * @param id The subject ID
     * @return The subject, or null if not found
     */
    public Subject getSubject(String id) {
        return subjects.get(id);
    }
    
    /**
     * Get all subjects.
     * @return List of all subjects
     */
    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects.values());
    }
    
    /**
     * Update an existing subject.
     * @param subject The subject to update
     */
    public void updateSubject(Subject subject) {
        if (subject != null && subject.getId() != null && subjects.containsKey(subject.getId())) {
            subjects.put(subject.getId(), subject);
        }
    }
    
    /**
     * Delete a subject by ID.
     * @param id The subject ID
     */
    public void deleteSubject(String id) {
        subjects.remove(id);
    }
    
    // Faculty CRUD operations
    
    /**
     * Add a faculty member to the repository.
     * @param faculty The faculty to add
     */
    public void addFaculty(Faculty faculty) {
        if (faculty != null && faculty.getId() != null) {
            faculties.put(faculty.getId(), faculty);
        }
    }
    
    /**
     * Get a faculty member by ID.
     * @param id The faculty ID
     * @return The faculty, or null if not found
     */
    public Faculty getFaculty(String id) {
        return faculties.get(id);
    }
    
    /**
     * Get all faculty members.
     * @return List of all faculty members
     */
    public List<Faculty> getAllFaculties() {
        return new ArrayList<>(faculties.values());
    }
    
    /**
     * Update an existing faculty member.
     * @param faculty The faculty to update
     */
    public void updateFaculty(Faculty faculty) {
        if (faculty != null && faculty.getId() != null && faculties.containsKey(faculty.getId())) {
            faculties.put(faculty.getId(), faculty);
        }
    }
    
    /**
     * Delete a faculty member by ID.
     * @param id The faculty ID
     */
    public void deleteFaculty(String id) {
        faculties.remove(id);
    }
    
    // Room CRUD operations
    
    /**
     * Add a room to the repository.
     * @param room The room to add
     */
    public void addRoom(Room room) {
        if (room != null && room.getId() != null) {
            rooms.put(room.getId(), room);
        }
    }
    
    /**
     * Get a room by ID.
     * @param id The room ID
     * @return The room, or null if not found
     */
    public Room getRoom(String id) {
        return rooms.get(id);
    }
    
    /**
     * Get all rooms.
     * @return List of all rooms
     */
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }
    
    /**
     * Update an existing room.
     * @param room The room to update
     */
    public void updateRoom(Room room) {
        if (room != null && room.getId() != null && rooms.containsKey(room.getId())) {
            rooms.put(room.getId(), room);
        }
    }
    
    /**
     * Delete a room by ID.
     * @param id The room ID
     */
    public void deleteRoom(String id) {
        rooms.remove(id);
    }
    
    // ClassSection CRUD operations
    
    /**
     * Add a class section to the repository.
     * @param classSection The class section to add
     */
    public void addClassSection(ClassSection classSection) {
        if (classSection != null && classSection.getId() != null) {
            classSections.put(classSection.getId(), classSection);
        }
    }
    
    /**
     * Get a class section by ID.
     * @param id The class section ID
     * @return The class section, or null if not found
     */
    public ClassSection getClassSection(String id) {
        return classSections.get(id);
    }
    
    /**
     * Get all class sections.
     * @return List of all class sections
     */
    public List<ClassSection> getAllClassSections() {
        return new ArrayList<>(classSections.values());
    }
    
    /**
     * Update an existing class section.
     * @param classSection The class section to update
     */
    public void updateClassSection(ClassSection classSection) {
        if (classSection != null && classSection.getId() != null && classSections.containsKey(classSection.getId())) {
            classSections.put(classSection.getId(), classSection);
        }
    }
    
    /**
     * Delete a class section by ID.
     * @param id The class section ID
     */
    public void deleteClassSection(String id) {
        classSections.remove(id);
    }
    
    // Timetable operations
    
    /**
     * Save a generated timetable.
     * @param timetable The timetable to save
     */
    public void saveTimetable(Timetable timetable) {
        if (timetable != null) {
            generatedTimetables.add(timetable);
        }
    }
    
    /**
     * Get all generated timetables.
     * @return List of all generated timetables
     */
    public List<Timetable> getAllTimetables() {
        return new ArrayList<>(generatedTimetables);
    }
    
    /**
     * Clear all generated timetables.
     */
    public void clearTimetables() {
        generatedTimetables.clear();
    }
    
    /**
     * Clear all data from the repository.
     */
    public void clearAll() {
        subjects.clear();
        faculties.clear();
        rooms.clear();
        classSections.clear();
        generatedTimetables.clear();
    }
}
