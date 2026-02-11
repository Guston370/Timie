package com.mit.timie.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
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
import com.mit.timie.model.Subject;
import com.mit.timie.viewmodel.SubjectViewModel;

import java.util.UUID;

/**
 * Activity for subject input.
 * Allows users to add, edit, and delete subjects.
 * Validates that at least one subject exists before proceeding.
 */
public class SubjectInputActivity extends AppCompatActivity implements SubjectAdapter.OnSubjectActionListener {
    
    private SubjectViewModel viewModel;
    private SubjectAdapter adapter;
    private RecyclerView recyclerView;
    private Button nextButton;
    private FloatingActionButton addSubjectFab;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_input);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        
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
        recyclerView = findViewById(R.id.subjectsRecyclerView);
        nextButton = findViewById(R.id.nextButton);
        addSubjectFab = findViewById(R.id.addSubjectFab);
    }
    
    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        adapter = new SubjectAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    /**
     * Observes LiveData from ViewModel.
     */
    private void observeViewModel() {
        // Observe subject list
        viewModel.getSubjects().observe(this, subjects -> {
            adapter.setSubjects(subjects);
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
        addSubjectFab.setOnClickListener(v -> showSubjectDialog(null));
        
        nextButton.setOnClickListener(v -> {
            if (validateAndProceed()) {
                navigateToFacultyInput();
            }
        });
    }
    
    /**
     * Shows dialog for adding or editing a subject.
     * 
     * @param subject The subject to edit, or null to add a new subject
     */
    private void showSubjectDialog(Subject subject) {
        boolean isEdit = subject != null;
        
        // Inflate dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_subject_input, null);
        
        // Get dialog views
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextInputLayout subjectNameLayout = dialogView.findViewById(R.id.subjectNameLayout);
        TextInputEditText subjectNameInput = dialogView.findViewById(R.id.subjectNameInput);
        TextInputLayout weeklyPeriodsLayout = dialogView.findViewById(R.id.weeklyPeriodsLayout);
        TextInputEditText weeklyPeriodsInput = dialogView.findViewById(R.id.weeklyPeriodsInput);
        Spinner subjectTypeSpinner = dialogView.findViewById(R.id.subjectTypeSpinner);
        Spinner prioritySpinner = dialogView.findViewById(R.id.prioritySpinner);
        CheckBox allowRepetitionCheckbox = dialogView.findViewById(R.id.allowRepetitionCheckbox);
        TextView errorText = dialogView.findViewById(R.id.errorText);
        
        // Set dialog title
        dialogTitle.setText(isEdit ? R.string.edit_subject_title : R.string.add_subject_title);
        
        // Set up spinners
        setupSubjectTypeSpinner(subjectTypeSpinner);
        setupPrioritySpinner(prioritySpinner);
        
        // Populate fields if editing
        if (isEdit) {
            subjectNameInput.setText(subject.getName());
            weeklyPeriodsInput.setText(String.valueOf(subject.getWeeklyPeriods()));
            subjectTypeSpinner.setSelection(subject.getType() == Subject.SubjectType.LAB ? 1 : 0);
            prioritySpinner.setSelection(getPriorityIndex(subject.getPriority()));
            allowRepetitionCheckbox.setChecked(subject.isAllowRepetition());
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
            subjectNameLayout.setError(null);
            weeklyPeriodsLayout.setError(null);
            errorText.setVisibility(View.GONE);
            
            // Get input values
            String name = subjectNameInput.getText() != null ? subjectNameInput.getText().toString().trim() : "";
            String periodsStr = weeklyPeriodsInput.getText() != null ? weeklyPeriodsInput.getText().toString().trim() : "";
            
            // Validate inputs
            boolean valid = true;
            
            if (name.isEmpty()) {
                subjectNameLayout.setError("Subject name is required");
                valid = false;
            }
            
            if (periodsStr.isEmpty()) {
                weeklyPeriodsLayout.setError("Weekly periods is required");
                valid = false;
            }
            
            int weeklyPeriods = 0;
            if (!periodsStr.isEmpty()) {
                try {
                    weeklyPeriods = Integer.parseInt(periodsStr);
                    if (weeklyPeriods < 1 || weeklyPeriods > 30) {
                        weeklyPeriodsLayout.setError("Must be between 1 and 30");
                        valid = false;
                    }
                } catch (NumberFormatException e) {
                    weeklyPeriodsLayout.setError("Invalid number");
                    valid = false;
                }
            }
            
            if (!valid) {
                return;
            }
            
            // Create or update subject
            Subject subjectToSave = isEdit ? subject : new Subject();
            if (!isEdit) {
                subjectToSave.setId(UUID.randomUUID().toString());
            }
            
            subjectToSave.setName(name);
            subjectToSave.setWeeklyPeriods(weeklyPeriods);
            subjectToSave.setType(subjectTypeSpinner.getSelectedItemPosition() == 0 ? 
                    Subject.SubjectType.THEORY : Subject.SubjectType.LAB);
            subjectToSave.setPriority(getPriorityFromIndex(prioritySpinner.getSelectedItemPosition()));
            subjectToSave.setAllowRepetition(allowRepetitionCheckbox.isChecked());
            
            // Save to ViewModel
            boolean success = isEdit ? viewModel.editSubject(subjectToSave) : viewModel.addSubject(subjectToSave);
            
            if (success) {
                dialog.dismiss();
                Toast.makeText(this, isEdit ? "Subject updated" : "Subject added", Toast.LENGTH_SHORT).show();
            } else {
                errorText.setText(viewModel.getValidationError().getValue());
                errorText.setVisibility(View.VISIBLE);
            }
        });
        
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * Sets up the subject type spinner with options.
     * 
     * @param spinner The spinner to set up
     */
    private void setupSubjectTypeSpinner(Spinner spinner) {
        String[] types = {"Theory", "Lab"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    
    /**
     * Sets up the priority spinner with options.
     * 
     * @param spinner The spinner to set up
     */
    private void setupPrioritySpinner(Spinner spinner) {
        String[] priorities = {"High", "Medium", "Low"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    
    /**
     * Gets the spinner index for a priority.
     * 
     * @param priority The priority
     * @return The spinner index
     */
    private int getPriorityIndex(Subject.Priority priority) {
        if (priority == null) return 1; // Default to Medium
        
        switch (priority) {
            case HIGH:
                return 0;
            case MEDIUM:
                return 1;
            case LOW:
                return 2;
            default:
                return 1;
        }
    }
    
    /**
     * Gets the priority from a spinner index.
     * 
     * @param index The spinner index
     * @return The priority
     */
    private Subject.Priority getPriorityFromIndex(int index) {
        switch (index) {
            case 0:
                return Subject.Priority.HIGH;
            case 1:
                return Subject.Priority.MEDIUM;
            case 2:
                return Subject.Priority.LOW;
            default:
                return Subject.Priority.MEDIUM;
        }
    }
    
    /**
     * Validates that at least one subject exists before proceeding.
     * 
     * @return true if validation passes, false otherwise
     */
    private boolean validateAndProceed() {
        if (viewModel.getSubjects().getValue() == null || 
                viewModel.getSubjects().getValue().isEmpty()) {
            Toast.makeText(this, R.string.validation_error_no_subjects, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    
    /**
     * Navigates to the Faculty Input Activity.
     */
    private void navigateToFacultyInput() {
        Intent intent = new Intent(this, FacultyInputActivity.class);
        startActivity(intent);
    }
    
    @Override
    public void onEditSubject(Subject subject) {
        showSubjectDialog(subject);
    }
    
    @Override
    public void onDeleteSubject(Subject subject) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Subject")
                .setMessage("Are you sure you want to delete " + subject.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteSubject(subject.getId());
                    Toast.makeText(this, "Subject deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
