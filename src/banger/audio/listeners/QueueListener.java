package banger.audio.listeners;

import banger.audio.data.Song;
import banger.gui.Queue;
import javafx.collections.FXCollections;

import java.util.ArrayList;

public class QueueListener {

    private Queue queue;

    public QueueListener(Queue queue) {
        this.queue = queue;
    }

    public void queueUpdated(ArrayList<Song> songs) {
        queue.setItems(FXCollections.observableList(songs));
        queue.getSelectionModel().clearSelection();
        queue.getSelectionModel().select(songs.get(0));
    }

}
