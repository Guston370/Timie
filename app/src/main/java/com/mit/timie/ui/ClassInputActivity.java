package com.mit.timie.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.mit.timie.model.ClassSection;
import com.mit.timie.viewmodel.ClassViewModel;

import java.util.UUID;

/**
 * Activity for class and section input.
 * Allows users to add, edit, and delete class sections.
 * Validates that at least one class section exists before proceeding to generation.
 */
public class ClassInputActivity extends AppCompatActivity implements ClassSectionAdapter.OnClassSectionActionListener {
    
    private ClassViewModel viewModel;
    private ClassSectionAdapter adapter;
    private RecyclerView recyclerView;
    private Button generateTimetableButton;
    private FloatingActionButton addClassFab;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_input);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ClassViewModel.class);
        
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
        recyclerView = findViewById(R.id.classesRecyclerView);
        generateTimetableButton = findViewById(R.id.generateTimetableButton);
        addClassFab = findViewById(R.id.addClassFab);
    }
    
    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        adapter = new ClassSectionAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    /**
     * Observes LiveData from ViewModel.
     */
    private void observeViewModel() {
        // Observe class section list
        viewModel.getClassSections().observe(this, classSections -> {
            adapter.setClassSections(classSections);
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
        addClassFab.setOnClickListener(v -> showClassDialog(null));
        
        generateTimetableButton.setOnClickListener(v -> {
            if (validateAndProceed()) {
                navigateToGeneration();
            }
        });
    }
    
    /**
     * Shows dialog for adding or editing a class section.
     * 
     * @param classSection The class section to edit, or null to add a new class section
     */
    private void showClassDialog(ClassSection classSection) {
        boolean isEdit = classSection != null;
        
        // Inflate dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_class_input, null);
        
        // Get dialog views
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextInputLayout classNameLayout = dialogView.findViewById(R.id.classNameLayout);
        TextInputEditText classNameInput = dialogView.findViewById(R.id.classNameInput);
        TextInputLayout sectionNameLayout = dialogView.findViewById(R.id.sectionNameLayout);
        TextInputEditText sectionNameInput = dialogView.findViewById(R.id.sectionNameInput);
        TextInputLayout studentStrengthLayout = dialogView.findViewById(R.id.studentStrengthLayout);
        TextInputEditText studentStrengthInput = dialogView.findViewById(R.id.studentStrengthInput);
        TextView errorText = dialogView.findViewById(R.id.errorText);
        
        // Set dialog title
        dialogTitle.setText(isEdit ? R.string.edit_class_title : R.string.add_class_title);
        
        // Populate fields if editing
        if (isEdit) {
            classNameInput.setText(classSection.getClassName());
            sectionNameInput.setText(classSection.getSectionName());
            studentStrengthInput.setText(String.valueOf(classSection.getStudentStrength()));
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
            classNameLayout.setError(null);
            sectionNameLayout.setError(null);
            studentStrengthLayout.setError(null);
            errorText.setVisibility(View.GONE);
            
            // Get input values
            String className = classNameInput.getText() != null ? classNameInput.getText().toString().trim() : "";
            String sectionName = sectionNameInput.getText() != null ? sectionNameInput.getText().toString().trim() : "";
            String strengthStr = studentStrengthInput.getText() != null ? studentStrengthInput.getText().toString().trim() : "";
            
            // Validate inputs
            boolean valid = true;
            
            if (className.isEmpty()) {
                classNameLayout.setError("Class name is required");
                valid = false;
            }
            
            if (sectionName.isEmpty()) {
                sectionNameLayout.setError("Section name is required");
                valid = false;
            }
            
            if (strengthStr.isEmpty()) {
                studentStrengthLayout.setError("Student strength is required");
                valid = false;
            }
            
            int studentStrength = 0;
            if (!strengthStr.isEmpty()) {
                try {
                    studentStrength = Integer.parseInt(strengthStr);
                    if (studentStrength < 1) {
                        studentStrengthLayout.setError("Must be at least 1");
                        valid = false;
                    }
                } catch (NumberFormatException e) {
                    studentStrengthLayout.setError("Invalid number");
                    valid = false;
                }
            }
            
            if (!valid) {
                return;
            }
            
            // Create or update class section
            ClassSection classSectionToSave = isEdit ? classSection : new ClassSection();
            if (!isEdit) {
                classSectionToSave.setId(UUID.randomUUID().toString());
            }
            
            classSectionToSave.setClassName(className);
            classSectionToSave.setSectionName(sectionName);
            classSectionToSave.setStudentStrength(studentStrength);
            
            // Save to ViewModel
            boolean success = isEdit ? viewModel.editClass(classSectionToSave) : viewModel.addClass(classSectionToSave);
            
            if (success) {
                dialog.dismiss();
                Toast.makeText(this, isEdit ? "Class section updated" : "Class section added", Toast.LENGTH_SHORT).show();
            } else {
                errorText.setText(viewModel.getValidationError().getValue());
                errorText.setVisibility(View.VISIBLE);
            }
        });
        
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * Validates that at least one class section exists before proceeding.
     * 
     * @return true if validation passes, false otherwise
     */
    private boolean validateAndProceed() {
        if (viewModel.getClassSections().getValue() == null || 
                viewModel.getClassSections().getValue().isEmpty()) {
            Toast.makeText(this, R.string.validation_error_no_classes, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    
    /**
     * Navigates to the Generation Activity.
     */
    private void navigateToGeneration() {
        Intent intent = new Intent(this, GenerationActivity.class);
        startActivity(intent);
    }
    
    @Override
    public void onEditClassSection(ClassSection classSection) {
        showClassDialog(classSection);
    }
    
    @Override
    public void onDeleteClassSection(ClassSection classSection) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class Section")
                .setMessage("Are you sure you want to delete " + classSection.getClassName() + 
                        " - Section " + classSection.getSectionName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteClass(classSection.getId());
                    Toast.makeText(this, "Class section deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
