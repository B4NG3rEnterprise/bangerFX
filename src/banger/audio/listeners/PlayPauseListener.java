package banger.audio.listeners;

import banger.audio.data.Song;
import banger.gui.coverview.CoverView;
import banger.gui.library.Library;
import banger.gui.options.Options;
import banger.gui.popup.Popup;
import banger.gui.statusbar.StatusBar;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class PlayPauseListener {

    private Stage owner;
    private StatusBar statusbar;
    private Library library;
    private CoverView coverView;

    public PlayPauseListener(Stage owner, StatusBar statusbar, Library library, CoverView coverView) {
        this.owner = owner;
        this.statusbar = statusbar;
        this.library = library;
        this.coverView = coverView;
    }

    public void statusChanged(boolean playing, Song now) {
        if (playing) {
            statusbar.play();
            coverView.updateView(now);

            if (Options.notifications && owner.isIconified()) {
                TextFlow flow = new TextFlow();
                Text song = new Text(now.getName());
                song.setStyle("-fx-font-weight: bold; -fx-font-size: 120%;");
                Text artist = new Text(" - " + now.getArtist());
                Text album = new Text("\n" + now.getAlbum());
                album.setStyle("-fx-font-size: 90%;");
                flow.getChildren().addAll(song, artist, album);
                new Popup(owner, flow);
            }

            if (library.getCurrentView() == Library.VIEW_LYRICS)
                library.refreshData();
        } else {
            statusbar.pause();
        }
    }

}
