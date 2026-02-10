package com.mit.timie.repository;

import com.mit.timie.model.Config;

/**
 * Repository for managing configuration data using in-memory storage.
 * Implements singleton pattern to ensure single instance across the application.
 */
public class ConfigRepository {
    private static ConfigRepository instance;
    
    private Config config;
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private ConfigRepository() {
        config = null;
    }
    
    /**
     * Get the singleton instance of ConfigRepository.
     * @return The singleton instance
     */
    public static synchronized ConfigRepository getInstance() {
        if (instance == null) {
            instance = new ConfigRepository();
        }
        return instance;
    }
    
    /**
     * Save the configuration.
     * @param config The configuration to save
     */
    public void saveConfig(Config config) {
        this.config = config;
    }
    
    /**
     * Get the saved configuration.
     * @return The configuration, or null if not set
     */
    public Config getConfig() {
        return config;
    }
    
    /**
     * Check if configuration exists.
     * @return true if configuration is set, false otherwise
     */
    public boolean hasConfig() {
        return config != null;
    }
    
    /**
     * Clear the configuration.
     */
    public void clearConfig() {
        config = null;
    }
}
