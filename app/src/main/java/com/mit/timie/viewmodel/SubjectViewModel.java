package com.mit.timie.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mit.timie.model.Subject;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for managing subject data and operations.
 * Handles subject CRUD operations with validation.
 */
public class SubjectViewModel extends ViewModel {
    
    private final TimetableRepository repository;
    private final MutableLiveData<List<Subject>> subjects;
    private final MutableLiveData<String> validationError;
    
    public SubjectViewModel() {
        this.repository = TimetableRepository.getInstance();
        this.subjects = new MutableLiveData<>(new ArrayList<>());
        this.validationError = new MutableLiveData<>();
        
        // Load existing subjects
        loadSubjects();
    }
    
    /**
     * Loads all subjects from the repository.
     */
    private void loadSubjects() {
        List<Subject> subjectList = repository.getAllSubjects();
        subjects.setValue(subjectList);
    }
    
    /**
     * Adds a new subject after validation.
     * 
     * @param subject The subject to add
     * @return true if validation passed and subject was added, false otherwise
     */
    public boolean addSubject(Subject subject) {
        String error = validateSubject(subject);
        
        if (error != null) {
            validationError.setValue(error);
            return false;
        }
        
        repository.addSubject(subject);
        loadSubjects();
        validationError.setValue(null);
        
        return true;
    }
    
    /**
     * Edits an existing subject after validation.
     * 
     * @param subject The subject with updated data
     * @return true if validation passed and subject was updated, false otherwise
     */
    public boolean editSubject(Subject subject) {
        String error = validateSubject(subject);
        
        if (error != null) {
            validationError.setValue(error);
            return false;
        }
        
        repository.updateSubject(subject);
        loadSubjects();
        validationError.setValue(null);
        
        return true;
    }
    
    /**
     * Deletes a subject from the repository.
     * 
     * @param subjectId The ID of the subject to delete
     */
    public void deleteSubject(String subjectId) {
        repository.deleteSubject(subjectId);
        loadSubjects();
    }
    
    /**
     * Validates subject data before saving.
     * 
     * @param subject The subject to validate
     * @return Error message if validation fails, null if valid
     */
    private String validateSubject(Subject subject) {
        if (subject == null) {
            return "Subject cannot be null";
        }
        
        if (subject.getId() == null || subject.getId().trim().isEmpty()) {
            return "Subject ID is required";
        }
        
        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            return "Subject name is required";
        }
        
        if (subject.getWeeklyPeriods() < 1 || subject.getWeeklyPeriods() > 30) {
            return "Weekly periods must be between 1 and 30";
        }
        
        if (subject.getType() == null) {
            return "Subject type is required";
        }
        
        if (subject.getPriority() == null) {
            return "Subject priority is required";
        }
        
        return null;
    }
    
    /**
     * Exposes the subject list as LiveData for UI observation.
     * 
     * @return LiveData containing the list of subjects
     */
    public LiveData<List<Subject>> getSubjects() {
        return subjects;
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
     * Gets a subject by ID.
     * 
     * @param subjectId The ID of the subject to retrieve
     * @return The subject, or null if not found
     */
    public Subject getSubjectById(String subjectId) {
        return repository.getSubject(subjectId);
    }
}
