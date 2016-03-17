package banger.gui;

import banger.Test;
import banger.audio.MusicPlayer;
import banger.gui.statusbar.StatusBar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;


public class MainView extends Application {


    MusicPlayer player;


	public void start(Stage stage) throws Exception {
        player = Test.getMusicPlayer();

        StatusBar statusbar = new StatusBar(this);
        statusbar.setCustomBackground(Paint.valueOf("#FA7D38"));

        Scene scene = new Scene(statusbar);
		scene.getStylesheets().add("banger/gui/statusbar/statusbar.css");

		stage.setScene(scene);
        stage.setTitle("MusicPlayer");
        stage.setMinWidth(1000);
        stage.setMinHeight(75);
        stage.show();
	}

    public MusicPlayer getMusicPlayer() {
        return player;
    }

}
