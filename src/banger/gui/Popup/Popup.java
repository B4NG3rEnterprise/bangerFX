package banger.gui.popup;

import banger.gui.options.Options;
import banger.util.Utility;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Popup extends javafx.stage.Popup {

    public Popup(Stage owner, TextFlow message) {
        super();
        Color color = Color.valueOf(Options.backgroundColor);
        TextFlow label = message;
        if (Utility.isDark(color)) label.getStylesheets().add("banger/gui/popup/darkpopup.css");
        else label.getStylesheets().add("banger/gui/popup/popup.css");

        label.getStyleClass().add("popup");
        label.setStyle("-fx-background-color: rgb(" + color.getRed()*255 + "," + color.getGreen()*255 + "," + color.getBlue()*255 + ");");
        label.setMinWidth(200);
        getContent().add(label);

        setOnShowing(e -> {
            new Thread(() -> {
                //Slowly increasy opacity to make it visible
                for(int i = 0; i <= 17; i++) {
                    double opacity = (double) i / 20;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        setOpacity(opacity);
                    });
                }

                //Show it for 2 seconds
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                //Decrease opacity to hide the popup
                for(int i = 17; i >= 0; i--) {
                    double opacity = (double) i / 20;
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        setOpacity(opacity);
                    });
                }
                Platform.runLater(() -> hide());
            }).start();
        });

        setOnShown(e -> {
            //Add the popup to the monitor with the application on it.
            Rectangle2D primaryScreenBounds;
            if(!owner.isIconified()) {
                ObservableList<Screen> screens = Screen.getScreensForRectangle(owner.getX(), owner.getY(), owner.getWidth(), owner.getHeight());
                primaryScreenBounds = screens.get(0).getVisualBounds();
            } else {
                primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            }
            setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - getWidth() - 5);
            setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - getHeight() - 5);
        });

        show(owner);
    }
}
