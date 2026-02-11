package com.mit.timie.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Config;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;
import com.mit.timie.viewmodel.TimetableViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for viewing faculty timetables in a grid format.
 * Displays the teacher's schedule across all days and periods.
 * Shows class, section, subject, and room for each assigned period.
 * Displays daily workload summary.
 */
public class FacultyTimetableActivity extends AppCompatActivity {
    
    private TimetableViewModel viewModel;
    private TimetableRepository repository;
    private ConfigRepository configRepository;
    
    private Spinner facultySpinner;
    private TextView workloadSummaryText;
    private RecyclerView facultyScheduleRecyclerView;
    private Button backButton;
    
    private FacultyTimetableAdapter adapter;
    private Config config;
    
    private List<Faculty> faculties;
    private Timetable currentTimetable;
    private Faculty selectedFaculty;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_timetable);
        
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
        
        // Set up spinner
        setupSpinner();
        
        // Observe ViewModel
        observeViewModel();
        
        // Set up listeners
        setupListeners();
    }
    
    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        facultySpinner = findViewById(R.id.facultySpinner);
        workloadSummaryText = findViewById(R.id.workloadSummaryText);
        facultyScheduleRecyclerView = findViewById(R.id.facultyScheduleRecyclerView);
        backButton = findViewById(R.id.backButton);
    }
    
    /**
     * Loads data from repositories.
     */
    private void loadData() {
        faculties = repository.getAllFaculties();
        
        if (faculties.isEmpty()) {
            Toast.makeText(this, "No faculty members found. Please add faculty first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // Get current timetable from ViewModel or use first available
        currentTimetable = viewModel.getCurrentTimetable().getValue();
        if (currentTimetable == null) {
            List<Timetable> timetables = repository.getAllTimetables();
            if (!timetables.isEmpty()) {
                currentTimetable = timetables.get(0);
                viewModel.setCurrentTimetable(currentTimetable);
            } else {
                Toast.makeText(this, "No timetables found. Please generate timetables first.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
    }
    
    /**
     * Sets up the RecyclerView with GridLayoutManager and adapter.
     */
    private void setupRecyclerView() {
        // Create adapter
        adapter = new FacultyTimetableAdapter(config, repository);
        
        // Set up GridLayoutManager with periods as columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, config.getPeriodsPerDay());
        facultyScheduleRecyclerView.setLayoutManager(layoutManager);
        facultyScheduleRecyclerView.setAdapter(adapter);
    }
    
    /**
     * Sets up the spinner for faculty selection.
     */
    private void setupSpinner() {
        // Set up faculty spinner
        List<String> facultyNames = new ArrayList<>();
        for (Faculty faculty : faculties) {
            facultyNames.add(faculty.getName());
        }
        
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                facultyNames
        );
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facultySpinner.setAdapter(facultyAdapter);
        
        // Set up spinner listener
        facultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFaculty = faculties.get(position);
                updateFacultySchedule();
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
                currentTimetable = timetable;
                adapter.setTimetable(timetable);
                updateWorkloadSummary();
            }
        });
    }
    
    /**
     * Sets up click listeners for buttons.
     */
    private void setupListeners() {
        backButton.setOnClickListener(v -> {
            // Go back to previous activity
            finish();
        });
    }
    
    /**
     * Updates the faculty schedule display when faculty selection changes.
     */
    private void updateFacultySchedule() {
        if (selectedFaculty != null && currentTimetable != null) {
            adapter.setFacultyId(selectedFaculty.getId());
            adapter.setTimetable(currentTimetable);
            updateWorkloadSummary();
        }
    }
    
    /**
     * Updates the workload summary display.
     * Shows the number of periods assigned to the faculty on each day.
     */
    private void updateWorkloadSummary() {
        if (adapter != null) {
            String summary = adapter.getWorkloadSummary();
            workloadSummaryText.setText(summary);
        }
    }
}
