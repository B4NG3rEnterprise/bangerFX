package banger.gui;

import banger.Test;
import banger.audio.MusicPlayer;
import banger.gui.statusbar.StatusBar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainView extends Application {


    MusicPlayer player;


	public void start(Stage stage) throws Exception {
        player = Test.getMusicPlayer();

		Scene scene = new Scene(new StatusBar(this));
		scene.getStylesheets().add("banger/gui/statusbar/statusbar.css");
		
		stage.setScene(scene);
        stage.setTitle("MusicPlayer");
        stage.setMinWidth(1000);
        stage.show();
	}

    public MusicPlayer getMusicPlayer() {
        return player;
    }

}
