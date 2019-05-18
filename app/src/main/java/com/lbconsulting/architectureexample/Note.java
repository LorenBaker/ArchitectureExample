package com.lbconsulting.architectureexample;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private int priority;

//    @Ignore
//    public Note(){
//        this.id = -1;
//        this.title = "Title";
//        this.description = "Description";
//        this.priority = 1;
//    }


    public Note(String title, String description, int priority) {
//        this.id = -1;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

//    @Ignore
//    public Note(int id, String title, String description, int priority) {
//        this.id = id;
//        this.title = title;
//        this.description = description;
//        this.priority = priority;
//    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public static String toJson(Note note) {
        Gson gson = new Gson();
        return gson.toJson(note);
    }

    public static Note fromJson(String noteJson) {
        Gson gson = new Gson();
        return gson.fromJson(noteJson, Note.class);
    }
}
