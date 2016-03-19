package banger.gui;

import banger.audio.MusicPlayer;
import banger.audio.Song;
import banger.gui.menubar.BangerBar;
import banger.gui.statusbar.StatusBar;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.Iterator;


public class MainView extends Application {

    MusicPlayer player;

    StatusBar statusbar;
    Library library;
    BangerBar bangerBar;

    public void start(Stage stage) throws Exception {
        player = new MusicPlayer(this);

        statusbar = new StatusBar(this);
        statusbar.setCustomBackground(Paint.valueOf("#FA7D38"));
        statusbar.setMinSize(0, 40);

        library = new Library(this);
        library.setMinSize(0,0);

        bangerBar = new BangerBar(this);
        bangerBar.setMinSize(0,0);

        BorderPane bl = new BorderPane();
        bl.setTop(bangerBar);
        bl.setCenter(library);
        bl.setBottom(statusbar);

        Scene scene = new Scene(bl);
		scene.getStylesheets().add("banger/gui/statusbar/statusbar.css");

		stage.setScene(scene);
        stage.setTitle("MusicPlayer");
        stage.setMinWidth(1000);
        stage.setMaxWidth(1400);
        stage.setMinHeight(120);
        stage.setOnCloseRequest(e -> {
            player.kill();
            System.exit(0);
        });
        stage.show();
	}

    public MusicPlayer getMusicPlayer() {
        return player;
    }

    public Library getLibrary() {
        return library;
    }

    public void play(Song s) {
        player.play(s);
        statusbar.play();
    }

    public void play() {
        player.play();
        statusbar.play();
    }

    public void pause() {
        player.pause();
    }

    public void skipForward() {
        for (Iterator<Song> iterator = library.getItems().iterator(); iterator.hasNext(); ) {
            if (iterator.next().equals(player.getNowPlaying())) {
                if (iterator.hasNext())
                    play(iterator.next());
                break;
            }
        }
    }

    //TODO: Think of some better way!
    public void skipBackward() {
        ObservableList<Song> s = library.getItems();
        Collections.reverse(s); //Problem: Changes the underlying list (and with that even the order in the table)
        for (Iterator<Song> iterator = s.iterator(); iterator.hasNext(); ) {
            if (iterator.next().equals(player.getNowPlaying())) {
                if (iterator.hasNext())
                    play(iterator.next());
                break;
            }
        }
        Collections.reverse(s);
    }
}
