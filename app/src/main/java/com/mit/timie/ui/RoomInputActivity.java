package com.mit.timie.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import com.mit.timie.model.Config;
import com.mit.timie.model.Room;
import com.mit.timie.repository.ConfigRepository;
import com.mit.timie.viewmodel.RoomViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Activity for room input.
 * Allows users to add, edit, and delete rooms with availability configuration.
 */
public class RoomInputActivity extends AppCompatActivity implements RoomAdapter.OnRoomActionListener {
    
    private RoomViewModel viewModel;
    private RoomAdapter adapter;
    private RecyclerView recyclerView;
    private Button nextButton;
    private FloatingActionButton addRoomFab;
    private Config config;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_input);
        
        // Load configuration
        config = ConfigRepository.getInstance().getConfig();
        
        if (config == null) {
            Toast.makeText(this, "Configuration not found. Please complete setup first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        
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
        recyclerView = findViewById(R.id.roomsRecyclerView);
        nextButton = findViewById(R.id.nextButton);
        addRoomFab = findViewById(R.id.addRoomFab);
    }
    
    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        adapter = new RoomAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    /**
     * Observes LiveData from ViewModel.
     */
    private void observeViewModel() {
        // Observe room list
        viewModel.getRooms().observe(this, rooms -> {
            adapter.setRooms(rooms);
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
        addRoomFab.setOnClickListener(v -> showRoomDialog(null));
        
        nextButton.setOnClickListener(v -> navigateToClassInput());
    }
    
    /**
     * Shows dialog for adding or editing a room.
     * 
     * @param room The room to edit, or null to add a new room
     */
    private void showRoomDialog(Room room) {
        boolean isEdit = room != null;
        
        // Inflate dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_room_input, null);
        
        // Get dialog views
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextInputLayout roomNameLayout = dialogView.findViewById(R.id.roomNameLayout);
        TextInputEditText roomNameInput = dialogView.findViewById(R.id.roomNameInput);
        Spinner roomTypeSpinner = dialogView.findViewById(R.id.roomTypeSpinner);
        LinearLayout availabilityGridContainer = dialogView.findViewById(R.id.availabilityGridContainer);
        TextView errorText = dialogView.findViewById(R.id.errorText);
        
        // Set dialog title
        dialogTitle.setText(isEdit ? R.string.edit_room_title : R.string.add_room_title);
        
        // Set up room type spinner
        setupRoomTypeSpinner(roomTypeSpinner);
        
        // Create availability grid
        Map<Integer, List<Integer>> availability = isEdit && room.getAvailability() != null ? 
                new HashMap<>(room.getAvailability()) : createDefaultAvailability();
        
        createAvailabilityGrid(availabilityGridContainer, availability);
        
        // Populate fields if editing
        if (isEdit) {
            roomNameInput.setText(room.getName());
            roomTypeSpinner.setSelection(room.getType() == Room.RoomType.LAB ? 1 : 0);
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
            roomNameLayout.setError(null);
            errorText.setVisibility(View.GONE);
            
            // Get input values
            String name = roomNameInput.getText() != null ? roomNameInput.getText().toString().trim() : "";
            
            // Validate inputs
            boolean valid = true;
            
            if (name.isEmpty()) {
                roomNameLayout.setError("Room name is required");
                valid = false;
            }
            
            if (!valid) {
                return;
            }
            
            // Create or update room
            Room roomToSave = isEdit ? room : new Room();
            if (!isEdit) {
                roomToSave.setId(UUID.randomUUID().toString());
            }
            
            roomToSave.setName(name);
            roomToSave.setType(roomTypeSpinner.getSelectedItemPosition() == 0 ? 
                    Room.RoomType.CLASSROOM : Room.RoomType.LAB);
            roomToSave.setAvailability(availability);
            
            // Save to ViewModel
            boolean success = isEdit ? viewModel.editRoom(roomToSave) : viewModel.addRoom(roomToSave);
            
            if (success) {
                dialog.dismiss();
                Toast.makeText(this, isEdit ? "Room updated" : "Room added", Toast.LENGTH_SHORT).show();
            } else {
                errorText.setText(viewModel.getValidationError().getValue());
                errorText.setVisibility(View.VISIBLE);
            }
        });
        
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * Sets up the room type spinner with options.
     * 
     * @param spinner The spinner to set up
     */
    private void setupRoomTypeSpinner(Spinner spinner) {
        String[] types = {"Classroom", "Lab"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    
    /**
     * Creates default availability (all periods available on all days).
     * 
     * @return Default availability map
     */
    private Map<Integer, List<Integer>> createDefaultAvailability() {
        Map<Integer, List<Integer>> availability = new HashMap<>();
        
        for (int day = 0; day < config.getWorkingDays(); day++) {
            List<Integer> periods = new ArrayList<>();
            for (int period = 0; period < config.getPeriodsPerDay(); period++) {
                periods.add(period);
            }
            availability.put(day, periods);
        }
        
        return availability;
    }
    
    /**
     * Creates the availability grid with checkboxes for each day and period.
     * 
     * @param container The container to add the grid to
     * @param availability The availability map to populate
     */
    private void createAvailabilityGrid(LinearLayout container, Map<Integer, List<Integer>> availability) {
        container.removeAllViews();
        
        // Create header row with period numbers
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        
        // Empty cell for day column
        TextView emptyCell = new TextView(this);
        emptyCell.setLayoutParams(new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT));
        emptyCell.setPadding(8, 8, 8, 8);
        headerRow.addView(emptyCell);
        
        // Period headers
        for (int period = 0; period < config.getPeriodsPerDay(); period++) {
            TextView periodHeader = new TextView(this);
            periodHeader.setLayoutParams(new LinearLayout.LayoutParams(60, LinearLayout.LayoutParams.WRAP_CONTENT));
            periodHeader.setText("P" + (period + 1));
            periodHeader.setGravity(Gravity.CENTER);
            periodHeader.setPadding(4, 8, 4, 8);
            periodHeader.setTextSize(12);
            periodHeader.setTextColor(Color.BLACK);
            headerRow.addView(periodHeader);
        }
        
        container.addView(headerRow);
        
        // Create row for each day
        for (int day = 0; day < config.getWorkingDays(); day++) {
            LinearLayout dayRow = new LinearLayout(this);
            dayRow.setOrientation(LinearLayout.HORIZONTAL);
            dayRow.setPadding(0, 4, 0, 4);
            
            // Day name
            TextView dayLabel = new TextView(this);
            dayLabel.setLayoutParams(new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT));
            dayLabel.setText(getDayName(day));
            dayLabel.setPadding(8, 8, 8, 8);
            dayLabel.setTextSize(14);
            dayLabel.setTextColor(Color.BLACK);
            dayRow.addView(dayLabel);
            
            // Checkboxes for each period
            final int currentDay = day;
            List<Integer> dayAvailability = availability.get(day);
            if (dayAvailability == null) {
                dayAvailability = new ArrayList<>();
                availability.put(day, dayAvailability);
            }
            
            for (int period = 0; period < config.getPeriodsPerDay(); period++) {
                final int currentPeriod = period;
                
                CheckBox periodCheckbox = new CheckBox(this);
                periodCheckbox.setLayoutParams(new LinearLayout.LayoutParams(60, LinearLayout.LayoutParams.WRAP_CONTENT));
                periodCheckbox.setPadding(4, 4, 4, 4);
                periodCheckbox.setChecked(dayAvailability.contains(period));
                
                final List<Integer> finalDayAvailability = dayAvailability;
                periodCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (!finalDayAvailability.contains(currentPeriod)) {
                            finalDayAvailability.add(currentPeriod);
                        }
                    } else {
                        finalDayAvailability.remove(Integer.valueOf(currentPeriod));
                    }
                });
                
                dayRow.addView(periodCheckbox);
            }
            
            container.addView(dayRow);
        }
    }
    
    /**
     * Gets the day name from the configuration.
     * 
     * @param dayIndex The day index
     * @return The day name
     */
    private String getDayName(int dayIndex) {
        if (config.getDayNames() != null && dayIndex < config.getDayNames().size()) {
            return config.getDayNames().get(dayIndex);
        }
        return "Day " + (dayIndex + 1);
    }
    
    /**
     * Navigates to the Class Input Activity.
     */
    private void navigateToClassInput() {
        Intent intent = new Intent(this, ClassInputActivity.class);
        startActivity(intent);
    }
    
    @Override
    public void onEditRoom(Room room) {
        showRoomDialog(room);
    }
    
    @Override
    public void onDeleteRoom(Room room) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Room")
                .setMessage("Are you sure you want to delete " + room.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteRoom(room.getId());
                    Toast.makeText(this, "Room deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
