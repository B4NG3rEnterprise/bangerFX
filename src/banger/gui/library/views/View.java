package banger.gui.library.views;

import banger.audio.data.Song;

public interface View {

    void refreshData();

    void select(Song song);

    Song getSelectedItem();

    Song[] getSelectedItems();
}
