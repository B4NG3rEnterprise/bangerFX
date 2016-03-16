package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import gui.statusbar.*;

public class MainView extends Application {

	
	public void start(Stage stage) throws Exception {
		
		
		Scene scene = new Scene(new StatusBar());
		scene.getStylesheets().add("./gui/statusbar/statusbar.css");
		
		stage.setScene(scene);
        stage.setTitle("MusicPlayer");
        stage.setMinWidth(1000);
        stage.show();
	}

}
