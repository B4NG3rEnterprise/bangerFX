package banger.gui.library.views;

import banger.audio.data.Song;
import javafx.collections.ObservableList;

public interface View {

    void refreshData(ObservableList<Song> songs);

    void select(Song song);

    Song getSelectedItem();

    Song[] getSelectedItems();

}
