package com.mit.timie.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Assignment;
import com.mit.timie.model.ClassSection;
import com.mit.timie.model.Config;
import com.mit.timie.model.ConflictReport;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;
import com.mit.timie.viewmodel.TimetableViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for viewing class timetables in a grid format.
 * Allows switching between class sections and timetable variants.
 * Handles cell clicks for editing options (lock, swap).
 */
public class ClassTimetableActivity extends AppCompatActivity implements TimetableGridAdapter.OnCellClickListener {
    
    private TimetableViewModel viewModel;
    private TimetableRepository repository;
    private ConfigRepository configRepository;
    
    private Spinner classSectionSpinner;
    private Spinner variantSpinner;
    private RecyclerView timetableRecyclerView;
    private Button viewFacultyButton;
    private Button viewSubjectDistributionButton;
    private Button editTimetableButton;
    
    private TimetableGridAdapter adapter;
    private Config config;
    
    private List<ClassSection> classSections;
    private List<Timetable> timetables;
    private ClassSection selectedClassSection;
    private Timetable selectedTimetable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_timetable);
        
        // Initialize repositories
        repository = TimetableRepository.getInstance();
        configRepository = ConfigRepository.getInstance();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(TimetableViewModel.class);
        
        // Get config
        config = configRepository.getConfig();
        
        if (config == null) {
            Toast.makeText(this, "Configuration not found. Please complete setup first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // Initialize views
        initializeViews();
        
        // Load data
        loadData();
        
        // Set up RecyclerView
        setupRecyclerView();
        
        // Set up spinners
        setupSpinners();
        
        // Observe ViewModel
        observeViewModel();
        
        // Set up listeners
        setupListeners();
    }
    
    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        classSectionSpinner = findViewById(R.id.classSectionSpinner);
        variantSpinner = findViewById(R.id.variantSpinner);
        timetableRecyclerView = findViewById(R.id.timetableRecyclerView);
        viewFacultyButton = findViewById(R.id.viewFacultyButton);
        viewSubjectDistributionButton = findViewById(R.id.viewSubjectDistributionButton);
        editTimetableButton = findViewById(R.id.editTimetableButton);
    }
    
    /**
     * Loads data from repositories.
     */
    private void loadData() {
        classSections = repository.getAllClassSections();
        timetables = repository.getAllTimetables();
        
        if (classSections.isEmpty()) {
            Toast.makeText(this, "No class sections found. Please add classes first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        if (timetables.isEmpty()) {
            Toast.makeText(this, "No timetables found. Please generate timetables first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
    
    /**
     * Sets up the RecyclerView with GridLayoutManager and adapter.
     */
    private void setupRecyclerView() {
        // Create adapter
        adapter = new TimetableGridAdapter(config, repository);
        adapter.setCellClickListener(this);
        
        // Set up GridLayoutManager with periods as columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, config.getPeriodsPerDay());
        timetableRecyclerView.setLayoutManager(layoutManager);
        timetableRecyclerView.setAdapter(adapter);
    }
    
    /**
     * Sets up the spinners for class section and variant selection.
     */
    private void setupSpinners() {
        // Set up class section spinner
        List<String> classSectionNames = new ArrayList<>();
        for (ClassSection classSection : classSections) {
            classSectionNames.add(classSection.getClassName() + " - " + classSection.getSectionName());
        }
        
        ArrayAdapter<String> classSectionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                classSectionNames
        );
        classSectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSectionSpinner.setAdapter(classSectionAdapter);
        
        // Set up variant spinner
        List<String> variantNames = new ArrayList<>();
        for (Timetable timetable : timetables) {
            variantNames.add(timetable.getVariantName());
        }
        
        ArrayAdapter<String> variantAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                variantNames
        );
        variantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        variantSpinner.setAdapter(variantAdapter);
        
        // Set up spinner listeners
        classSectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClassSection = classSections.get(position);
                updateTimetableDisplay();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        variantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimetable = timetables.get(position);
                updateTimetableDisplay();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    
    /**
     * Observes LiveData from ViewModel.
     */
    private void observeViewModel() {
        // Observe current timetable
        viewModel.getCurrentTimetable().observe(this, timetable -> {
            if (timetable != null) {
                adapter.setTimetable(timetable);
            }
        });
        
        // Observe edit conflicts
        viewModel.getEditConflicts().observe(this, conflictReport -> {
            if (conflictReport != null) {
                showConflictDialog(conflictReport);
            }
        });
    }
    
    /**
     * Sets up click listeners for buttons.
     */
    private void setupListeners() {
        viewFacultyButton.setOnClickListener(v -> {
            // Navigate to FacultyTimetableActivity
            Intent intent = new Intent(this, FacultyTimetableActivity.class);
            startActivity(intent);
        });
        
        viewSubjectDistributionButton.setOnClickListener(v -> {
            // TODO: Navigate to SubjectDistributionActivity when implemented (task 23)
            Toast.makeText(this, "Subject distribution view will be implemented in task 23", Toast.LENGTH_SHORT).show();
            
            // Placeholder for future implementation:
            // Intent intent = new Intent(this, SubjectDistributionActivity.class);
            // startActivity(intent);
        });
        
        editTimetableButton.setOnClickListener(v -> {
            // TODO: Navigate to EditTimetableActivity when implemented (task 25)
            Toast.makeText(this, "Edit timetable will be implemented in task 25", Toast.LENGTH_SHORT).show();
            
            // Placeholder for future implementation:
            // Intent intent = new Intent(this, EditTimetableActivity.class);
            // startActivity(intent);
        });
    }
    
    /**
     * Updates the timetable display when class section or variant changes.
     */
    private void updateTimetableDisplay() {
        if (selectedClassSection != null && selectedTimetable != null) {
            adapter.setClassSectionId(selectedClassSection.getId());
            adapter.setTimetable(selectedTimetable);
            viewModel.setCurrentTimetable(selectedTimetable);
        }
    }
    
    /**
     * Handles cell click events from the adapter.
     * Shows options to lock or swap the cell.
     * 
     * @param assignment The assignment in the clicked cell (null for free periods)
     * @param timeSlot The time slot of the clicked cell
     * @param position The position in the grid
     */
    @Override
    public void onCellClick(Assignment assignment, TimeSlot timeSlot, int position) {
        if (assignment == null) {
            // Free period - no actions available
            Toast.makeText(this, "Free period - no actions available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show options dialog
        String[] options;
        if (assignment.isLocked()) {
            options = new String[]{"Unlock Cell", "View Details"};
        } else {
            options = new String[]{"Lock Cell", "View Details"};
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Cell Options")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Lock/Unlock
                        if (assignment.isLocked()) {
                            viewModel.unlockAssignment(assignment);
                            Toast.makeText(this, "Cell unlocked", Toast.LENGTH_SHORT).show();
                        } else {
                            viewModel.lockAssignment(assignment);
                            Toast.makeText(this, "Cell locked", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyItemChanged(position);
                    } else {
                        // View details
                        showAssignmentDetails(assignment);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Shows a dialog displaying assignment details.
     * 
     * @param assignment The assignment to display
     */
    private void showAssignmentDetails(Assignment assignment) {
        String subjectName = repository.getSubject(assignment.getSubjectId()).getName();
        String facultyName = repository.getFaculty(assignment.getFacultyId()).getName();
        String roomName = repository.getRoom(assignment.getRoomId()).getName();
        
        String details = "Subject: " + subjectName + "\n" +
                "Faculty: " + facultyName + "\n" +
                "Room: " + roomName + "\n" +
                "Day: " + config.getDayNames().get(assignment.getTimeSlot().getDay()) + "\n" +
                "Period: " + (assignment.getTimeSlot().getPeriod() + 1) + "\n" +
                "Locked: " + (assignment.isLocked() ? "Yes" : "No");
        
        new AlertDialog.Builder(this)
                .setTitle("Assignment Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }
    
    /**
     * Shows a dialog displaying conflict details.
     * 
     * @param conflictReport The conflict report to display
     */
    private void showConflictDialog(ConflictReport conflictReport) {
        StringBuilder conflictDetails = new StringBuilder();
        if (conflictReport.getConflicts() != null) {
            for (String conflict : conflictReport.getConflicts()) {
                conflictDetails.append("• ").append(conflict).append("\n");
            }
        }
        
        String message = conflictDetails.toString();
        if (conflictReport.getSuggestion() != null && !conflictReport.getSuggestion().isEmpty()) {
            message += "\nSuggestion: " + conflictReport.getSuggestion();
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Conflict Detected")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> viewModel.clearConflicts())
                .show();
    }
}
