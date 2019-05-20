package com.lbconsulting.architectureexample.models;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * This class holds the result of the Firestore EventListener.
 *
 * @author Loren Baker
 * @version 1.0
 * Date: 5/19/2019
 */
public class FirestoreListenerResult {
    private final NotificationAction notificationAction;
    private final List<Note> notes;

    public FirestoreListenerResult(@NotNull NotificationAction notificationAction, @NotNull List<Note> notes) {
        this.notificationAction = notificationAction;
        this.notes = notes;
    }

    public NotificationAction getNotificationAction() {
        return notificationAction;
    }

    public List<Note> getNotes() {
        return notes;
    }
}
