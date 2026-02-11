package com.mit.timie.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mit.timie.R;
import com.mit.timie.model.Config;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Subject;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.viewmodel.FacultyViewModel;
import com.mit.timie.viewmodel.SubjectViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Activity for faculty input.
 * Allows users to add, edit, and delete faculty members.
 * Validates that at least one faculty exists before proceeding.
 */
public class FacultyInputActivity extends AppCompatActivity implements FacultyAdapter.OnFacultyActionListener {
    
    private FacultyViewModel viewModel;
    private SubjectViewModel subjectViewModel;
    private FacultyAdapter adapter;
    private RecyclerView recyclerView;
    private Button nextButton;
    private FloatingActionButton addFacultyFab;
    private Config config;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_input);
        
        // Load configuration
        config = ConfigRepository.getInstance().getConfig();
        
        // Initialize ViewModels
        viewModel = new ViewModelProvider(this).get(FacultyViewModel.class);
        subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        
        // Initialize views
        initializeViews();
        
        // Set up RecyclerView
        setupRecyclerView();
        
        // Observe ViewModel
        observeViewModel();
        
        // Set up listeners
        setupListeners();
    }
    
    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        recyclerView = findViewById(R.id.facultiesRecyclerView);
        nextButton = findViewById(R.id.nextButton);
        addFacultyFab = findViewById(R.id.addFacultyFab);
    }
    
    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        adapter = new FacultyAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    /**
     * Observes LiveData from ViewModel.
     */
    private void observeViewModel() {
        // Observe faculty list
        viewModel.getFaculties().observe(this, faculties -> {
            adapter.setFaculties(faculties);
        });
        
        // Observe validation errors
        viewModel.getValidationError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    /**
     * Sets up click listeners for buttons.
     */
    private void setupListeners() {
        addFacultyFab.setOnClickListener(v -> showFacultyDialog(null));
        
        nextButton.setOnClickListener(v -> {
            if (validateAndProceed()) {
                navigateToRoomInput();
            }
        });
    }
    
    /**
     * Shows dialog for adding or editing a faculty member.
     * 
     * @param faculty The faculty to edit, or null to add a new faculty
     */
    private void showFacultyDialog(Faculty faculty) {
        boolean isEdit = faculty != null;
        
        // Inflate dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_faculty_input, null);
        
        // Get dialog views
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextInputLayout facultyNameLayout = dialogView.findViewById(R.id.facultyNameLayout);
        TextInputEditText facultyNameInput = dialogView.findViewById(R.id.facultyNameInput);
        TextInputLayout maxPeriodsLayout = dialogView.findViewById(R.id.maxPeriodsLayout);
        TextInputEditText maxPeriodsInput = dialogView.findViewById(R.id.maxPeriodsInput);
        LinearLayout subjectsContainer = dialogView.findViewById(R.id.subjectsContainer);
        LinearLayout availabilityContainer = dialogView.findViewById(R.id.availabilityContainer);
        CheckBox avoidConsecutiveCheckbox = dialogView.findViewById(R.id.avoidConsecutiveCheckbox);
        TextView errorText = dialogView.findViewById(R.id.errorText);
        
        // Set dialog title
        dialogTitle.setText(isEdit ? R.string.edit_faculty_title : R.string.add_faculty_title);
        
        // Set up subjects checkboxes
        List<CheckBox> subjectCheckboxes = setupSubjectsCheckboxes(subjectsContainer);
        
        // Set up availability checkboxes
        Map<Integer, List<CheckBox>> availabilityCheckboxes = setupAvailabilityCheckboxes(availabilityContainer);
        
        // Populate fields if editing
        if (isEdit) {
            facultyNameInput.setText(faculty.getName());
            maxPeriodsInput.setText(String.valueOf(faculty.getMaxPeriodsPerDay()));
            avoidConsecutiveCheckbox.setChecked(faculty.isAvoidConsecutive());
            
            // Check selected subjects
            for (CheckBox checkbox : subjectCheckboxes) {
                String subjectId = (String) checkbox.getTag();
                if (faculty.getSubjectIds().contains(subjectId)) {
                    checkbox.setChecked(true);
                }
            }
            
            // Check selected availability
            Map<Integer, List<Integer>> availability = faculty.getAvailability();
            for (Map.Entry<Integer, List<CheckBox>> entry : availabilityCheckboxes.entrySet()) {
                int day = entry.getKey();
                List<CheckBox> periodCheckboxes = entry.getValue();
                
                if (availability.containsKey(day)) {
                    List<Integer> availablePeriods = availability.get(day);
                    for (CheckBox checkbox : periodCheckboxes) {
                        int period = (int) checkbox.getTag();
                        if (availablePeriods.contains(period)) {
                            checkbox.setChecked(true);
                        }
                    }
                }
            }
        }
        
        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        
        // Set up button listeners
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        
        saveButton.setOnClickListener(v -> {
            // Clear previous errors
            facultyNameLayout.setError(null);
            maxPeriodsLayout.setError(null);
            errorText.setVisibility(View.GONE);
            
            // Get input values
            String name = facultyNameInput.getText() != null ? facultyNameInput.getText().toString().trim() : "";
            String maxPeriodsStr = maxPeriodsInput.getText() != null ? maxPeriodsInput.getText().toString().trim() : "";
            
            // Validate inputs
            boolean valid = true;
            
            if (name.isEmpty()) {
                facultyNameLayout.setError("Faculty name is required");
                valid = false;
            }
            
            if (maxPeriodsStr.isEmpty()) {
                maxPeriodsLayout.setError("Max periods per day is required");
                valid = false;
            }
            
            int maxPeriods = 0;
            if (!maxPeriodsStr.isEmpty()) {
                try {
                    maxPeriods = Integer.parseInt(maxPeriodsStr);
                    if (maxPeriods < 1 || maxPeriods > 12) {
                        maxPeriodsLayout.setError("Must be between 1 and 12");
                        valid = false;
                    }
                } catch (NumberFormatException e) {
                    maxPeriodsLayout.setError("Invalid number");
                    valid = false;
                }
            }
            
            // Get selected subjects
            List<String> selectedSubjects = new ArrayList<>();
            for (CheckBox checkbox : subjectCheckboxes) {
                if (checkbox.isChecked()) {
                    selectedSubjects.add((String) checkbox.getTag());
                }
            }
            
            if (selectedSubjects.isEmpty()) {
                errorText.setText("Please select at least one subject");
                errorText.setVisibility(View.VISIBLE);
                valid = false;
            }
            
            // Get selected availability
            Map<Integer, List<Integer>> selectedAvailability = new HashMap<>();
            for (Map.Entry<Integer, List<CheckBox>> entry : availabilityCheckboxes.entrySet()) {
                int day = entry.getKey();
                List<Integer> periods = new ArrayList<>();
                
                for (CheckBox checkbox : entry.getValue()) {
                    if (checkbox.isChecked()) {
                        periods.add((int) checkbox.getTag());
                    }
                }
                
                if (!periods.isEmpty()) {
                    selectedAvailability.put(day, periods);
                }
            }
            
            if (selectedAvailability.isEmpty()) {
                errorText.setText("Please select at least one available time slot");
                errorText.setVisibility(View.VISIBLE);
                valid = false;
            }
            
            if (!valid) {
                return;
            }
            
            // Create or update faculty
            Faculty facultyToSave = isEdit ? faculty : new Faculty();
            if (!isEdit) {
                facultyToSave.setId(UUID.randomUUID().toString());
            }
            
            facultyToSave.setName(name);
            facultyToSave.setMaxPeriodsPerDay(maxPeriods);
            facultyToSave.setSubjectIds(selectedSubjects);
            facultyToSave.setAvailability(selectedAvailability);
            facultyToSave.setAvoidConsecutive(avoidConsecutiveCheckbox.isChecked());
            
            // Save to ViewModel
            boolean success = isEdit ? viewModel.editFaculty(facultyToSave) : viewModel.addFaculty(facultyToSave);
            
            if (success) {
                dialog.dismiss();
                Toast.makeText(this, isEdit ? "Faculty updated" : "Faculty added", Toast.LENGTH_SHORT).show();
            } else {
                errorText.setText(viewModel.getValidationError().getValue());
                errorText.setVisibility(View.VISIBLE);
            }
        });
        
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * Sets up checkboxes for subject selection.
     * 
     * @param container The container to add checkboxes to
     * @return List of created checkboxes
     */
    private List<CheckBox> setupSubjectsCheckboxes(LinearLayout container) {
        List<CheckBox> checkboxes = new ArrayList<>();
        List<Subject> subjects = subjectViewModel.getSubjects().getValue();
        
        if (subjects == null || subjects.isEmpty()) {
            TextView noSubjectsText = new TextView(this);
            noSubjectsText.setText("No subjects available. Please add subjects first.");
            noSubjectsText.setPadding(8, 8, 8, 8);
            container.addView(noSubjectsText);
            return checkboxes;
        }
        
        for (Subject subject : subjects) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(subject.getName());
            checkbox.setTag(subject.getId());
            checkbox.setPadding(8, 8, 8, 8);
            container.addView(checkbox);
            checkboxes.add(checkbox);
        }
        
        return checkboxes;
    }
    
    /**
     * Sets up checkboxes for day-wise availability.
     * 
     * @param container The container to add checkboxes to
     * @return Map of day to list of period checkboxes
     */
    private Map<Integer, List<CheckBox>> setupAvailabilityCheckboxes(LinearLayout container) {
        Map<Integer, List<CheckBox>> checkboxMap = new HashMap<>();
        
        if (config == null) {
            TextView errorText = new TextView(this);
            errorText.setText("Configuration not found. Please complete setup first.");
            errorText.setPadding(8, 8, 8, 8);
            container.addView(errorText);
            return checkboxMap;
        }
        
        List<String> dayNames = config.getDayNames();
        int periodsPerDay = config.getPeriodsPerDay();
        List<Integer> breakPeriods = config.getBreakPeriods();
        
        for (int day = 0; day < config.getWorkingDays(); day++) {
            // Day header
            TextView dayHeader = new TextView(this);
            String dayName = day < dayNames.size() ? dayNames.get(day) : "Day " + (day + 1);
            dayHeader.setText(dayName);
            dayHeader.setTextSize(16);
            dayHeader.setTextColor(getResources().getColor(android.R.color.black));
            dayHeader.setPadding(8, 16, 8, 8);
            dayHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            container.addView(dayHeader);
            
            // Period checkboxes container
            LinearLayout periodsLayout = new LinearLayout(this);
            periodsLayout.setOrientation(LinearLayout.HORIZONTAL);
            periodsLayout.setPadding(16, 0, 0, 8);
            
            LinearLayout periodsColumn1 = new LinearLayout(this);
            periodsColumn1.setOrientation(LinearLayout.VERTICAL);
            periodsColumn1.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            
            LinearLayout periodsColumn2 = new LinearLayout(this);
            periodsColumn2.setOrientation(LinearLayout.VERTICAL);
            periodsColumn2.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            
            List<CheckBox> dayCheckboxes = new ArrayList<>();
            
            for (int period = 0; period < periodsPerDay; period++) {
                CheckBox checkbox = new CheckBox(this);
                String periodLabel = "Period " + (period + 1);
                
                if (breakPeriods.contains(period)) {
                    periodLabel += " (Break)";
                    checkbox.setEnabled(false);
                }
                
                checkbox.setText(periodLabel);
                checkbox.setTag(period);
                checkbox.setPadding(4, 4, 4, 4);
                
                // Add to appropriate column
                if (period % 2 == 0) {
                    periodsColumn1.addView(checkbox);
                } else {
                    periodsColumn2.addView(checkbox);
                }
                
                if (!breakPeriods.contains(period)) {
                    dayCheckboxes.add(checkbox);
                }
            }
            
            periodsLayout.addView(periodsColumn1);
            periodsLayout.addView(periodsColumn2);
            container.addView(periodsLayout);
            
            checkboxMap.put(day, dayCheckboxes);
        }
        
        return checkboxMap;
    }
    
    /**
     * Validates that at least one faculty exists before proceeding.
     * 
     * @return true if validation passes, false otherwise
     */
    private boolean validateAndProceed() {
        if (viewModel.getFaculties().getValue() == null || 
                viewModel.getFaculties().getValue().isEmpty()) {
            Toast.makeText(this, R.string.validation_error_no_faculties, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    
    /**
     * Navigates to the Room Input Activity.
     */
    private void navigateToRoomInput() {
        Intent intent = new Intent(this, RoomInputActivity.class);
        startActivity(intent);
    }
    
    @Override
    public void onEditFaculty(Faculty faculty) {
        showFacultyDialog(faculty);
    }
    
    @Override
    public void onDeleteFaculty(Faculty faculty) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Faculty")
                .setMessage("Are you sure you want to delete " + faculty.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteFaculty(faculty.getId());
                    Toast.makeText(this, "Faculty deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
