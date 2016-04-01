package banger.audio.listeners;

import banger.audio.data.Song;
import banger.gui.Queue;
import javafx.collections.ObservableList;

public class QueueListener {

    private Queue queue;
    private int scrollPos;

    public QueueListener(Queue queue) {
        this.queue = queue;
        scrollPos = 0;
    }

    public void queueUpdated(ObservableList<Song> songs, int queueIndex) {
        queue.setItems(songs);
        queue.getSelectionModel().clearSelection();
        queue.getSelectionModel().select(queueIndex);
        if (queueIndex - scrollPos > 8)
            scrollPos = queueIndex - 8;
        else if (queueIndex - scrollPos < 0)
            scrollPos = queueIndex - 5;

        queue.scrollTo(scrollPos);
    }

}
