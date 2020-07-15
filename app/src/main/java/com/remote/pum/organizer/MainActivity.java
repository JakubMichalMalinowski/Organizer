package com.remote.pum.organizer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewListener {
    private RecyclerView notesRecyclerView;
    private NotesRecyclerViewAdapter notesRecyclerViewAdapter;
    private File data;
    private List<Note> notes;

    @Override
    @SuppressWarnings("unchecked")
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
                closeAppWithFileError("Nie udało sie utworzyć pliku.").show();
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

                notes = new ArrayList<>();
                saveData();
            } else {
                closeAppWithFileError("Nie udało się utworzyć pliku.").show();
            }
        } else {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(data))) {
                notes = (List<Note>) objectInputStream.readObject();
                if (!notes.isEmpty()) {
                    Toast.makeText(this, "Załadowano notatki", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Brak notatek", Toast.LENGTH_LONG).show();
                }
            } catch (IOException | ClassNotFoundException e) {
                closeAppWithFileError("Błąd odczytu danych.").show();
            }
        }

        if (notes != null) {
            final SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

            notesRecyclerView = findViewById(R.id.list_of_notes_recycler_view);

            notesRecyclerViewAdapter = new NotesRecyclerViewAdapter(this, preferences, notes);
            notesRecyclerViewAdapter.setRecyclerViewListener(this);

            notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            notesRecyclerView.setAdapter(notesRecyclerViewAdapter);
            notesRecyclerView.setItemAnimator(new DefaultItemAnimator());

            if (preferences.getBoolean("show_help", true)) {
                buildHelpBaseAlertDialogBuilder().setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).setNegativeButton("Nie pokazuj ponownie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferences.edit().putBoolean("show_help", false).apply();
                    }
                }).create().show();
            }
        }
    }

    private AlertDialog closeAppWithFileError(String additionalInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Błąd dostępu do danych, uruchom aplikację ponownie, a w przypadku niepowodzenia wyczyść dane aplikacji.\n" + additionalInfo);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder.create();
    }

    private void saveData() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(data))) {
            objectOutputStream.writeObject(notes);
            Toast.makeText(this, "Zapisano", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            closeAppWithFileError("Błąd zapisu danych.").show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_note) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tytuł notatki");

            final View view = getLayoutInflater().inflate(R.layout.add_note_layout, null);
            builder.setView(view);

            builder.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText titleEditText = view.findViewById(R.id.title_edit_text);
                    String title = titleEditText.getText().toString();
                    if (!title.equals("")) {
                        notes.add(new Note(title));
                        saveData();
                    } else {
                        Toast.makeText(getApplicationContext(), "Anulowano. Wprowadzono pusty tytuł", Toast.LENGTH_LONG).show();
                    }
                }
            });

            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.create().show();
        }

        if (item.getItemId() == R.id.choose_color) {
            final SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("Kolor notatek zmieni sie po restarcie aplikacji")
                    .initialColor(preferences.getInt("note_color", Color.WHITE))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton("ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            preferences.edit().putInt("note_color", selectedColor).apply();
                            Toast.makeText(getApplicationContext(), "Kolor notatek zmieni się po ponownym uruchomieniu aplikacji", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .build()
                    .show();
        }

        if (item.getItemId() == R.id.help) {
            buildHelpBaseAlertDialogBuilder().setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }).create().show();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            Note note = (Note) data.getSerializableExtra("return_note");

            if (note != null) {
                notes.get(requestCode).setTitle(note.getTitle());
                notes.get(requestCode).setContent(note.getContent());
                notes.get(requestCode).setPicture(note.getPicture());
                notes.get(requestCode).setDate(note.getDateDate());
                notes.get(requestCode).setLocation(note.getLocation());
            }
        }

        notesRecyclerViewAdapter.notifyItemChanged(requestCode);
        saveData();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onModifyClick(View view, int position) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("current_note", notes.get(position));
        startActivityForResult(intent, position);
    }

    @Override
    public void onDeleteMotion(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usuwanie notatki")
                .setMessage("Czy na pewno chcesz usunąć tą notatkę?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notes.remove(position);
                        notesRecyclerViewAdapter.notifyItemRemoved(position);
                        notesRecyclerViewAdapter.notifyItemRangeChanged(0, notes.size());
                        saveData();
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    private AlertDialog.Builder buildHelpBaseAlertDialogBuilder() {
        return new AlertDialog.Builder(this).setTitle("Informacje i pomoc")
                .setMessage("Aplikacja umożliwiwa dodawanie SZYBKICH NOTATEK, które są reprezentowane tylko tytułem. " +
                        "Po dotknięciu takiej notatki możliwa jest jej edycja i rozbudowanie jej o opis oraz dodanie obrazka. " +
                        "Zapis danej notatki po modyfikacjach następuje po dotknięciu przycisku powrotu, zrobiono tak ze względu na wygode i bezpieczeństwo użytkownika. " +
                        "Usunięcie następuje poprzez przesuniecie danej notatki w prawo bądź lewo. " +
                        "Aby zobaczyć opis danego przycisku należy go przytrzymać. " +
                        "W razie wątpliwości można wrócić do tej informacji poprzez dotknięcie znaku zapytania w górnej belce aplikacji. ");
    }
}