package banger.gui.sidebar;

import banger.gui.MainView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class ViewSelectorController{

	@FXML
	Label song;
	@FXML
	Label list;
	@FXML
	Label album;
	@FXML
	Label background;
	
	private MainView mv;
	private Label currentView = song;
	
	public ViewSelectorController(MainView mv) {
		this.mv = mv;
	}


	@FXML
	protected void showTitle() {
		clearLabels();
		song.setBackground(new Background(new BackgroundFill(Color.web("#FA7D38"), CornerRadii.EMPTY, Insets.EMPTY)));		
		mv.getLibrary().setView(Library.VIEW_TITLE);
	}

	@FXML
	protected void showTable() {
		clearLabels();
		list.setBackground(new Background(new BackgroundFill(Color.web("#FA7D38"), CornerRadii.EMPTY, Insets.EMPTY)));	
		mv.getLibrary().setView(Library.VIEW_TABLE);
	}

	@FXML
	protected void showAlbum() {
		clearLabels();
		album.setBackground(new Background(new BackgroundFill(Color.web("#FA7D38"), CornerRadii.EMPTY, Insets.EMPTY)));	
		mv.getLibrary().setView(Library.VIEW_ALBUM);
	}

	protected void clearLabels() {
		song.setBackground(null);
		list.setBackground(null);
		album.setBackground(null);
	}

}
