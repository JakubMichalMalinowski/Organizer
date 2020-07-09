package com.remote.pum.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

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
}