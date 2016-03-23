package banger.gui.coverview;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CoverViewController{
	
	@FXML Label title;
	@FXML Label artist;
	@FXML Label album;	
	@FXML ImageView cover;
	
	
	public void update(String ntitle, String nartist, String nalbum, String coverLocation) {

		title.setText(ntitle);
		artist.setText(nartist);
		album.setText(nalbum);
		
		cover.setImage(new Image("file:/D:/Data/Programming/bangerFX/res/png/Cover0.jpg"));
	}

}
