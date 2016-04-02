package banger.audio.listeners;

import banger.audio.data.Song;
import banger.gui.coverview.CoverView;
import banger.gui.options.Options;
import banger.gui.popup.Popup;
import banger.gui.statusbar.StatusBar;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class PlayPauseListener {

    private Stage owner;
    private StatusBar statusbar;
    private CoverView coverView;

    public PlayPauseListener(Stage owner, StatusBar statusbar, CoverView coverView) {
        this.owner = owner;
        this.statusbar = statusbar;
        this.coverView = coverView;
    }

    public void statusChanged(boolean playing, Song now) {
        if (playing) {
            statusbar.play();

            if (Options.notifications && owner.isIconified()) {
                TextFlow flow = new TextFlow();
                Text song = new Text(now.getName());
                Text artist = new Text(" - " + now.getArtist());
                song.setStyle("-fx-font-weight: bold; -fx-font-size: 120%;");
                Text album = new Text("\n" + now.getAlbum());
                album.setStyle("-fx-font-size: 90%;");
                flow.getChildren().addAll(song, artist, album);
                new Popup(owner, flow);
            }
            try {
                coverView.updateView(now);
            } catch (NullPointerException e) {
                //Nothing
            }
        } else {
            statusbar.pause();
        }
    }

}
