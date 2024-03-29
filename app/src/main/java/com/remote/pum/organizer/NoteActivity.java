package com.remote.pum.organizer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Activity służące do modyfikacji notatki
 */
public class NoteActivity extends AppCompatActivity {
    private static final int PICTURE_REQUEST = 1;
    private static final int EVENT_REQUEST = 2;
    private static final int PERMISSION_REQUEST = 3;

    private Note note;

    private EditText titleEditText;
    private EditText contentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        note = (Note) getIntent().getSerializableExtra("current_note");

        titleEditText = findViewById(R.id.title_edit_text_note_activity);
        contentEditText = findViewById(R.id.content_edit_text_note_activity);

        if (note != null) {
            if (note.getTitle() != null) {
                titleEditText.setText(note.getTitle());
            } else {
                titleEditText.setText("");
            }

            if (note.getContent() != null) {
                contentEditText.setText(note.getContent());
            } else {
                contentEditText.setText("");
            }
        }
    }

    /**
     * Wywoływana gdy użytkownik naciśnie przycisk powrotu, ustawiająca tytuł i zawartość do notatki i zwraca tą notatkę do Activity-rodzica
     */
    @Override
    public void onBackPressed() {
        if (note != null) {
            note.setTitle(titleEditText.getText().toString());
            note.setContent(contentEditText.getText().toString());
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("return_note", note);
        setResult(RESULT_OK, returnIntent);

        super.onBackPressed();
    }

    /**
     * Inicjalizacja zawartości menu opcji
     *
     * @param menu menu, w którym umieszczamy zawartość
     * @return true - aby pokazać menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    /**
     * Rozpoznanie wybranej opcji w menu
     *
     * @param item wybrana opcja
     * @return true - jezeli skonsumowane
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //dodanie obrazka do notatki
        if (item.getItemId() == R.id.add_picture_menu_item) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            } else {
                choosePicture();
            }
        }

        //usunięcie obrazka z notatki
        if (item.getItemId() == R.id.remove_picture_menu_item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Usuwanie obrazu")
                    .setMessage("Czy chcesz usunąć obraz z tej notatki?\nObraz nie zostanie usunięty z oryginalnej lokalizacji.")
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            note.setPicture(null);
                        }
                    })
                    .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    }).create().show();
        }

        //dodanie zdarzenia do notatki
        if (item.getItemId() == R.id.add_event_menu_item) {
            Intent intent = new Intent(this, EventActivity.class);
            intent.putExtra("current_note_for_event", note);
            startActivityForResult(intent, EVENT_REQUEST);
        }

        return true;
    }

    /**
     * Pobranie rezultatu z zamykanego Activity
     *
     * @param requestCode kod zapytania
     * @param resultCode  kod wyniku
     * @param data        Intent zawierające zwrócone dane
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //wybór obrazu
        if (requestCode == PICTURE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            String result;
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            if (cursor == null) {
                result = data.getData().getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }

            note.setPicture(result);
        }

        //Activity modyfikujące wydarzenie
        if (requestCode == EVENT_REQUEST && resultCode == RESULT_OK && data != null) {
            Note returnNote = (Note) data.getSerializableExtra("note_return_from_event");
            if (returnNote != null) {
                this.note.setDate(returnNote.getDateDate());
                this.note.setLocation(returnNote.getLocation());
            }
        }
    }

    /**
     * Uruchomienie Activity odpowiedzialnego za wybranie obrazu
     */
    private void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png", "image/bmp"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICTURE_REQUEST);
    }

    /**
     * Wywoływana jako rezultat zapytania o udzielenie pozwoleń
     *
     * @param requestCode  kod zapytania
     * @param permissions  zapytane pozwolenia
     * @param grantResults wyniki czy zostały udzielone pozwolenia czy nie
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePicture();
            } else {
                Toast.makeText(this, "Nie można korzystać z dodawania zdjęć, bo nie udzielono pozwolenia.", Toast.LENGTH_LONG).show();
            }
        }
    }
}