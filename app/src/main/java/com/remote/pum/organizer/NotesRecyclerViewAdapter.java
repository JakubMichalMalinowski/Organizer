package com.remote.pum.organizer;

import android.content.Context;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        switch (viewType) {
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout_with_picture, parent, false);
                return new ViewHolderWithPicture(view);

            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout_with_content, parent, false);
                return new ViewHolderWithContent(view);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 2:
                ((ViewHolderWithPicture)holder).imageView.setImageURI(Uri.parse(notes.get(position).getPicture()));

            case 1:
                ((ViewHolderWithContent)holder).contentTextView.setText(notes.get(position).getContent());

            case 0:
                holder.titleTextView.setText(notes.get(position).getTitle());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
        }
    }

    public static class ViewHolderWithContent extends NotesRecyclerViewAdapter.ViewHolder {
        private TextView contentTextView;

        public ViewHolderWithContent(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.content_text_view);
        }
    }

    public static class ViewHolderWithPicture extends NotesRecyclerViewAdapter.ViewHolderWithContent {
        private ImageView imageView;

        public ViewHolderWithPicture(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }

    public void setRecyclerViewListener(RecyclerViewListener recyclerViewListener) {
        this.recyclerViewListener = recyclerViewListener;
    }

    @Override
    public int getItemViewType(int position) {
       if (notes.get(position).getPicture() != null) {
           return 2;
       }

       if (!notes.get(position).getContent().equals("")) {
           return 1;
       }

       return 0;
    }
}
