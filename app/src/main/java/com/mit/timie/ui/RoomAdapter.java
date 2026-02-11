package com.mit.timie.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.timie.R;
import com.mit.timie.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying rooms in a list.
 * Displays room name and type.
 * Provides edit and delete buttons for each item.
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    
    private List<Room> rooms;
    private OnRoomActionListener listener;
    
    /**
     * Interface for handling room actions (edit, delete).
     */
    public interface OnRoomActionListener {
        void onEditRoom(Room room);
        void onDeleteRoom(Room room);
    }
    
    public RoomAdapter(OnRoomActionListener listener) {
        this.rooms = new ArrayList<>();
        this.listener = listener;
    }
    
    /**
     * Updates the room list and refreshes the view.
     * 
     * @param rooms The new list of rooms
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms != null ? rooms : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.bind(room);
    }
    
    @Override
    public int getItemCount() {
        return rooms.size();
    }
    
    /**
     * ViewHolder for room items.
     */
    class RoomViewHolder extends RecyclerView.ViewHolder {
        
        private TextView roomNameText;
        private TextView roomTypeText;
        private ImageButton editButton;
        private ImageButton deleteButton;
        
        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            
            roomNameText = itemView.findViewById(R.id.roomNameText);
            roomTypeText = itemView.findViewById(R.id.roomTypeText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
        
        /**
         * Binds room data to the view.
         * 
         * @param room The room to display
         */
        public void bind(Room room) {
            roomNameText.setText(room.getName());
            roomTypeText.setText(formatRoomType(room.getType()));
            
            // Set click listeners
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditRoom(room);
                }
            });
            
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteRoom(room);
                }
            });
        }
        
        /**
         * Formats room type for display.
         * 
         * @param type The room type
         * @return Formatted type string
         */
        private String formatRoomType(Room.RoomType type) {
            if (type == null) return "Unknown";
            
            switch (type) {
                case CLASSROOM:
                    return "Classroom";
                case LAB:
                    return "Lab";
                default:
                    return type.toString();
            }
        }
    }
}
