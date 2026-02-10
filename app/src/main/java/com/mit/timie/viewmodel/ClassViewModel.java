package com.mit.timie.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mit.timie.model.ClassSection;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for managing class section data and operations.
 * Handles class section CRUD operations with validation.
 */
public class ClassViewModel extends ViewModel {
    
    private final TimetableRepository repository;
    private final MutableLiveData<List<ClassSection>> classSections;
    private final MutableLiveData<String> validationError;
    
    public ClassViewModel() {
        this.repository = TimetableRepository.getInstance();
        this.classSections = new MutableLiveData<>(new ArrayList<>());
        this.validationError = new MutableLiveData<>();
        
        // Load existing class sections
        loadClassSections();
    }
    
    /**
     * Loads all class sections from the repository.
     */
    private void loadClassSections() {
        List<ClassSection> classList = repository.getAllClassSections();
        classSections.setValue(classList);
    }
    
    /**
     * Adds a new class section after validation.
     * 
     * @param classSection The class section to add
     * @return true if validation passed and class section was added, false otherwise
     */
    public boolean addClass(ClassSection classSection) {
        String error = validateClassSection(classSection);
        
        if (error != null) {
            validationError.setValue(error);
            return false;
        }
        
        repository.addClassSection(classSection);
        loadClassSections();
        validationError.setValue(null);
        
        return true;
    }
    
    /**
     * Edits an existing class section after validation.
     * 
     * @param classSection The class section with updated data
     * @return true if validation passed and class section was updated, false otherwise
     */
    public boolean editClass(ClassSection classSection) {
        String error = validateClassSection(classSection);
        
        if (error != null) {
            validationError.setValue(error);
            return false;
        }
        
        repository.updateClassSection(classSection);
        loadClassSections();
        validationError.setValue(null);
        
        return true;
    }
    
    /**
     * Deletes a class section from the repository.
     * 
     * @param classSectionId The ID of the class section to delete
     */
    public void deleteClass(String classSectionId) {
        repository.deleteClassSection(classSectionId);
        loadClassSections();
    }
    
    /**
     * Validates class section data before saving.
     * 
     * @param classSection The class section to validate
     * @return Error message if validation fails, null if valid
     */
    private String validateClassSection(ClassSection classSection) {
        if (classSection == null) {
            return "Class section cannot be null";
        }
        
        if (classSection.getId() == null || classSection.getId().trim().isEmpty()) {
            return "Class section ID is required";
        }
        
        if (classSection.getClassName() == null || classSection.getClassName().trim().isEmpty()) {
            return "Class name is required";
        }
        
        if (classSection.getSectionName() == null || classSection.getSectionName().trim().isEmpty()) {
            return "Section name is required";
        }
        
        if (classSection.getStudentStrength() < 1) {
            return "Student strength must be at least 1";
        }
        
        return null;
    }
    
    /**
     * Exposes the class section list as LiveData for UI observation.
     * 
     * @return LiveData containing the list of class sections
     */
    public LiveData<List<ClassSection>> getClassSections() {
        return classSections;
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
     * Gets a class section by ID.
     * 
     * @param classSectionId The ID of the class section to retrieve
     * @return The class section, or null if not found
     */
    public ClassSection getClassSectionById(String classSectionId) {
        return repository.getClassSection(classSectionId);
    }
}
