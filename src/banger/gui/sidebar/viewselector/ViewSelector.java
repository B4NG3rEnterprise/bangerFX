package banger.gui.sidebar.viewselector;

import banger.gui.sidebar.viewselector.ViewSelectorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class ViewSelector {
	
	private Pane p;
	
	private ViewSelectorController controller;

	public ViewSelector() {		
		FXMLLoader loader = new FXMLLoader();	
		try {
			p = loader.load(this.getClass().getResource("viewSelector.fxml"));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		controller = (ViewSelectorController) loader.getController();	
	}
	
	public Pane getPane() {
		return this.p;
	}
	
}