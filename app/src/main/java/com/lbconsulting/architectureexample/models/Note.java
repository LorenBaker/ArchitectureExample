package com.lbconsulting.architectureexample.models;

import com.google.gson.Gson;


public class Note {

    private String uid;
    private String title;
    private String description;
    private int priority;

    public Note() {

    }

    public Note(String title, String description, int priority) {
        this.title = title;
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
}
