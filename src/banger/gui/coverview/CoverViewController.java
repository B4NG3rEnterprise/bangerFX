package banger.gui.coverview;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import banger.gui.MainView;
import javafx.application.Application;

public class CoverViewController{
	
	@FXML Label title;
	@FXML Label artist;
	@FXML Label album;	
	@FXML ImageView cover;
	
	
	public void update(String ntitle, String nartist, String nalbum, String coverLocation) {

		title.setText(ntitle);
		artist.setText(nartist);
		album.setText(nalbum);
		
		cover.setImage(new Image("file:/C:/Users/Nick/Desktop/image.jpg"));
	}

}
