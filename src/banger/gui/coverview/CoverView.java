package banger.gui.coverview;

import banger.gui.MainView;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class CoverView {

	private Pane p;
	
	private MainView mainview;
	
	private CoverViewController controller;
	
	public CoverView(/*MainView mv*/) {
//		this.mainview = mv;
		
		FXMLLoader fxmlLoader = new FXMLLoader();	
		try {
		p = fxmlLoader.load(getClass().getResource("coverView.fxml").openStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		controller = (CoverViewController) fxmlLoader.getController();
		
	}
	
	public Pane getPane() {
		return p;
	}
	
	public void updateView(String title, String artist, String album, String coverLocation) {
		controller.update(title, artist, album, coverLocation);
	}
}
