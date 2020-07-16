package com.remote.pum.organizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {
    private List<Note> notes;
    private GestureDetector gestureDetector;
    private AlertDialog pictureNotExistAlertDialog;
    private SharedPreferences preferences;
    private RecyclerViewGestureDetector recyclerViewGestureDetector;

    public NotesRecyclerViewAdapter(Context context, SharedPreferences preferences, List<Note> notes) {
        this.notes = notes;
        this.preferences = preferences;
        recyclerViewGestureDetector = new RecyclerViewGestureDetector();
        gestureDetector = new GestureDetector(context, recyclerViewGestureDetector);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        pictureNotExistAlertDialog = builder.setMessage("Niektóre obrazy nie istnieją/zosatały usunięte z oryginalnej lokalizacji. Aby zapobiec zbędnemu pokazywaniu komunikatu należy usunąć obraz z poziomu notatki.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        }).create();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);

        switch (viewType) {
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout_with_picture, parent, false);
                setColor(view);
                return new ViewHolderWithPicture(view);

            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout_with_content, parent, false);
                setColor(view);
                return new ViewHolderWithContent(view);
        }

        setColor(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 2:
                File photo = new File(notes.get(position).getPicture());
                if (photo.exists()) {
                    ((ViewHolderWithPicture) holder).imageView.setImageURI(Uri.parse(photo.toString()));
                } else {
                    pictureNotExistAlertDialog.show();
                }

            case 1:
                ((ViewHolderWithContent) holder).contentTextView.setText(notes.get(position).getContent());

            case 0:
                holder.titleTextView.setText(notes.get(position).getTitle());
        }

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                recyclerViewGestureDetector.setView(v);
                recyclerViewGestureDetector.setPosition(position);
                gestureDetector.onTouchEvent(event);
                return true;
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
        this.recyclerViewGestureDetector.setRecyclerViewListener(recyclerViewListener);
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

    private void setColor(View view) {
        CardView cardView = view.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor(preferences.getInt("note_color", Color.parseColor("#FFFACD")));
    }
}
