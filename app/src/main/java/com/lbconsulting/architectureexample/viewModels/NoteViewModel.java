package com.lbconsulting.architectureexample.viewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lbconsulting.architectureexample.models.Note;
import com.lbconsulting.architectureexample.repositories.Note2Repository;

import java.util.List;

import timber.log.Timber;

public class NoteViewModel extends AndroidViewModel {
    private final Note2Repository repository;
    private final LiveData<List<Note>> allNotes;

    public NoteViewModel(Application application) {
        super(application);
        Timber.i("NoteViewModel initialized.");
        repository = new Note2Repository(false);
        allNotes = repository.getAllNotes();
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.i("onCleared()");
        repository.removeAllNotesEventListener();
    }
}
