package banger.gui.options;

import banger.util.Utility;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * Created by Nick on 31.03.2016.
 */
public class KeySelectionPopup extends Pane {

    public KeySelectionPopup() {
        super();
        String color = Options.backgroundColor;
        TextFlow label = new TextFlow(new Text("Dr"+"\u00FC"+"cken sie die gew"+"\u00FC"+"nschte Tastenkombination.\nESC zum Abbrechen"));
        if (Utility.isDark(Color.valueOf(color))) label.getStylesheets().add("banger/gui/popup/darkpopup.css");
        else label.getStylesheets().add("banger/gui/popup/popup.css");

        label.setTextAlignment(TextAlignment.CENTER);
        label.getStyleClass().add("popup");
        label.setStyle("-fx-background-color: " + color);
        label.setMinWidth(200);
        getChildren().add(label);

    }
}
