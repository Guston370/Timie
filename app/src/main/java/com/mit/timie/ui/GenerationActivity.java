package com.mit.timie.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.ConflictReport;
import com.mit.timie.model.Timetable;
import com.mit.timie.viewmodel.GenerationViewModel;

/**
 * Activity for generating timetable variants.
 * Displays loading indicator during generation and shows generated variants.
 * Handles generation errors by displaying conflict details.
 */
public class GenerationActivity extends AppCompatActivity implements VariantsAdapter.OnVariantClickListener {
    
    private GenerationViewModel viewModel;
    private Button generateButton;
    private ProgressBar loadingIndicator;
    private TextView loadingText;
    private TextView conflictMessageText;
    private TextView variantsLabel;
    private RecyclerView variantsRecyclerView;
    private VariantsAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generation);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(GenerationViewModel.class);
        
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
        generateButton = findViewById(R.id.generateButton);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingText = findViewById(R.id.loadingText);
        conflictMessageText = findViewById(R.id.conflictMessageText);
        variantsLabel = findViewById(R.id.variantsLabel);
        variantsRecyclerView = findViewById(R.id.variantsRecyclerView);
    }
    
    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        adapter = new VariantsAdapter(this);
        variantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        variantsRecyclerView.setAdapter(adapter);
    }
    
    /**
     * Observes LiveData from ViewModel.
     */
    private void observeViewModel() {
        // Observe generation loading state
        viewModel.getIsGenerating().observe(this, isGenerating -> {
            if (isGenerating) {
                // Show loading indicator
                loadingIndicator.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                generateButton.setEnabled(false);
                
                // Hide other views
                conflictMessageText.setVisibility(View.GONE);
                variantsLabel.setVisibility(View.GONE);
                variantsRecyclerView.setVisibility(View.GONE);
            } else {
                // Hide loading indicator
                loadingIndicator.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                generateButton.setEnabled(true);
            }
        });
        
        // Observe generated timetables
        viewModel.getTimetables().observe(this, timetables -> {
            if (timetables != null && !timetables.isEmpty()) {
                // Show variants
                adapter.setVariants(timetables);
                variantsLabel.setVisibility(View.VISIBLE);
                variantsRecyclerView.setVisibility(View.VISIBLE);
                conflictMessageText.setVisibility(View.GONE);
            }
        });
        
        // Observe conflicts
        viewModel.getConflicts().observe(this, conflictReport -> {
            if (conflictReport != null) {
                // Show conflict message
                showConflictDialog(conflictReport);
                
                // Display brief conflict message in the activity
                if (conflictReport.getConflicts() != null && !conflictReport.getConflicts().isEmpty()) {
                    conflictMessageText.setText(conflictReport.getConflicts().get(0));
                    conflictMessageText.setVisibility(View.VISIBLE);
                }
                
                // Hide variants
                variantsLabel.setVisibility(View.GONE);
                variantsRecyclerView.setVisibility(View.GONE);
            }
        });
    }
    
    /**
     * Sets up click listeners for buttons.
     */
    private void setupListeners() {
        generateButton.setOnClickListener(v -> {
            // Clear previous results
            viewModel.clearResults();
            
            // Start generation
            viewModel.generateTimetables();
        });
    }
    
    /**
     * Shows a dialog displaying conflict details.
     * 
     * @param conflictReport The conflict report to display
     */
    private void showConflictDialog(ConflictReport conflictReport) {
        // Inflate dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_conflict, null);
        
        // Get dialog views
        TextView conflictDetailsText = dialogView.findViewById(R.id.conflictDetailsText);
        TextView suggestionLabel = dialogView.findViewById(R.id.suggestionLabel);
        TextView suggestionText = dialogView.findViewById(R.id.suggestionText);
        Button okButton = dialogView.findViewById(R.id.okButton);
        
        // Build conflict details text
        StringBuilder conflictDetails = new StringBuilder();
        if (conflictReport.getConflicts() != null) {
            for (String conflict : conflictReport.getConflicts()) {
                conflictDetails.append("• ").append(conflict).append("\n");
            }
        }
        conflictDetailsText.setText(conflictDetails.toString());
        
        // Show suggestion if available
        if (conflictReport.getSuggestion() != null && !conflictReport.getSuggestion().isEmpty()) {
            suggestionLabel.setVisibility(View.VISIBLE);
            suggestionText.setVisibility(View.VISIBLE);
            suggestionText.setText(conflictReport.getSuggestion());
        }
        
        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        
        // Set up OK button
        okButton.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * Handles variant click events from the adapter.
     * Navigates to ClassTimetableActivity to view the selected variant.
     * 
     * @param timetable The selected timetable variant
     */
    @Override
    public void onVariantClick(Timetable timetable) {
        // Navigate to ClassTimetableActivity
        Intent intent = new Intent(this, ClassTimetableActivity.class);
        startActivity(intent);
    }
}
