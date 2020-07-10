package com.remote.pum.organizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NoteActivity extends AppCompatActivity {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_picture_menu_item) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            String[] mimeTypes = {"image/jpeg", "image/png", "image/bmp"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(intent, 1);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            note.setPicture(data.getData().toString());
        }
    }
}