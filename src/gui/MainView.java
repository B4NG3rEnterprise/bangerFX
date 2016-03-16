package gui;

import audio.MusicPlayer;
import gui.statusbar.StatusBar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.Method;


public class MainView extends Application {


    MusicPlayer player;


	public void start(Stage stage) throws Exception {
        Class c = Class.forName("Test");
        Method m = c.getMethod("getMusicPlayer", null);

        player = (MusicPlayer) m.invoke(null);

		Scene scene = new Scene(new StatusBar(this));
		scene.getStylesheets().add("./gui/statusbar/statusbar.css");
		
		stage.setScene(scene);
        stage.setTitle("MusicPlayer");
        stage.setMinWidth(1000);
        stage.show();
	}

    public MusicPlayer getMusicPlayer() {
        return player;
    }

}
