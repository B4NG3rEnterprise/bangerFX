package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainView extends Application {

	
	public void start(Stage stage) throws Exception {
		
		
		Scene scene = new Scene(new StatusBar());
		
		
		stage.setScene(scene);
        stage.setTitle("MusicPlayer");
        stage.setMinWidth(1400);
        stage.show();
	}

}