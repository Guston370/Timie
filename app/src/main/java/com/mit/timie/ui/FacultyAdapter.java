package com.mit.timie.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Faculty;
import com.mit.timie.model.Subject;
import com.mit.timie.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying faculty members in a list.
 * Displays faculty name, subjects, and max periods.
 * Provides edit and delete buttons for each item.
 */
public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder> {
    
    private List<Faculty> faculties;
    private OnFacultyActionListener listener;
    private TimetableRepository repository;
    
    /**
     * Interface for handling faculty actions (edit, delete).
     */
    public interface OnFacultyActionListener {
        void onEditFaculty(Faculty faculty);
        void onDeleteFaculty(Faculty faculty);
    }
    
    public FacultyAdapter(OnFacultyActionListener listener) {
        this.faculties = new ArrayList<>();
        this.listener = listener;
        this.repository = TimetableRepository.getInstance();
    }
    
    /**
     * Updates the faculty list and refreshes the view.
     * 
     * @param faculties The new list of faculties
     */
    public void setFaculties(List<Faculty> faculties) {
        this.faculties = faculties != null ? faculties : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faculty, parent, false);
        return new FacultyViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        Faculty faculty = faculties.get(position);
        holder.bind(faculty);
    }
    
    @Override
    public int getItemCount() {
        return faculties.size();
    }
    
    /**
     * ViewHolder for faculty items.
     */
    class FacultyViewHolder extends RecyclerView.ViewHolder {
        
        private TextView facultyNameText;
        private TextView facultyDetailsText;
        private ImageButton editButton;
        private ImageButton deleteButton;
        
        public FacultyViewHolder(@NonNull View itemView) {
            super(itemView);
            
            facultyNameText = itemView.findViewById(R.id.facultyNameText);
            facultyDetailsText = itemView.findViewById(R.id.facultyDetailsText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
        
        /**
         * Binds faculty data to the view.
         * 
         * @param faculty The faculty to display
         */
        public void bind(Faculty faculty) {
            facultyNameText.setText(faculty.getName());
            
            // Format details: Subjects | Max Periods
            String subjectsText = formatSubjects(faculty.getSubjectIds());
            String details = String.format("%s | Max %d periods/day",
                    subjectsText,
                    faculty.getMaxPeriodsPerDay());
            
            facultyDetailsText.setText(details);
            
            // Set click listeners
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditFaculty(faculty);
                }
            });
            
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteFaculty(faculty);
                }
            });
        }
        
        /**
         * Formats subject IDs into a readable string.
         * 
         * @param subjectIds The list of subject IDs
         * @return Formatted subjects string
         */
        private String formatSubjects(List<String> subjectIds) {
            if (subjectIds == null || subjectIds.isEmpty()) {
                return "No subjects";
            }
            
            List<String> subjectNames = new ArrayList<>();
            for (String subjectId : subjectIds) {
                Subject subject = repository.getSubject(subjectId);
                if (subject != null) {
                    subjectNames.add(subject.getName());
                }
            }
            
            if (subjectNames.isEmpty()) {
                return "No subjects";
            }
            
            if (subjectNames.size() == 1) {
                return subjectNames.get(0);
            }
            
            if (subjectNames.size() <= 3) {
                return String.join(", ", subjectNames);
            }
            
            // Show first 2 subjects and count
            return subjectNames.get(0) + ", " + subjectNames.get(1) + 
                   " (+" + (subjectNames.size() - 2) + " more)";
        }
    }
}
