package com.lbconsulting.architectureexample.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lbconsulting.architectureexample.R;
import com.lbconsulting.architectureexample.models.FirestoreListenerResult;
import com.lbconsulting.architectureexample.models.Note;
import com.lbconsulting.architectureexample.models.NotificationAction;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * This Adapter populates a RecyclerView with Note items.
 *
 * @author Loren Baker
 * @version 1.0
 * Date: 5/19/2019
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private List<Note> notes = new ArrayList<>();
    private onNoteClickListener mListener;


    @NonNull
    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);

        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.tvTitle.setText(currentNote.getTitle());
        holder.tvDescription.setText(currentNote.getDescription());
        holder.tvPriority.setText(String.valueOf(currentNote.getPriority()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * The RecyclerView updates the UI via this setFirestoreListenerResult function.
     *
     * @param result Contains an updated Notes list and an "action" indicating
     *               whether a Note was "inserted (created)", "changed", "moved",
     *               or "removed". It also contains the updated Note's
     *               old and new RecyclerView position index. The action information
     *               comes from the FirestoreNoteRepository getAllNotes EventListener.
     */
    public void setFirestoreListenerResult(FirestoreListenerResult result) {
        this.notes = result.getNotes();
        String action = result.getNotificationAction().getAction();
        switch (action) {
            case NotificationAction.ACTION_INSERTED:
                notifyItemInserted(result.getNotificationAction().getNewIndex());
                break;

            case NotificationAction.ACTION_CHANGED:
                notifyItemChanged(result.getNotificationAction().getOldIndex());
                break;

            case NotificationAction.ACTION_MOVED:
                notifyItemChanged(result.getNotificationAction().getOldIndex());
                notifyItemMoved(result.getNotificationAction().getOldIndex(),
                        result.getNotificationAction().getNewIndex());
                break;

            case NotificationAction.ACTION_REMOVED:
                notifyItemRemoved(result.getNotificationAction().getOldIndex());
                break;

            default:
                Timber.e("setFirestoreListenerResult(): Invalid NotificationAction");
        }
    }

    public Note getNote(int position) {
        return notes.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvPriority;

        NoteHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPriority = itemView.findViewById(R.id.tvPriority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Note selectedNote = notes.get(position);
                    if (mListener != null) {
                        mListener.onNoteClick(selectedNote, position);
                    }
                }
            });
        }
    }

    public interface onNoteClickListener {
        void onNoteClick(Note note, @SuppressWarnings("unused") int position);
    }

    public void setOnNoteClickListener(onNoteClickListener listener) {
        mListener = listener;
    }
}
