package com.remote.pum.organizer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView notesRecyclerView;
    private NotesRecyclerViewAdapter notesRecyclerViewAdapter;
    private File data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new File(getApplicationContext().getFilesDir(), "notes_data.data");

        if (!data.exists()) {
            boolean created = false;

            try {
                created = data.createNewFile();
                //throw new IOException();
            } catch (IOException e) {
                closeAppWithFileError().show();
            }

            if (created) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Brak pliku z danymi. Został utworzony nowy pusty plik.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Brak notatek", Toast.LENGTH_LONG).show();
                    }
                });
                builder.create().show();
            }
            else {
                closeAppWithFileError().show();
            }
        }
        else {
            Toast.makeText(this, "Załadowano notatki", Toast.LENGTH_LONG).show();
        }

        List<Note> notes = new ArrayList<>();
        notes.add(new Note("Do zrobienia"));
        notes.add(new Note("Zakupy").setContent("Aparat\nTelefon\nLaptop"));
        notes.add(new Note("Przykład dłuższego tekstu").setContent("Prezentowanie danych w postaci listy elementów to bardzo popularny wzorzec aplikacji mobilnych. Wystarczy otworzyć kilka aplikacji od Google, aby się o tym przekonać. Google Play wyświetla aplikacje w postaci listy. Gmail wyświetla e-maile w postaci listy. Google+ również wyświetla zawartość w postaci listy elementów."));
        notes.add(new Note("Mercedes-Benz").setContent("Mercedes-Benz – marka samochodów produkowanych przez koncern Daimler AG, zaś wcześniej przez koncern Daimler-Benz, popularnie nazywana Mercedes. Pod marką tą produkowane są samochody osobowe, dostawcze, ciężarowe i autobusy. W kategorii samochodów osobowych, Mercedes-Benz uważany jest za jedną z najstarszych."));
        notes.add(new Note("Fendt").setContent("Fendt – niemiecki producent ciągników, kombajnów i maszyn rolniczych. Jest częścią AGCO Corporation. Został założony w 1937 roku przez Xaver Fendta i zakupiony przez AGCO w 1997 roku."));

        notesRecyclerView = findViewById(R.id.listOfNotesRecyclerView);
        notesRecyclerViewAdapter = new NotesRecyclerViewAdapter(notes);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(notesRecyclerViewAdapter);
    }

    private AlertDialog closeAppWithFileError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Nie udało się utworzyć pliku z danymi, należy uruchomić aplikację ponownie.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder.create();
    }
}