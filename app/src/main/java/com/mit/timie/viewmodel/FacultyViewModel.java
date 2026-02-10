package com.mit.timie.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mit.timie.model.Faculty;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for managing faculty data and operations.
 * Handles faculty CRUD operations with validation.
 */
public class FacultyViewModel extends ViewModel {
    
    private final TimetableRepository repository;
    private final MutableLiveData<List<Faculty>> faculties;
    private final MutableLiveData<String> validationError;
    
    public FacultyViewModel() {
        this.repository = TimetableRepository.getInstance();
        this.faculties = new MutableLiveData<>(new ArrayList<>());
        this.validationError = new MutableLiveData<>();
        
        // Load existing faculties
        loadFaculties();
    }
    
    /**
     * Loads all faculties from the repository.
     */
    private void loadFaculties() {
        List<Faculty> facultyList = repository.getAllFaculties();
        faculties.setValue(facultyList);
    }
    
    /**
     * Adds a new faculty after validation.
     * 
     * @param faculty The faculty to add
     * @return true if validation passed and faculty was added, false otherwise
     */
    public boolean addFaculty(Faculty faculty) {
        String error = validateFaculty(faculty);
        
        if (error != null) {
            validationError.setValue(error);
            return false;
        }
        
        repository.addFaculty(faculty);
        loadFaculties();
        validationError.setValue(null);
        
        return true;
    }
    
    /**
     * Edits an existing faculty after validation.
     * 
     * @param faculty The faculty with updated data
     * @return true if validation passed and faculty was updated, false otherwise
     */
    public boolean editFaculty(Faculty faculty) {
        String error = validateFaculty(faculty);
        
        if (error != null) {
            validationError.setValue(error);
            return false;
        }
        
        repository.updateFaculty(faculty);
        loadFaculties();
        validationError.setValue(null);
        
        return true;
    }
    
    /**
     * Deletes a faculty from the repository.
     * 
     * @param facultyId The ID of the faculty to delete
     */
    public void deleteFaculty(String facultyId) {
        repository.deleteFaculty(facultyId);
        loadFaculties();
    }
    
    /**
     * Validates faculty data before saving.
     * 
     * @param faculty The faculty to validate
     * @return Error message if validation fails, null if valid
     */
    private String validateFaculty(Faculty faculty) {
        if (faculty == null) {
            return "Faculty cannot be null";
        }
        
        if (faculty.getId() == null || faculty.getId().trim().isEmpty()) {
            return "Faculty ID is required";
        }
        
        if (faculty.getName() == null || faculty.getName().trim().isEmpty()) {
            return "Faculty name is required";
        }
        
        if (faculty.getSubjectIds() == null || faculty.getSubjectIds().isEmpty()) {
            return "Faculty must be assigned to at least one subject";
        }
        
        if (faculty.getMaxPeriodsPerDay() < 1 || faculty.getMaxPeriodsPerDay() > 12) {
            return "Max periods per day must be between 1 and 12";
        }
        
        if (faculty.getAvailability() == null || faculty.getAvailability().isEmpty()) {
            return "Faculty availability is required";
        }
        
        return null;
    }
    
    /**
     * Exposes the faculty list as LiveData for UI observation.
     * 
     * @return LiveData containing the list of faculties
     */
    public LiveData<List<Faculty>> getFaculties() {
        return faculties;
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
     * Gets a faculty by ID.
     * 
     * @param facultyId The ID of the faculty to retrieve
     * @return The faculty, or null if not found
     */
    public Faculty getFacultyById(String facultyId) {
        return repository.getFaculty(facultyId);
    }
}
