package com.lbconsulting.architectureexample.models;

import com.google.gson.Gson;

import java.util.Objects;


/**
 * This class holds a Note item data.
 *
 * @author Loren Baker
 * @version 1.0
 * Date: 5/19/2019
 */
@SuppressWarnings("unused")
public class Note {

    private String uid;
    private String title;
    private String titleSortKey;
    private String description;
    private int priority;

    public Note() {
        // Empty constructor required by Firestore
    }

    /**
     * Constructs and initializes a Note item.
     *
     * @param title       The Note's single line title. The titleSortKey is initialized as an upper
     *                    case title to allow for case insensitive sorting by Firestore.
     * @param description The Note's multi-line description.
     * @param priority    The Note's priority (1, 2, or 3).
     */
    public Note(String title, String description, int priority) {
        this.title = title;
        this.titleSortKey = title.toUpperCase();
        this.description = description;
        this.priority = priority;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleSortKey = title.toUpperCase();
    }

    public String getTitleSortKey() {
        return titleSortKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static String toJson(Note note) {
        Gson gson = new Gson();
        return gson.toJson(note);
    }

    public static Note fromJson(String noteJson) {
        Gson gson = new Gson();
        return gson.fromJson(noteJson, Note.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return priority == note.priority &&
                title.equals(note.title) &&
                description.equals(note.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, priority);
    }


}
