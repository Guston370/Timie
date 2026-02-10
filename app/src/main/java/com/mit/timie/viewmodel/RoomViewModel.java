package com.mit.timie.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mit.timie.model.Room;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for managing room data and operations.
 * Handles room CRUD operations with validation.
 */
public class RoomViewModel extends ViewModel {
    
    private final TimetableRepository repository;
    private final MutableLiveData<List<Room>> rooms;
    private final MutableLiveData<String> validationError;
    
    public RoomViewModel() {
        this.repository = TimetableRepository.getInstance();
        this.rooms = new MutableLiveData<>(new ArrayList<>());
        this.validationError = new MutableLiveData<>();
        
        // Load existing rooms
        loadRooms();
    }
    
    /**
     * Loads all rooms from the repository.
     */
    private void loadRooms() {
        List<Room> roomList = repository.getAllRooms();
        rooms.setValue(roomList);
    }
    
    /**
     * Adds a new room after validation.
     * 
     * @param room The room to add
     * @return true if validation passed and room was added, false otherwise
     */
    public boolean addRoom(Room room) {
        String error = validateRoom(room);
        
        if (error != null) {
            validationError.setValue(error);
            return false;
        }
        
        repository.addRoom(room);
        loadRooms();
        validationError.setValue(null);
        
        return true;
    }
    
    /**
     * Edits an existing room after validation.
     * 
     * @param room The room with updated data
     * @return true if validation passed and room was updated, false otherwise
     */
    public boolean editRoom(Room room) {
        String error = validateRoom(room);
        
        if (error != null) {
            validationError.setValue(error);
            return false;
        }
        
        repository.updateRoom(room);
        loadRooms();
        validationError.setValue(null);
        
        return true;
    }
    
    /**
     * Deletes a room from the repository.
     * 
     * @param roomId The ID of the room to delete
     */
    public void deleteRoom(String roomId) {
        repository.deleteRoom(roomId);
        loadRooms();
    }
    
    /**
     * Validates room data before saving.
     * 
     * @param room The room to validate
     * @return Error message if validation fails, null if valid
     */
    private String validateRoom(Room room) {
        if (room == null) {
            return "Room cannot be null";
        }
        
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            return "Room ID is required";
        }
        
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            return "Room name is required";
        }
        
        if (room.getType() == null) {
            return "Room type is required";
        }
        
        if (room.getAvailability() == null) {
            return "Room availability is required";
        }
        
        return null;
    }
    
    /**
     * Exposes the room list as LiveData for UI observation.
     * 
     * @return LiveData containing the list of rooms
     */
    public LiveData<List<Room>> getRooms() {
        return rooms;
    }
    
    /**
     * Exposes validation errors as LiveData for UI observation.
     * 
     * @return LiveData containing validation error messages
     */
    public LiveData<String> getValidationError() {
        return validationError;
    }
    
    /**
     * Gets a room by ID.
     * 
     * @param roomId The ID of the room to retrieve
     * @return The room, or null if not found
     */
    public Room getRoomById(String roomId) {
        return repository.getRoom(roomId);
    }
}
