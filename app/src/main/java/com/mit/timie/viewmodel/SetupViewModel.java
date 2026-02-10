package com.mit.timie.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mit.timie.model.Config;
import com.mit.timie.repository.ConfigRepository;

/**
 * ViewModel for managing setup wizard configuration state.
 * Handles configuration data validation and persistence.
 */
public class SetupViewModel extends ViewModel {
    
    private final ConfigRepository configRepository;
    private final MutableLiveData<Config> config;
    private final MutableLiveData<String> validationError;
    
    public SetupViewModel() {
        this.configRepository = ConfigRepository.getInstance();
        this.config = new MutableLiveData<>();
        this.validationError = new MutableLiveData<>();
        
        // Load existing config if available
        Config existingConfig = configRepository.getConfig();
        if (existingConfig != null) {
            config.setValue(existingConfig);
        }
    }
    
    /**
     * Saves the configuration after validation.
     * 
     * @param config The configuration to save
     * @return true if validation passed and config was saved, false otherwise
     */
    public boolean saveConfig(Config config) {
        // Validate configuration
        String error = validateConfig(config);
        
        if (error != null) {
            validationError.setValue(error);
            return false;
        }
        
        // Save to repository
        configRepository.saveConfig(config);
        this.config.setValue(config);
        validationError.setValue(null);
        
        return true;
    }
    
    /**
     * Validates the configuration to ensure at least one teaching period exists per day.
     * 
     * @param config The configuration to validate
     * @return Error message if validation fails, null if valid
     */
    private String validateConfig(Config config) {
        if (config == null) {
            return "Configuration cannot be null";
        }
        
        if (config.getWorkingDays() < 1 || config.getWorkingDays() > 7) {
            return "Working days must be between 1 and 7";
        }
        
        if (config.getPeriodsPerDay() < 1 || config.getPeriodsPerDay() > 12) {
            return "Periods per day must be between 1 and 12";
        }
        
        if (config.getPeriodDuration() <= 0) {
            return "Period duration must be greater than 0";
        }
        
        if (config.getDayNames() == null || config.getDayNames().size() != config.getWorkingDays()) {
            return "Day names must match the number of working days";
        }
        
        // Validate that at least one teaching period exists per day
        if (config.getBreakPeriods() != null) {
            int breakCount = config.getBreakPeriods().size();
            int teachingPeriods = config.getPeriodsPerDay() - breakCount;
            
            if (teachingPeriods < 1) {
                return "At least one teaching period must exist per day";
            }
        }
        
        return null;
    }
    
    /**
     * Exposes the configuration as LiveData for UI observation.
     * 
     * @return LiveData containing the current configuration
     */
    public LiveData<Config> getConfig() {
        return config;
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
     * Updates the configuration without saving to repository.
     * Useful for temporary updates during user input.
     * 
     * @param config The configuration to set
     */
    public void setConfig(Config config) {
        this.config.setValue(config);
    }
}
