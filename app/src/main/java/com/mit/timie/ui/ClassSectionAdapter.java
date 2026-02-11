package com.mit.timie.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.ClassSection;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying class sections in a list.
 * Displays class name, section name, and student strength.
 * Provides edit and delete buttons for each item.
 */
public class ClassSectionAdapter extends RecyclerView.Adapter<ClassSectionAdapter.ClassSectionViewHolder> {
    
    private List<ClassSection> classSections;
    private OnClassSectionActionListener listener;
    
    /**
     * Interface for handling class section actions (edit, delete).
     */
    public interface OnClassSectionActionListener {
        void onEditClassSection(ClassSection classSection);
        void onDeleteClassSection(ClassSection classSection);
    }
    
    public ClassSectionAdapter(OnClassSectionActionListener listener) {
        this.classSections = new ArrayList<>();
        this.listener = listener;
    }
    
    /**
     * Updates the class section list and refreshes the view.
     * 
     * @param classSections The new list of class sections
     */
    public void setClassSections(List<ClassSection> classSections) {
        this.classSections = classSections != null ? classSections : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ClassSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassSectionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClassSectionViewHolder holder, int position) {
        ClassSection classSection = classSections.get(position);
        holder.bind(classSection);
    }
    
    @Override
    public int getItemCount() {
        return classSections.size();
    }
    
    /**
     * ViewHolder for class section items.
     */
    class ClassSectionViewHolder extends RecyclerView.ViewHolder {
        
        private TextView classNameText;
        private TextView classDetailsText;
        private ImageButton editButton;
        private ImageButton deleteButton;
        
        public ClassSectionViewHolder(@NonNull View itemView) {
            super(itemView);
            
            classNameText = itemView.findViewById(R.id.classNameText);
            classDetailsText = itemView.findViewById(R.id.classDetailsText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
        
        /**
         * Binds class section data to the view.
         * 
         * @param classSection The class section to display
         */
        public void bind(ClassSection classSection) {
            classNameText.setText(classSection.getClassName());
            
            // Format details: Section | Students
            String details = String.format("Section %s | %d students",
                    classSection.getSectionName(),
                    classSection.getStudentStrength());
            
            classDetailsText.setText(details);
            
            // Set click listeners
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClassSection(classSection);
                }
            });
            
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClassSection(classSection);
                }
            });
        }
    }
}
