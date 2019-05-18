package com.lbconsulting.architectureexample.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lbconsulting.architectureexample.R;
import com.lbconsulting.architectureexample.models.Note2;
import com.lbconsulting.architectureexample.ui.adapters.Note2Adapter;
import com.lbconsulting.architectureexample.viewModels.Note2ViewModel;

import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 10;
    public static final int EDIT_NOTE_REQUEST = 20;

    private Note2ViewModel note2ViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //android.util.Log.i(TAG, "onCreate: ");
        Timber.i("onCreate()");
        setContentView(R.layout.activity_main);


//        noteRepository2 = new Note2Repository(userUid, true);

        FloatingActionButton fabAddNote = findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        RecyclerView rvNotes = findViewById(R.id.rvNotes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        final Note2Adapter adapter = new Note2Adapter();
        rvNotes.setAdapter(adapter);

        note2ViewModel = ViewModelProviders.of(this).get(Note2ViewModel.class);
        note2ViewModel.getAllNotes().observe(this, new Observer<List<Note2>>() {

            @Override
            public void onChanged(List<Note2> notes) {
                // update UI (i.e.: the RecyclerView)
                adapter.setNotes(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int positon = viewHolder.getAdapterPosition();
                Note2 note = adapter.getNoteAt(positon);
                if (note != null) {
                    note2ViewModel.delete(note);
                    Toast.makeText(MainActivity.this, String.format("Note \"%s\" deleted.", note.getTitle()), Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(rvNotes);

        adapter.setOnNoteClickListener(new Note2Adapter.onNoteClickListener() {
            @Override
            public void onNoteClick(Note2 note, int position) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.NOTE_JSON, Note2.toJson(note));
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
    }
//    private Note2Repository noteRepository2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.i("onActivityResult()");

        if (data != null) {
            if (data.hasExtra(AddEditNoteActivity.NOTE_JSON)) {
                String noteJson = data.getStringExtra(AddEditNoteActivity.NOTE_JSON);
                Note2 note = Note2.fromJson(noteJson);
                if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
                    note2ViewModel.insert(note);
                    Toast.makeText(this, String.format(
                            "Note \"%s\" inserted.", note.getTitle()),
                            Toast.LENGTH_SHORT).show();

                } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
                    note2ViewModel.update(note);
                    Toast.makeText(this, String.format(
                            "Note \"%s\" updated.", note.getTitle()),
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Save note canceled.", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Toast.makeText(this, "Save note canceled.", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                note2ViewModel.deleteAllNotes();
                Toast.makeText(this, "All notes deleted.", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
