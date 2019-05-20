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
import com.lbconsulting.architectureexample.models.FirestoreListenerResult;
import com.lbconsulting.architectureexample.models.Note;
import com.lbconsulting.architectureexample.models.NotificationAction;
import com.lbconsulting.architectureexample.ui.adapters.NoteAdapter;
import com.lbconsulting.architectureexample.viewModels.NoteViewModel;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_NOTE_REQUEST = 10;
    private static final int EDIT_NOTE_REQUEST = 20;

    private NoteViewModel noteViewModel;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        setContentView(R.layout.activity_main);


        FloatingActionButton fabAddNote = findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        RecyclerView rvNotes = findViewById(R.id.rvNotes);
        layoutManager = new LinearLayoutManager(this);
        rvNotes.setLayoutManager(layoutManager);
        rvNotes.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        rvNotes.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<FirestoreListenerResult>() {
            @Override
            public void onChanged(FirestoreListenerResult result) {
                // update UI (i.e.: the RecyclerView)
                adapter.setFirestoreListenerResult(result);
                if (result.getNotificationAction().getAction().equals(NotificationAction.ACTION_MOVED)) {
                    Timber.i("getAllNotes.observe(): Modified Note Moved: Scrolling to position=%d", result.getNotificationAction().getNewIndex());
                    layoutManager.scrollToPositionWithOffset(result.getNotificationAction().getNewIndex(), 150);
                }
            }
//
//            @Override
//            public void onChanged(FirestoreListenerResult result) {
//                // update UI (i.e.: the RecyclerView)
//                adapter.setFirestoreListenerResult(result);
//            }

        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = adapter.getNote(position);
                if (note != null) {
                    noteViewModel.delete(note);
                    Toast.makeText(MainActivity.this, String.format("Note \"%s\" deleted.", note.getTitle()), Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(rvNotes);

        adapter.setOnNoteClickListener(new NoteAdapter.onNoteClickListener() {
            @Override
            public void onNoteClick(Note note, int position) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.NOTE_JSON, Note.toJson(note));
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.i("onActivityResult()");

        if (data != null) {
            if (data.hasExtra(AddEditNoteActivity.NOTE_JSON)) {
                String noteJson = data.getStringExtra(AddEditNoteActivity.NOTE_JSON);
                Note note = Note.fromJson(noteJson);
                if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
                    noteViewModel.insert(note);
                    Timber.i("onActivityResult(): Note \"%s\" inserted.", note.getTitle());
//                    Toast.makeText(this, String.format(
//                            "Note \"%s\" inserted.", note.getTitle()),
//                            Toast.LENGTH_SHORT).show();

                } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
                    noteViewModel.update(note);
                    Timber.i("onActivityResult()L Note \"%s\" updated.", note.getTitle());
//                    Toast.makeText(this, String.format(
//                            "Note \"%s\" updated.", note.getTitle()),
//                            Toast.LENGTH_SHORT).show();

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
        if (item.getItemId() == R.id.delete_all_notes) {
            noteViewModel.deleteAllNotes();
            Toast.makeText(this, "All notes deleted.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
