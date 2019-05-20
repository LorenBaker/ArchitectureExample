package com.lbconsulting.architectureexample.models;

/**
 * This class holds information needed by the NoteAdapter to make transitions for
 * when a Note is "changed", "inserted", "moved", or "removed" from the Note List.
 * <p>
 * The action information comes from the FirestoreNoteRepository getAllNotes EventListener.
 * This class takes the EventListener's "added", "modified", or "removed" types and
 * converts them to the NoteAdapter's "changed", "inserted", "moved", or "removed" actions.
 */
public class NotificationAction {
    public static final String ACTION_CHANGED = "ACTION_CHANGED";
    public static final String ACTION_INSERTED = "ACTION_INSERTED";
    public static final String ACTION_MOVED = "ACTION_MOVED";
    public static final String ACTION_REMOVED = "ACTION_REMOVED";
    public static final String ADDED = "ADDED";
    public static final String MODIFIED = "MODIFIED";
    public static final String REMOVED = "REMOVED";

    private String action;
    private final int oldIndex;
    private final int newIndex;

    public NotificationAction(String action, int oldIndex, int newIndex) {

        this.oldIndex = oldIndex;
        this.newIndex = newIndex;

        switch (action) {
            case ADDED:
                this.action = ACTION_INSERTED;
                break;

            case MODIFIED:
                this.action = ACTION_CHANGED;
                if (oldIndex != newIndex) {
                    this.action = ACTION_MOVED;
                }
                break;

            case REMOVED:
                this.action = ACTION_REMOVED;
                break;

            default:
                this.action = action;
        }
    }

    public String getAction() {
        return action;
    }

    public int getOldIndex() {
        return oldIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }

}
