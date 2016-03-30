package banger.audio.listeners;

import banger.audio.data.Song;
import banger.gui.coverview.CoverView;
import banger.gui.library.views.LyricsView;
import banger.gui.popup.Popup;
import banger.gui.statusbar.StatusBar;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class PlayPauseListener {

    private Stage owner;
    private StatusBar statusbar;
    private LyricsView lyricsView;
    private CoverView coverView;

    public PlayPauseListener(Stage owner, StatusBar statusbar, LyricsView lyricsView, CoverView coverView) {
        this.owner = owner;
        this.statusbar = statusbar;
        this.lyricsView = lyricsView;
        this.coverView = coverView;
    }

    public void statusChanged(boolean playing, Song now) {
        if (playing) {
            statusbar.play();

            // lyricsview.initLyrics(); // TODO only get lyrics when view is selected (IP block!)

            if (owner.isIconified()) {
                TextFlow flow = new TextFlow();
                Text song = new Text(now.getName());
                Text artist = new Text(" - " + now.getArtist());
                song.setStyle("-fx-font-weight: bold; -fx-font-size: 120%;");
                Text album = new Text("\n" + now.getAlbum());
                album.setStyle("-fx-font-size: 90%;");
                flow.getChildren().addAll(song, artist, album);
                new Popup(owner, flow);
            }

            coverView.updateView(now.getName(), now.getArtist(), now.getAlbum(), null);
        } else {
            statusbar.pause();
        }
    }

}