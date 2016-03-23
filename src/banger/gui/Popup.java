package banger.gui;

import javafx.application.Platform;
import javafx.scene.text.TextFlow;

public class Popup extends javafx.stage.Popup{

    MainView mainview;

    public Popup(TextFlow message, MainView m) {
        super();
        mainview = m;
        String color = "#FA7D38";  // #FA7D38
        TextFlow label = message;
        if (mainview.isDark(color)) label.getStylesheets().add("banger/gui/darkpopup.css");
        else label.getStylesheets().add("banger/gui/popup.css");
        label.getStyleClass().add("popup");
        label.setStyle("-fx-background-color: " + color);
        label.setMinWidth(200);
        getContent().add(label);
        setOnShowing(e -> {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
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
                Platform.runLater(() -> {
                    hide();
                });
            }).start();
        });
    }
}
