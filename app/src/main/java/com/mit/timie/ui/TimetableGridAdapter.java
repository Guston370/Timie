package com.mit.timie.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Assignment;
import com.mit.timie.model.Config;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Room;
import com.mit.timie.model.Subject;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.TimetableRepository;

/**
 * Adapter for displaying timetable grid in RecyclerView.
 * Calculates day and period from position and displays assignment details.
 */
public class TimetableGridAdapter extends RecyclerView.Adapter<TimetableGridAdapter.CellViewHolder> {
    
    private Timetable timetable;
    private Config config;
    private String classSectionId;
    private TimetableRepository repository;
    private OnCellClickListener cellClickListener;
    
    /**
     * Interface for handling cell click events.
     */
    public interface OnCellClickListener {
        void onCellClick(Assignment assignment, TimeSlot timeSlot, int position);
    }
    
    /**
     * Constructor for TimetableGridAdapter.
     * 
     * @param config The configuration containing working days and periods
     * @param repository The repository for accessing entity data
     */
    public TimetableGridAdapter(Config config, TimetableRepository repository) {
        this.config = config;
        this.repository = repository;
    }
    
    /**
     * Set the timetable to display.
     * 
     * @param timetable The timetable to display
     */
    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
        notifyDataSetChanged();
    }
    
    /**
     * Set the class section to display.
     * 
     * @param classSectionId The class section ID
     */
    public void setClassSectionId(String classSectionId) {
        this.classSectionId = classSectionId;
        notifyDataSetChanged();
    }
    
    /**
     * Set the cell click listener.
     * 
     * @param listener The listener for cell clicks
     */
    public void setCellClickListener(OnCellClickListener listener) {
        this.cellClickListener = listener;
    }
    
    @NonNull
    @Override
    public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timetable_cell, parent, false);
        return new CellViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
        if (config == null) {
            return;
        }
        
        // Calculate day and period from position
        int day = position / config.getPeriodsPerDay();
        int period = position % config.getPeriodsPerDay();
        
        TimeSlot slot = new TimeSlot(day, period);
        
        // Check if this is a break period
        if (config.getBreakPeriods() != null && config.getBreakPeriods().contains(period)) {
            displayBreakPeriod(holder);
            holder.itemView.setEnabled(false);
            holder.itemView.setOnClickListener(null);
            return;
        }
        
        // Get assignment for this slot
        Assignment assignment = null;
        if (timetable != null && classSectionId != null) {
            assignment = timetable.getAssignment(classSectionId, slot);
        }
        
        if (assignment != null) {
            displayAssignment(holder, assignment);
            
            // Set click listener
            final Assignment finalAssignment = assignment;
            final TimeSlot finalSlot = slot;
            final int finalPosition = position;
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                if (cellClickListener != null) {
                    cellClickListener.onCellClick(finalAssignment, finalSlot, finalPosition);
                }
            });
        } else {
            displayFreePeriod(holder);
            
            // Set click listener for free period
            final TimeSlot finalSlot = slot;
            final int finalPosition = position;
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                if (cellClickListener != null) {
                    cellClickListener.onCellClick(null, finalSlot, finalPosition);
                }
            });
        }
    }
    
    @Override
    public int getItemCount() {
        if (config == null) {
            return 0;
        }
        return config.getWorkingDays() * config.getPeriodsPerDay();
    }
    
    /**
     * Display assignment details in the cell.
     * 
     * @param holder The view holder
     * @param assignment The assignment to display
     */
    private void displayAssignment(CellViewHolder holder, Assignment assignment) {
        // Show assignment views
        holder.subjectNameText.setVisibility(View.VISIBLE);
        holder.facultyNameText.setVisibility(View.VISIBLE);
        holder.roomNameText.setVisibility(View.VISIBLE);
        holder.breakIndicator.setVisibility(View.GONE);
        
        // Get entity names from repository
        String subjectName = getSubjectName(assignment.getSubjectId());
        String facultyName = getFacultyName(assignment.getFacultyId());
        String roomName = getRoomName(assignment.getRoomId());
        
        // Set text
        holder.subjectNameText.setText(subjectName);
        holder.facultyNameText.setText(facultyName);
        holder.roomNameText.setText(roomName);
        
        // Apply locked cell styling
        if (assignment.isLocked()) {
            holder.lockedIndicator.setVisibility(View.VISIBLE);
            holder.cellContainer.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.lockedIndicator.setVisibility(View.GONE);
            holder.cellContainer.setBackgroundColor(Color.WHITE);
        }
    }
    
    /**
     * Display break period in the cell.
     * 
     * @param holder The view holder
     */
    private void displayBreakPeriod(CellViewHolder holder) {
        // Hide assignment views
        holder.subjectNameText.setVisibility(View.GONE);
        holder.facultyNameText.setVisibility(View.GONE);
        holder.roomNameText.setVisibility(View.GONE);
        holder.lockedIndicator.setVisibility(View.GONE);
        
        // Show break indicator
        holder.breakIndicator.setVisibility(View.VISIBLE);
        holder.breakIndicator.setText("BREAK");
        
        // Set background color
        holder.cellContainer.setBackgroundColor(Color.parseColor("#FFF3E0")); // Light orange
    }
    
    /**
     * Display free period in the cell.
     * 
     * @param holder The view holder
     */
    private void displayFreePeriod(CellViewHolder holder) {
        // Show subject text only
        holder.subjectNameText.setVisibility(View.VISIBLE);
        holder.facultyNameText.setVisibility(View.GONE);
        holder.roomNameText.setVisibility(View.GONE);
        holder.lockedIndicator.setVisibility(View.GONE);
        holder.breakIndicator.setVisibility(View.GONE);
        
        // Set text
        holder.subjectNameText.setText("Free Period");
        holder.subjectNameText.setTextColor(Color.GRAY);
        
        // Set background color
        holder.cellContainer.setBackgroundColor(Color.WHITE);
    }
    
    /**
     * Get subject name from repository.
     * 
     * @param subjectId The subject ID
     * @return The subject name, or "Unknown Subject" if not found
     */
    private String getSubjectName(String subjectId) {
        if (repository == null || subjectId == null) {
            return "Unknown Subject";
        }
        Subject subject = repository.getSubject(subjectId);
        return subject != null ? subject.getName() : "Unknown Subject";
    }
    
    /**
     * Get faculty name from repository.
     * 
     * @param facultyId The faculty ID
     * @return The faculty name, or "Unknown Faculty" if not found
     */
    private String getFacultyName(String facultyId) {
        if (repository == null || facultyId == null) {
            return "Unknown Faculty";
        }
        Faculty faculty = repository.getFaculty(facultyId);
        return faculty != null ? faculty.getName() : "Unknown Faculty";
    }
    
    /**
     * Get room name from repository.
     * 
     * @param roomId The room ID
     * @return The room name, or "Unknown Room" if not found
     */
    private String getRoomName(String roomId) {
        if (repository == null || roomId == null) {
            return "Unknown Room";
        }
        Room room = repository.getRoom(roomId);
        return room != null ? room.getName() : "Unknown Room";
    }
    
    /**
     * ViewHolder for timetable cells.
     */
    static class CellViewHolder extends RecyclerView.ViewHolder {
        LinearLayout cellContainer;
        TextView subjectNameText;
        TextView facultyNameText;
        TextView roomNameText;
        ImageView lockedIndicator;
        TextView breakIndicator;
        
        CellViewHolder(@NonNull View itemView) {
            super(itemView);
            cellContainer = itemView.findViewById(R.id.cellContainer);
            subjectNameText = itemView.findViewById(R.id.subjectNameText);
            facultyNameText = itemView.findViewById(R.id.facultyNameText);
            roomNameText = itemView.findViewById(R.id.roomNameText);
            lockedIndicator = itemView.findViewById(R.id.lockedIndicator);
            breakIndicator = itemView.findViewById(R.id.breakIndicator);
        }
    }
}
