package com.remote.pum.organizer;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {
    List<Note> notes;
    RecyclerViewListener recyclerViewListener;
    GestureDetector gestureDetector;

    public NotesRecyclerViewAdapter(Context context, List<Note> notes) {
        this.notes = notes;
        gestureDetector = new GestureDetector(context, new RecyclerViewGestureDetector());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.titleTextView.setText(notes.get(position).getTitle());
        if (notes.get(position).getContent() == null) {
            holder.contentTextView.setText("");
        }
        else {
            holder.contentTextView.setText(notes.get(position).getContent());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewListener.onModifyClick(v, position);
            }
        });

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RecyclerViewGestureDetector.setToDelete(false);
                gestureDetector.onTouchEvent(event);

                if (RecyclerViewGestureDetector.isToDelete()) {
                    recyclerViewListener.onDeleteMotion(v, position);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView contentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
        }
    }

    public void setRecyclerViewListener(RecyclerViewListener recyclerViewListener) {
        this.recyclerViewListener = recyclerViewListener;
    }
}
