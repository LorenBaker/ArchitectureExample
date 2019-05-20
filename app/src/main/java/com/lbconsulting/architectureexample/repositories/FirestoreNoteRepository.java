package com.lbconsulting.architectureexample.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.lbconsulting.architectureexample.models.FirestoreListenerResult;
import com.lbconsulting.architectureexample.models.Note;
import com.lbconsulting.architectureexample.models.NotificationAction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import timber.log.Timber;

public class FirestoreNoteRepository {
    private static final String USERS_COLLECTION = "users";
    private static final String NOTES_COLLECTION = "notes";

    // Access a Cloud Firestore instance
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Hard coded userUid
    // TODO: 2019-05-19 Get userUid from Firebase
    @SuppressWarnings("SpellCheckingInspection")
    private final String mUserUid = "gEwXzgGLnhdOLsFiLLlLKGIHyyB2";

    private final String mNotesCollectionPath = USERS_COLLECTION + "/" + mUserUid + "/" + NOTES_COLLECTION;
    private final List<Note> mNotes = new ArrayList<>();
    private final MutableLiveData<FirestoreListenerResult> mLiveData = new MutableLiveData<>();
    private final Query mAllNotesQuery = db.collection(mNotesCollectionPath)
            .orderBy("priority", Query.Direction.ASCENDING)
            .orderBy("titleSortKey", Query.Direction.ASCENDING);
    private ListenerRegistration mAllNotesEventListener;

    public FirestoreNoteRepository(boolean populateFirestoreDb) {
        Timber.i("FirestoreNoteRepository initialized.");
        if (populateFirestoreDb) {
            populateFirestoreDatabase();
        }
    }

    /**
     * This function adds Notes to the Firestore database.
     */
    private void populateFirestoreDatabase() {
        Timber.i("populateFirestoreDatabase()");
        // Get a new write batch
        WriteBatch batch = db.batch();
        final int numberOfNotes = 27;
        int priority = 1;
        DocumentReference noteRef;
        String uid;

        for (int i = 0; i < numberOfNotes; i++) {
            int number = i + 1;
            String title = String.format(Locale.getDefault(), "Title %02d", number);
            String description = String.format(Locale.getDefault(), "Description %02d", number);
            Note note = new Note(title, description, priority);

            noteRef = db.collection(mNotesCollectionPath).document();
            uid = noteRef.getId();
            note.setUid(uid);
            batch.set(noteRef, note);

            priority++;
            if (priority > 3) {
                priority = 1;
            }
        }

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Timber.i("populateFirestoreDatabase onComplete(): Firestore database populated with %d notes.", numberOfNotes);
            }
        });
    }

    /**
     * This getAllNotes() function initializes the Firestore EventListener that listens
     * for changes in the user's Firestore notes collection.
     *
     * @return A FirestoreListenerResult object. This function takes the EventListener's "added", "modified",
     * or "removed" types and converts them to the NoteAdapter's "changed", "inserted", "moved", or
     * "removed" actions. It also contains the updated Note's old and new RecyclerView position index.
     */
    public LiveData<FirestoreListenerResult> getAllNotes() {
        Timber.i("getAllNotes()");

        mAllNotesEventListener = mAllNotesQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Timber.e("getAllNotes onEvent(): Listen failed. FirebaseFirestoreException = %s", e.getMessage());
                    return;
                }
                if (snapshots != null) {
                    Timber.i("allNotesEventListener: %d snapshots changed.", snapshots.getDocumentChanges().size());
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Note note = dc.getDocument().toObject(Note.class);
                        NotificationAction action;
                        FirestoreListenerResult result;

                        switch (dc.getType()) {
                            case ADDED:
                                mNotes.add(dc.getNewIndex(), note);
                                action = new NotificationAction(NotificationAction.ADDED, dc.getOldIndex(), dc.getNewIndex());
                                result = new FirestoreListenerResult(action, mNotes);
                                mLiveData.setValue(result);
                                Timber.i("allNotesEventListener: Note \"%s\" ADDED at index=%d.",
                                        note.getTitle(), dc.getNewIndex());
                                break;

                            case MODIFIED:
                                mNotes.remove(dc.getOldIndex());
                                mNotes.add(dc.getNewIndex(), note);
                                action = new NotificationAction(NotificationAction.MODIFIED, dc.getOldIndex(), dc.getNewIndex());
                                result = new FirestoreListenerResult(action, mNotes);
                                mLiveData.setValue(result);
                                Timber.i("allNotesEventListener: Note \"%s\" MODIFIED. Removed Note at index=%d. Added Note at index=%d.",
                                        note.getTitle(), dc.getOldIndex(), dc.getNewIndex());
                                break;

                            case REMOVED:
                                mNotes.remove(dc.getOldIndex());
                                action = new NotificationAction(NotificationAction.REMOVED, dc.getOldIndex(), dc.getNewIndex());
                                result = new FirestoreListenerResult(action, mNotes);
                                mLiveData.setValue(result);
                                Timber.i("allNotesEventListener: Note \"%s\" REMOVED at index=%d.",
                                        note.getTitle(), dc.getOldIndex());
                                break;
                        }
                    }
                }
            }
        });

        return mLiveData;
    }

    public void removeAllNotesEventListener() {
        Timber.i("removeAllNotesEventListener()");
        if (mAllNotesEventListener != null) {
            mAllNotesEventListener.remove();
        }
    }

    public void insert(@NotNull final Note note) {
        Timber.i("insert(): Note \"%s\"", note.getTitle());

        DocumentReference newNoteRef = db.collection(mNotesCollectionPath).document();
        String uid = newNoteRef.getId();
        note.setUid(uid);

        newNoteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.i("onSuccess(): Note \"%s\" successfully inserted.",
                                note.getTitle());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.e("onFailure(): Error inserting Note \"%s\". Exception: %s.",
                                note.getTitle(), e.getMessage());
                    }
                });

    }

    public void update(@NotNull final Note note) {
        Timber.i("update(): Note \"%s\"", note.getTitle());

        if (note.getUid() != null && !note.getUid().isEmpty()) {
            DocumentReference noteRef = db.collection(mNotesCollectionPath).document(note.getUid());

            noteRef.set(note)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Timber.i("onSuccess(): Note \"%s\" successfully updated.", note.getTitle());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.e("onFailure(): Error updating Note \"%s\". Exception: %s.",
                                    note.getTitle(), e.getMessage());
                        }
                    });
        } else {
            Timber.e("update(): Unable to update Note \"%s\". The note's uid does not exist! ", note.getTitle());
        }
    }

    public void delete(@NotNull final Note note) {
        Timber.i("delete(): Note \"%s\"", note.getTitle());
        DocumentReference noteRef = db.collection(mNotesCollectionPath).document(note.getUid());

        noteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.i("onSuccess(): Note \"%s\" successfully deleted.", note.getTitle());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.e("onFailure(): Error deleting Note \"%s\". Exception: %s.",
                                note.getTitle(), e.getMessage());
                    }
                });

    }

    public void deleteAllNotes() {
        Timber.i("deleteAllNotes()");

        // Get a new write batch
        WriteBatch batch = db.batch();
        final int numberOfNotes;
        if (mNotes.size() > 500) {
            Timber.e("deleteAllNotes(): deleting the first 500 Notes.");
            numberOfNotes = 500;
        } else {
            numberOfNotes = mNotes.size();
        }

        DocumentReference noteRef;
        Note note;

        for (int i = 0; i < numberOfNotes; i++) {
            note = mNotes.get(i);
            noteRef = db.collection(mNotesCollectionPath).document(note.getUid());
            batch.delete(noteRef);
        }

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Timber.i("deleteAllNotes() onComplete(). %d notes deleted.", numberOfNotes);
            }
        });
    }

}
