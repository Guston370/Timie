package com.mit.timie.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mit.timie.R;
import com.mit.timie.databinding.ActivitySetupWizardBinding;
import com.mit.timie.model.Config;
import com.mit.timie.viewmodel.SetupViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for the setup wizard that collects timetable configuration.
 * Allows administrators to configure working days, periods, and break times.
 */
public class SetupWizardActivity extends AppCompatActivity {

    private ActivitySetupWizardBinding binding;
    private SetupViewModel viewModel;
    private List<EditText> dayNameInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize ViewBinding
        binding = ActivitySetupWizardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        
        // Initialize day name inputs list
        dayNameInputs = new ArrayList<>();
        
        // Set up UI listeners
        setupListeners();
        
        // Observe ViewModel
        observeViewModel();
    }

    /**
     * Sets up listeners for UI elements.
     */
    private void setupListeners() {
        // Working days input listener
        binding.workingDaysInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        int days = Integer.parseInt(s.toString());
                        if (days >= 1 && days <= 7) {
                            generateDayNameInputs(days);
                            binding.workingDaysLayout.setError(null);
                        } else {
                            binding.workingDaysLayout.setError("Must be between 1 and 7");
                        }
                    } catch (NumberFormatException e) {
                        binding.workingDaysLayout.setError("Invalid number");
                    }
                }
            }
        });
        
        // Next button listener
        binding.nextButton.setOnClickListener(v -> onNextClicked());
    }

    /**
     * Observes ViewModel LiveData for changes.
     */
    private void observeViewModel() {
        // Observe configuration
        viewModel.getConfig().observe(this, config -> {
            if (config != null) {
                populateFields(config);
            }
        });
        
        // Observe validation errors
        viewModel.getValidationError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                displayError(error);
            } else {
                hideError();
            }
        });
    }

    /**
     * Generates input fields for day names based on the number of working days.
     * 
     * @param numberOfDays The number of working days
     */
    private void generateDayNameInputs(int numberOfDays) {
        // Clear existing inputs
        binding.dayNamesContainer.removeAllViews();
        dayNameInputs.clear();
        
        // Create new inputs
        for (int i = 0; i < numberOfDays; i++) {
            TextInputLayout inputLayout = new TextInputLayout(this);
            inputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            inputLayout.setHint(getString(R.string.day_name_hint, i + 1));
            
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) inputLayout.getLayoutParams();
            params.setMargins(0, 0, 0, 16);
            inputLayout.setLayoutParams(params);
            
            TextInputEditText editText = new TextInputEditText(this);
            editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            
            inputLayout.addView(editText);
            binding.dayNamesContainer.addView(inputLayout);
            dayNameInputs.add(editText);
        }
    }

    /**
     * Populates form fields with existing configuration data.
     * 
     * @param config The configuration to populate from
     */
    private void populateFields(Config config) {
        binding.workingDaysInput.setText(String.valueOf(config.getWorkingDays()));
        binding.periodsPerDayInput.setText(String.valueOf(config.getPeriodsPerDay()));
        binding.periodDurationInput.setText(String.valueOf(config.getPeriodDuration()));
        
        // Populate day names
        if (config.getDayNames() != null && !config.getDayNames().isEmpty()) {
            for (int i = 0; i < config.getDayNames().size() && i < dayNameInputs.size(); i++) {
                dayNameInputs.get(i).setText(config.getDayNames().get(i));
            }
        }
        
        // Populate break periods
        if (config.getBreakPeriods() != null && !config.getBreakPeriods().isEmpty()) {
            StringBuilder breakPeriods = new StringBuilder();
            for (int i = 0; i < config.getBreakPeriods().size(); i++) {
                breakPeriods.append(config.getBreakPeriods().get(i));
                if (i < config.getBreakPeriods().size() - 1) {
                    breakPeriods.append(",");
                }
            }
            binding.breakPeriodsInput.setText(breakPeriods.toString());
        }
    }

    /**
     * Handles the Next button click event.
     * Validates input and navigates to the next screen if valid.
     */
    private void onNextClicked() {
        // Collect input data
        Config config = collectConfigFromInputs();
        
        if (config == null) {
            displayError("Please fill in all required fields");
            return;
        }
        
        // Validate and save configuration
        boolean isValid = viewModel.saveConfig(config);
        
        if (isValid) {
            // Navigate to SubjectInputActivity
            navigateToSubjectInput();
        }
        // Error will be displayed via LiveData observer
    }

    /**
     * Collects configuration data from input fields.
     * 
     * @return Config object with input data, or null if inputs are invalid
     */
    private Config collectConfigFromInputs() {
        try {
            // Get working days
            String workingDaysStr = binding.workingDaysInput.getText().toString().trim();
            if (workingDaysStr.isEmpty()) {
                return null;
            }
            int workingDays = Integer.parseInt(workingDaysStr);
            
            // Get day names
            List<String> dayNames = new ArrayList<>();
            for (EditText input : dayNameInputs) {
                String dayName = input.getText().toString().trim();
                if (dayName.isEmpty()) {
                    return null;
                }
                dayNames.add(dayName);
            }
            
            // Get periods per day
            String periodsPerDayStr = binding.periodsPerDayInput.getText().toString().trim();
            if (periodsPerDayStr.isEmpty()) {
                return null;
            }
            int periodsPerDay = Integer.parseInt(periodsPerDayStr);
            
            // Get period duration
            String periodDurationStr = binding.periodDurationInput.getText().toString().trim();
            if (periodDurationStr.isEmpty()) {
                return null;
            }
            int periodDuration = Integer.parseInt(periodDurationStr);
            
            // Get break periods (optional)
            List<Integer> breakPeriods = new ArrayList<>();
            String breakPeriodsStr = binding.breakPeriodsInput.getText().toString().trim();
            if (!breakPeriodsStr.isEmpty()) {
                String[] breakPeriodTokens = breakPeriodsStr.split(",");
                for (String token : breakPeriodTokens) {
                    token = token.trim();
                    if (!token.isEmpty()) {
                        breakPeriods.add(Integer.parseInt(token));
                    }
                }
            }
            
            // Create and return config
            return new Config(workingDays, dayNames, periodsPerDay, periodDuration, breakPeriods);
            
        } catch (NumberFormatException e) {
            displayError("Invalid number format in one or more fields");
            return null;
        }
    }

    /**
     * Displays an error message to the user.
     * 
     * @param message The error message to display
     */
    private void displayError(String message) {
        binding.errorText.setText(message);
        binding.errorText.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the error message.
     */
    private void hideError() {
        binding.errorText.setVisibility(View.GONE);
    }

    /**
     * Navigates to the SubjectInputActivity.
     */
    private void navigateToSubjectInput() {
        Intent intent = new Intent(this, SubjectInputActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
