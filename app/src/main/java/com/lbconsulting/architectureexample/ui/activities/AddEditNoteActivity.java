package com.lbconsulting.architectureexample.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lbconsulting.architectureexample.R;
import com.lbconsulting.architectureexample.models.Note2;

import java.util.Objects;

import timber.log.Timber;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String NOTE_JSON = "NOTE_JSON";

    private EditText txtTitle;
    private EditText txtDescription;
    private NumberPicker npPriority;
    private boolean noteExists = false;

    private Note2 mNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        setContentView(R.layout.activity_add_note);

        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        npPriority = findViewById(R.id.npPriority);

        npPriority.setMinValue(1);
        npPriority.setMaxValue(3);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();

        if (intent.hasExtra(NOTE_JSON)) {
            String noteJson = intent.getStringExtra(NOTE_JSON);
            mNote = Note2.fromJson(noteJson);
            if (mNote != null) {
                noteExists = true;
                txtTitle.setText(mNote.getTitle());
                txtDescription.setText(mNote.getDescription());
                npPriority.setValue(mNote.getPriority());
                setTitle("Edit Note");
            }

        } else {
            setTitle("Add Note");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.i("onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.i("onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Timber.i("onRestart()");
    }

    private void saveNote() {
        Timber.i("saveNote()");
        String title = txtTitle.getText().toString().trim();
        String description = txtDescription.getText().toString().trim();
        int priority = npPriority.getValue();


        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Neither Title nor Description can be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent noteIntent = new Intent();

        if (!noteExists) {
            mNote = new Note2(title, description, priority);

        } else {
            mNote.setTitle(title);
            mNote.setDescription(description);
            mNote.setPriority(priority);
        }

        noteIntent.putExtra(NOTE_JSON, Note2.toJson(mNote));
        setResult(RESULT_OK, noteIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
