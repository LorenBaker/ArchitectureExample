package com.lbconsulting.architectureexample.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.lbconsulting.architectureexample.R;
import com.lbconsulting.architectureexample.models.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends Adapter<NoteAdapter.NoteHolder> {
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
//        holder.tvDescription.setText(currentNote.toString());
        holder.tvPriority.setText(String.valueOf(currentNote.getPriority()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvPriority;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPriority = itemView.findViewById(R.id.tvPriority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Note selectedNote = notes.get(position);
                    if (mListener != null && position != RecyclerView.NO_POSITION) {
                        mListener.onNoteClick(selectedNote, position);
                    }
                }
            });
        }
    }

    public interface onNoteClickListener {
        void onNoteClick(Note note, int position);
    }

    public void setOnNoteClickListener(onNoteClickListener listener) {
        mListener = listener;
    }
}
