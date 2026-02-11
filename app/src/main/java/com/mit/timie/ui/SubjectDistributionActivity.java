package com.mit.timie.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Config;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.repository.TimetableRepository;
import com.mit.timie.viewmodel.TimetableViewModel;

import java.util.List;

/**
 * Activity for viewing subject distribution across the timetable.
 * Displays all subjects with their required periods, assigned periods,
 * and detailed slot information (day, period, class, faculty).
 * Highlights discrepancies when assigned periods don't match required periods.
 */
public class SubjectDistributionActivity extends AppCompatActivity {
    
    private TimetableViewModel viewModel;
    private TimetableRepository repository;
    private ConfigRepository configRepository;
    
    private RecyclerView subjectDistributionRecyclerView;
    private Button backButton;
    
    private SubjectDistributionAdapter adapter;
    private Config config;
    private Timetable currentTimetable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_distribution);
        
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
        
        // Observe ViewModel
        observeViewModel();
        
        // Set up listeners
        setupListeners();
    }
    
    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        subjectDistributionRecyclerView = findViewById(R.id.subjectDistributionRecyclerView);
        backButton = findViewById(R.id.backButton);
    }
    
    /**
     * Loads data from repositories.
     */
    private void loadData() {
        // Check if subjects exist
        if (repository.getAllSubjects().isEmpty()) {
            Toast.makeText(this, "No subjects found. Please add subjects first.", Toast.LENGTH_LONG).show();
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
     * Sets up the RecyclerView with LinearLayoutManager and adapter.
     */
    private void setupRecyclerView() {
        // Create adapter
        adapter = new SubjectDistributionAdapter(repository, config);
        
        // Set up LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        subjectDistributionRecyclerView.setLayoutManager(layoutManager);
        subjectDistributionRecyclerView.setAdapter(adapter);
        
        // Set initial timetable
        adapter.setTimetable(currentTimetable);
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
}
