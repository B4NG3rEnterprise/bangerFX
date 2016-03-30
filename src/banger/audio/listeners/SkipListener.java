package banger.audio.listeners;

import banger.audio.data.Song;
import banger.gui.Queue;
import banger.gui.library.Library;

public class SkipListener {

    public enum Skip {
        FORWARD, BACKWARD
    }

    private Library library;
    private Queue queue;

    public SkipListener(Library lib, Queue q) {
        library = lib;
        queue = q;
    }

    public void skipped(Skip dir, Song next) {
        library.select(next);
        queue.getSelectionModel().clearSelection();
        queue.getSelectionModel().select(next);
        switch(dir) {
            case FORWARD:
                break;
            case BACKWARD:
                break;
        }
    }

}
