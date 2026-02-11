package com.mit.timie.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying subjects in a list.
 * Displays subject name, type, weekly periods, and priority.
 * Provides edit and delete buttons for each item.
 */
public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    
    private List<Subject> subjects;
    private OnSubjectActionListener listener;
    
    /**
     * Interface for handling subject actions (edit, delete).
     */
    public interface OnSubjectActionListener {
        void onEditSubject(Subject subject);
        void onDeleteSubject(Subject subject);
    }
    
    public SubjectAdapter(OnSubjectActionListener listener) {
        this.subjects = new ArrayList<>();
        this.listener = listener;
    }
    
    /**
     * Updates the subject list and refreshes the view.
     * 
     * @param subjects The new list of subjects
     */
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects != null ? subjects : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.bind(subject);
    }
    
    @Override
    public int getItemCount() {
        return subjects.size();
    }
    
    /**
     * ViewHolder for subject items.
     */
    class SubjectViewHolder extends RecyclerView.ViewHolder {
        
        private TextView subjectNameText;
        private TextView subjectDetailsText;
        private ImageButton editButton;
        private ImageButton deleteButton;
        
        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            
            subjectNameText = itemView.findViewById(R.id.subjectNameText);
            subjectDetailsText = itemView.findViewById(R.id.subjectDetailsText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
        
        /**
         * Binds subject data to the view.
         * 
         * @param subject The subject to display
         */
        public void bind(Subject subject) {
            subjectNameText.setText(subject.getName());
            
            // Format details: Type | Periods | Priority
            String details = String.format("%s | %d periods/week | %s priority",
                    formatSubjectType(subject.getType()),
                    subject.getWeeklyPeriods(),
                    formatPriority(subject.getPriority()));
            
            subjectDetailsText.setText(details);
            
            // Set click listeners
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditSubject(subject);
                }
            });
            
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteSubject(subject);
                }
            });
        }
        
        /**
         * Formats subject type for display.
         * 
         * @param type The subject type
         * @return Formatted type string
         */
        private String formatSubjectType(Subject.SubjectType type) {
            if (type == null) return "Unknown";
            
            switch (type) {
                case THEORY:
                    return "Theory";
                case LAB:
                    return "Lab";
                default:
                    return type.toString();
            }
        }
        
        /**
         * Formats priority for display.
         * 
         * @param priority The priority level
         * @return Formatted priority string
         */
        private String formatPriority(Subject.Priority priority) {
            if (priority == null) return "Unknown";
            
            switch (priority) {
                case HIGH:
                    return "High";
                case MEDIUM:
                    return "Medium";
                case LOW:
                    return "Low";
                default:
                    return priority.toString();
            }
        }
    }
}
