package com.mit.timie.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Assignment;
import com.mit.timie.model.ClassSection;
import com.mit.timie.model.Config;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Subject;
import com.mit.timie.model.Timetable;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying subject distribution in a RecyclerView.
 * Shows subject name, required periods, assigned periods, and all assigned slots.
 * Highlights discrepancies when assigned periods don't match required periods.
 */
public class SubjectDistributionAdapter extends RecyclerView.Adapter<SubjectDistributionAdapter.SubjectDistributionViewHolder> {
    
    private List<SubjectDistribution> distributions;
    private TimetableRepository repository;
    private Config config;
    
    public SubjectDistributionAdapter(TimetableRepository repository, Config config) {
        this.distributions = new ArrayList<>();
        this.repository = repository;
        this.config = config;
    }
    
    /**
     * Sets the timetable and calculates subject distribution.
     * 
     * @param timetable The timetable to analyze
     */
    public void setTimetable(Timetable timetable) {
        distributions.clear();
        
        if (timetable == null) {
            notifyDataSetChanged();
            return;
        }
        
        // Get all subjects
        List<Subject> subjects = repository.getAllSubjects();
        
        // Calculate distribution for each subject
        for (Subject subject : subjects) {
            SubjectDistribution distribution = new SubjectDistribution();
            distribution.subject = subject;
            distribution.requiredPeriods = subject.getWeeklyPeriods();
            distribution.assignedSlots = new ArrayList<>();
            
            // Find all assignments for this subject
            for (Assignment assignment : timetable.getAllAssignments()) {
                if (assignment.getSubjectId().equals(subject.getId())) {
                    distribution.assignedSlots.add(assignment);
                }
            }
            
            distribution.assignedPeriods = distribution.assignedSlots.size();
            
            distributions.add(distribution);
        }
        
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public SubjectDistributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject_distribution, parent, false);
        return new SubjectDistributionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SubjectDistributionViewHolder holder, int position) {
        SubjectDistribution distribution = distributions.get(position);
        holder.bind(distribution);
    }
    
    @Override
    public int getItemCount() {
        return distributions.size();
    }
    
    /**
     * ViewHolder for subject distribution items.
     */
    class SubjectDistributionViewHolder extends RecyclerView.ViewHolder {
        
        private TextView subjectNameText;
        private TextView requiredPeriodsText;
        private TextView assignedPeriodsText;
        private TextView discrepancyIndicator;
        private RecyclerView assignedSlotsRecyclerView;
        
        public SubjectDistributionViewHolder(@NonNull View itemView) {
            super(itemView);
            
            subjectNameText = itemView.findViewById(R.id.subjectNameText);
            requiredPeriodsText = itemView.findViewById(R.id.requiredPeriodsText);
            assignedPeriodsText = itemView.findViewById(R.id.assignedPeriodsText);
            discrepancyIndicator = itemView.findViewById(R.id.discrepancyIndicator);
            assignedSlotsRecyclerView = itemView.findViewById(R.id.assignedSlotsRecyclerView);
            
            // Set up nested RecyclerView
            assignedSlotsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
        
        public void bind(SubjectDistribution distribution) {
            // Set subject name
            subjectNameText.setText(distribution.subject.getName());
            
            // Set period counts
            requiredPeriodsText.setText("Required: " + distribution.requiredPeriods);
            assignedPeriodsText.setText("Assigned: " + distribution.assignedPeriods);
            
            // Show discrepancy indicator if counts don't match
            if (distribution.requiredPeriods != distribution.assignedPeriods) {
                discrepancyIndicator.setVisibility(View.VISIBLE);
                discrepancyIndicator.setText("⚠ Discrepancy: " + 
                    (distribution.assignedPeriods - distribution.requiredPeriods > 0 ? "+" : "") +
                    (distribution.assignedPeriods - distribution.requiredPeriods) + " periods");
            } else {
                discrepancyIndicator.setVisibility(View.GONE);
            }
            
            // Set up assigned slots adapter
            AssignedSlotAdapter slotAdapter = new AssignedSlotAdapter(
                distribution.assignedSlots, 
                repository, 
                config
            );
            assignedSlotsRecyclerView.setAdapter(slotAdapter);
        }
    }
    
    /**
     * Data class to hold subject distribution information.
     */
    private static class SubjectDistribution {
        Subject subject;
        int requiredPeriods;
        int assignedPeriods;
        List<Assignment> assignedSlots;
    }
    
    /**
     * Adapter for displaying assigned slots within each subject distribution item.
     */
    private static class AssignedSlotAdapter extends RecyclerView.Adapter<AssignedSlotAdapter.SlotViewHolder> {
        
        private List<Assignment> assignments;
        private TimetableRepository repository;
        private Config config;
        
        public AssignedSlotAdapter(List<Assignment> assignments, TimetableRepository repository, Config config) {
            this.assignments = assignments;
            this.repository = repository;
            this.config = config;
        }
        
        @NonNull
        @Override
        public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_assigned_slot, parent, false);
            return new SlotViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
            Assignment assignment = assignments.get(position);
            holder.bind(assignment, repository, config);
        }
        
        @Override
        public int getItemCount() {
            return assignments.size();
        }
        
        /**
         * ViewHolder for assigned slot items.
         */
        static class SlotViewHolder extends RecyclerView.ViewHolder {
            
            private TextView slotTimeText;
            private TextView slotClassText;
            private TextView slotFacultyText;
            
            public SlotViewHolder(@NonNull View itemView) {
                super(itemView);
                
                slotTimeText = itemView.findViewById(R.id.slotTimeText);
                slotClassText = itemView.findViewById(R.id.slotClassText);
                slotFacultyText = itemView.findViewById(R.id.slotFacultyText);
            }
            
            public void bind(Assignment assignment, TimetableRepository repository, Config config) {
                // Format day and period
                int day = assignment.getTimeSlot().getDay();
                int period = assignment.getTimeSlot().getPeriod();
                
                String dayName = (day < config.getDayNames().size()) ? 
                    config.getDayNames().get(day) : "Day " + (day + 1);
                
                slotTimeText.setText(dayName + ", P" + (period + 1));
                
                // Get class section name
                ClassSection classSection = repository.getClassSection(assignment.getClassSectionId());
                if (classSection != null) {
                    slotClassText.setText(classSection.getClassName() + " " + classSection.getSectionName());
                } else {
                    slotClassText.setText("Unknown Class");
                }
                
                // Get faculty name
                Faculty faculty = repository.getFaculty(assignment.getFacultyId());
                if (faculty != null) {
                    slotFacultyText.setText(faculty.getName());
                } else {
                    slotFacultyText.setText("Unknown Faculty");
                }
            }
        }
    }
}
