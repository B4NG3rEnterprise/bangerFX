package banger.gui.library.views;

import banger.audio.data.Song;
import javafx.collections.ObservableList;

/**
 * Created by Nick on 01.04.2016.
 */
public class AlbumView implements View {

    public AlbumView() {

    }
    @Override
    public void refreshData(ObservableList<Song> songs) {

    }

    @Override
    public void select(Song song) {

    }

    @Override
    public Song getSelectedItem() {
        return null;
    }

    @Override
    public Song[] getSelectedItems() {
        return new Song[0];
    }
}
