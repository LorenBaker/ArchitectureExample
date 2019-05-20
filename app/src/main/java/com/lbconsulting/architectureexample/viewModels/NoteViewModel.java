package com.lbconsulting.architectureexample.viewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lbconsulting.architectureexample.models.FirestoreListenerResult;
import com.lbconsulting.architectureexample.models.Note;
import com.lbconsulting.architectureexample.repositories.FirestoreNoteRepository;

import timber.log.Timber;


/**
 * The ViewModel provides the data for the app's RecyclerView.
 * It initializes the FirestoreNoteRepository.
 *
 * @author Loren Baker
 * @version 1.0
 * Date: 5/19/2019
 */
public class NoteViewModel extends AndroidViewModel {
    private final FirestoreNoteRepository repository;
    private final LiveData<FirestoreListenerResult> allNotes;

    public NoteViewModel(Application application) {
        super(application);
        Timber.i("NoteViewModel initialized.");
        repository = new FirestoreNoteRepository(false);
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

    public LiveData<FirestoreListenerResult> getAllNotes() {
        return allNotes;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.i("onCleared()");
        repository.removeAllNotesEventListener();
    }
}
