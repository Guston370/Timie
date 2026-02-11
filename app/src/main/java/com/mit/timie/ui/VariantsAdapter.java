package com.mit.timie.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Timetable;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying timetable variants.
 * Shows variant name and provides a "View" button for each variant.
 */
public class VariantsAdapter extends RecyclerView.Adapter<VariantsAdapter.VariantViewHolder> {
    
    private List<Timetable> variants;
    private OnVariantClickListener listener;
    
    /**
     * Interface for handling variant click events.
     */
    public interface OnVariantClickListener {
        void onVariantClick(Timetable timetable);
    }
    
    /**
     * Constructor for VariantsAdapter.
     * 
     * @param listener Listener for variant click events
     */
    public VariantsAdapter(OnVariantClickListener listener) {
        this.variants = new ArrayList<>();
        this.listener = listener;
    }
    
    /**
     * Sets the list of timetable variants to display.
     * 
     * @param variants List of timetable variants
     */
    public void setVariants(List<Timetable> variants) {
        this.variants = variants != null ? variants : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_variant, parent, false);
        return new VariantViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        Timetable timetable = variants.get(position);
        holder.bind(timetable, listener);
    }
    
    @Override
    public int getItemCount() {
        return variants.size();
    }
    
    /**
     * ViewHolder for variant items.
     */
    static class VariantViewHolder extends RecyclerView.ViewHolder {
        
        private final TextView variantNameText;
        private final TextView variantDescriptionText;
        private final Button viewButton;
        
        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);
            variantNameText = itemView.findViewById(R.id.variantNameText);
            variantDescriptionText = itemView.findViewById(R.id.variantDescriptionText);
            viewButton = itemView.findViewById(R.id.viewButton);
        }
        
        /**
         * Binds timetable data to the view.
         * 
         * @param timetable The timetable variant to display
         * @param listener Listener for click events
         */
        public void bind(Timetable timetable, OnVariantClickListener listener) {
            // Display variant name
            variantNameText.setText(timetable.getVariantName());
            
            // Display variant description based on variant name
            String description = getVariantDescription(timetable.getVariantName());
            variantDescriptionText.setText(description);
            
            // Set up view button click listener
            viewButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVariantClick(timetable);
                }
            });
        }
        
        /**
         * Gets a description for the variant based on its name.
         * 
         * @param variantName The name of the variant
         * @return Description text for the variant
         */
        private String getVariantDescription(String variantName) {
            if (variantName == null) {
                return "Timetable variant";
            }
            
            switch (variantName) {
                case "Balanced":
                    return "Subjects distributed evenly across days";
                case "Compact":
                    return "Minimizes gaps and free periods";
                case "Faculty-friendly":
                    return "Respects faculty preferences for consecutive periods";
                default:
                    return "Timetable variant";
            }
        }
    }
}
