package com.mit.timie.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Assignment;
import com.mit.timie.model.ClassSection;
import com.mit.timie.model.Config;
import com.mit.timie.model.Room;
import com.mit.timie.model.Subject;
import com.mit.timie.model.TimeSlot;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.TimetableRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for displaying faculty schedule in RecyclerView.
 * Shows class, section, subject, and room for each assigned period.
 * Displays "Free" for unassigned periods.
 */
public class FacultyTimetableAdapter extends RecyclerView.Adapter<FacultyTimetableAdapter.FacultyCellViewHolder> {
    
    private Timetable timetable;
    private Config config;
    private String facultyId;
    private TimetableRepository repository;
    private Map<Integer, Integer> dailyWorkload;
    
    /**
     * Constructor for FacultyTimetableAdapter.
     * 
     * @param config The configuration containing working days and periods
     * @param repository The repository for accessing entity data
     */
    public FacultyTimetableAdapter(Config config, TimetableRepository repository) {
        this.config = config;
        this.repository = repository;
        this.dailyWorkload = new HashMap<>();
    }
    
    /**
     * Set the timetable to display.
     * 
     * @param timetable The timetable to display
     */
    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
        calculateDailyWorkload();
        notifyDataSetChanged();
    }
    
    /**
     * Set the faculty member to display.
     * 
     * @param facultyId The faculty ID
     */
    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
        calculateDailyWorkload();
        notifyDataSetChanged();
    }
    
    /**
     * Calculate daily workload for the faculty member.
     * Counts the number of periods assigned to the faculty on each day.
     */
    private void calculateDailyWorkload() {
        dailyWorkload.clear();
        
        if (timetable == null || facultyId == null || config == null) {
            return;
        }
        
        // Initialize workload for each day
        for (int day = 0; day < config.getWorkingDays(); day++) {
            dailyWorkload.put(day, 0);
        }
        
        // Count assignments for each day
        Map<TimeSlot, Assignment> facultySchedule = timetable.getFacultyTimetables().get(facultyId);
        if (facultySchedule != null) {
            for (TimeSlot slot : facultySchedule.keySet()) {
                int day = slot.getDay();
                dailyWorkload.put(day, dailyWorkload.get(day) + 1);
            }
        }
    }
    
    /**
     * Get the daily workload for a specific day.
     * 
     * @param day The day index
     * @return The number of periods assigned on that day
     */
    public int getDailyWorkload(int day) {
        return dailyWorkload.getOrDefault(day, 0);
    }
    
    /**
     * Get a formatted workload summary string.
     * 
     * @return A string showing workload for each day
     */
    public String getWorkloadSummary() {
        if (config == null || dailyWorkload.isEmpty()) {
            return "No workload data available";
        }
        
        StringBuilder summary = new StringBuilder();
        for (int day = 0; day < config.getWorkingDays(); day++) {
            String dayName = config.getDayNames().get(day);
            int workload = dailyWorkload.get(day);
            summary.append(dayName).append(": ").append(workload).append(" periods");
            if (day < config.getWorkingDays() - 1) {
                summary.append("\n");
            }
        }
        
        return summary.toString();
    }
    
    @NonNull
    @Override
    public FacultyCellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faculty_timetable_cell, parent, false);
        return new FacultyCellViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FacultyCellViewHolder holder, int position) {
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
            return;
        }
        
        // Get assignment for this slot
        Assignment assignment = null;
        if (timetable != null && facultyId != null) {
            Map<TimeSlot, Assignment> facultySchedule = timetable.getFacultyTimetables().get(facultyId);
            if (facultySchedule != null) {
                assignment = facultySchedule.get(slot);
            }
        }
        
        if (assignment != null) {
            displayAssignment(holder, assignment);
        } else {
            displayFreePeriod(holder);
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
     * Shows class, section, subject, and room.
     * 
     * @param holder The view holder
     * @param assignment The assignment to display
     */
    private void displayAssignment(FacultyCellViewHolder holder, Assignment assignment) {
        // Show all text views
        holder.classText.setVisibility(View.VISIBLE);
        holder.subjectText.setVisibility(View.VISIBLE);
        holder.roomText.setVisibility(View.VISIBLE);
        
        // Get entity names from repository
        String className = getClassName(assignment.getClassSectionId());
        String subjectName = getSubjectName(assignment.getSubjectId());
        String roomName = getRoomName(assignment.getRoomId());
        
        // Set text
        holder.classText.setText(className);
        holder.subjectText.setText(subjectName);
        holder.roomText.setText("Room: " + roomName);
        
        // Set normal text color
        holder.classText.setTextColor(Color.BLACK);
        holder.subjectText.setTextColor(Color.BLACK);
        holder.roomText.setTextColor(Color.DKGRAY);
        
        // Set background color
        holder.cellContainer.setBackgroundColor(Color.WHITE);
    }
    
    /**
     * Display break period in the cell.
     * 
     * @param holder The view holder
     */
    private void displayBreakPeriod(FacultyCellViewHolder holder) {
        // Show only class text
        holder.classText.setVisibility(View.VISIBLE);
        holder.subjectText.setVisibility(View.GONE);
        holder.roomText.setVisibility(View.GONE);
        
        // Set text
        holder.classText.setText("BREAK");
        holder.classText.setTextColor(Color.parseColor("#FF6F00")); // Dark orange
        
        // Set background color
        holder.cellContainer.setBackgroundColor(Color.parseColor("#FFF3E0")); // Light orange
    }
    
    /**
     * Display free period in the cell.
     * 
     * @param holder The view holder
     */
    private void displayFreePeriod(FacultyCellViewHolder holder) {
        // Show only class text
        holder.classText.setVisibility(View.VISIBLE);
        holder.subjectText.setVisibility(View.GONE);
        holder.roomText.setVisibility(View.GONE);
        
        // Set text
        holder.classText.setText("Free");
        holder.classText.setTextColor(Color.GRAY);
        
        // Set background color
        holder.cellContainer.setBackgroundColor(Color.parseColor("#F5F5F5")); // Light gray
    }
    
    /**
     * Get class name from repository.
     * 
     * @param classSectionId The class section ID
     * @return The class name with section, or "Unknown Class" if not found
     */
    private String getClassName(String classSectionId) {
        if (repository == null || classSectionId == null) {
            return "Unknown Class";
        }
        ClassSection classSection = repository.getClassSection(classSectionId);
        if (classSection != null) {
            return classSection.getClassName() + " - " + classSection.getSectionName();
        }
        return "Unknown Class";
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
     * ViewHolder for faculty timetable cells.
     */
    static class FacultyCellViewHolder extends RecyclerView.ViewHolder {
        LinearLayout cellContainer;
        TextView classText;
        TextView subjectText;
        TextView roomText;
        
        FacultyCellViewHolder(@NonNull View itemView) {
            super(itemView);
            cellContainer = itemView.findViewById(R.id.facultyCellContainer);
            classText = itemView.findViewById(R.id.classText);
            subjectText = itemView.findViewById(R.id.subjectText);
            roomText = itemView.findViewById(R.id.roomText);
        }
    }
}
