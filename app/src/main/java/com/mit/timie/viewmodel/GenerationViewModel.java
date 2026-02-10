package com.mit.timie.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mit.timie.algorithm.TimetableGenerator;
import com.mit.timie.model.ConflictReport;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for managing timetable generation process.
 * Handles generation on background thread and exposes results via LiveData.
 */
public class GenerationViewModel extends ViewModel {
    
    private final TimetableGenerator generator;
    private final TimetableRepository repository;
    
    private final MutableLiveData<List<Timetable>> timetables;
    private final MutableLiveData<Boolean> isGenerating;
    private final MutableLiveData<ConflictReport> conflicts;
    
    public GenerationViewModel() {
        this.repository = TimetableRepository.getInstance();
        ConfigRepository configRepository = ConfigRepository.getInstance();
        this.generator = new TimetableGenerator(repository, configRepository);
        
        this.timetables = new MutableLiveData<>(new ArrayList<>());
        this.isGenerating = new MutableLiveData<>(false);
        this.conflicts = new MutableLiveData<>();
    }
    
    /**
     * Generates timetable variants on a background thread.
     * Posts results or conflicts to LiveData when complete.
     */
    public void generateTimetables() {
        isGenerating.setValue(true);
        
        // Run generation on background thread
        new Thread(() -> {
            try {
                List<Timetable> variants = generator.generateVariants();
                
                if (variants == null || variants.isEmpty()) {
                    // Generation failed - create conflict report
                    ConflictReport report = new ConflictReport();
                    report.addConflict("Unable to generate valid timetable with current constraints");
                    report.setSuggestion("Try adjusting faculty availability, room availability, or subject requirements");
                    conflicts.postValue(report);
                    timetables.postValue(new ArrayList<>());
                } else {
                    // Generation successful - save and post timetables
                    for (Timetable timetable : variants) {
                        repository.saveTimetable(timetable);
                    }
                    timetables.postValue(variants);
                    conflicts.postValue(null);
                }
            } catch (Exception e) {
                // Handle generation errors
                ConflictReport report = new ConflictReport();
                report.addConflict("Generation failed: " + e.getMessage());
                report.setSuggestion("Check that all required data (subjects, faculty, rooms, classes) is properly configured");
                conflicts.postValue(report);
                timetables.postValue(new ArrayList<>());
            } finally {
                isGenerating.postValue(false);
            }
        }).start();
    }
    
    /**
     * Exposes the generated timetables as LiveData for UI observation.
     * 
     * @return LiveData containing the list of generated timetable variants
     */
    public LiveData<List<Timetable>> getTimetables() {
        return timetables;
    }
    
    /**
     * Exposes the generation loading state as LiveData for UI observation.
     * 
     * @return LiveData containing the loading state (true if generating, false otherwise)
     */
    public LiveData<Boolean> getIsGenerating() {
        return isGenerating;
    }
    
    /**
     * Exposes generation conflicts as LiveData for UI observation.
     * 
     * @return LiveData containing conflict reports when generation fails
     */
    public LiveData<ConflictReport> getConflicts() {
        return conflicts;
    }
    
    /**
     * Clears the current generation results and conflicts.
     */
    public void clearResults() {
        timetables.setValue(new ArrayList<>());
        conflicts.setValue(null);
    }
}
