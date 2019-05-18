package com.lbconsulting.architectureexample.viewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lbconsulting.architectureexample.models.Note2;
import com.lbconsulting.architectureexample.repositories.Note2Repository;

import java.util.List;

import timber.log.Timber;

public class Note2ViewModel extends AndroidViewModel {
    private final Note2Repository repository;
    private final LiveData<List<Note2>> allNotes;

    public Note2ViewModel(Application application) {
        super(application);
        Timber.i("Note2ViewModel initialized.");
        repository = new Note2Repository(false);
        allNotes = repository.getAllNotes();
    }

    public void insert(Note2 note) {
        repository.insert(note);
    }

    public void update(Note2 note) {
        repository.update(note);
    }

    public void delete(Note2 note) {
        repository.delete(note);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<Note2>> getAllNotes() {
        return allNotes;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.i("onCleared()");
        repository.removeAllNotesEventListener();
    }
}
