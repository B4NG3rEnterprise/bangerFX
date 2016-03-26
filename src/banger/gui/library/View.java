package banger.gui.library;

import banger.audio.Song;
import javafx.collections.ObservableList;

public interface View {

    void refreshData(ObservableList<Song> songs);

    void select(Song song);

    Song getSelectedItem();

    Song[] getSelectedItems();

}
