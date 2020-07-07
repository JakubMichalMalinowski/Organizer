package com.remote.pum.organizer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView notesRecyclerView;
    private NotesRecyclerViewAdapter notesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Note> notes = new ArrayList<>();
        notes.add(new Note("Do zrobienia"));
        notes.add(new Note("Zakupy").setContent("Aparat\nTelefon\nLaptop"));

        notesRecyclerView = findViewById(R.id.listOfNotesRecyclerView);
        notesRecyclerViewAdapter = new NotesRecyclerViewAdapter(notes);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(notesRecyclerViewAdapter);
    }
}