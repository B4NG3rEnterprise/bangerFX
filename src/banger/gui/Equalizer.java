package banger.gui;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Equalizer extends HBox {

    private MainView mainview;

    private Slider band0;
    private Slider band1;
    private Slider band2;
    private Slider band3;
    private Slider band4;

    private CheckBox box;
    private Button reset;

    public Equalizer(MainView mainview) {
        super(20);

        this.mainview = mainview;

        init();
    }

    public void init() {
        box = new CheckBox("Enable Equalizer");
        reset = new Button("Reset");
        reset.setOnMouseClicked(e -> {
            band0.setValue(0);
            band1.setValue(0);
            band2.setValue(0);
            band3.setValue(0);
            band4.setValue(0);
        });

        band0 = new Slider(-15, 15, 0);
        band0.setOrientation(Orientation.VERTICAL);
        band0.setShowTickMarks(true);
        band0.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (box.isSelected()) mainview.getMusicPlayer().updateEQ(0, newValue.floatValue());
        });

        band1 = new Slider(-15, 15, 0);
        band1.setOrientation(Orientation.VERTICAL);
        band1.setShowTickMarks(true);
        band1.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (box.isSelected()) mainview.getMusicPlayer().updateEQ(1, newValue.floatValue());
        });

        band2 = new Slider(-15, 15, 0);
        band2.setOrientation(Orientation.VERTICAL);
        band2.setShowTickMarks(true);
        band2.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (box.isSelected()) mainview.getMusicPlayer().updateEQ(2, newValue.floatValue());
        });

        band3 = new Slider(-15, 15, 0);
        band3.setOrientation(Orientation.VERTICAL);
        band3.setShowTickMarks(true);
        band3.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (box.isSelected()) mainview.getMusicPlayer().updateEQ(3, newValue.floatValue());
        });

        band4 = new Slider(-15, 15, 0);
        band4.setOrientation(Orientation.VERTICAL);
        band4.setShowTickMarks(true);
        band4.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (box.isSelected()) mainview.getMusicPlayer().updateEQ(4, newValue.floatValue());
        });

        box.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mainview.getMusicPlayer().updateEQ(0, (float) band0.getValue());
                mainview.getMusicPlayer().updateEQ(1, (float) band1.getValue());
                mainview.getMusicPlayer().updateEQ(2, (float) band2.getValue());
                mainview.getMusicPlayer().updateEQ(3, (float) band3.getValue());
                mainview.getMusicPlayer().updateEQ(4, (float) band4.getValue());
            } else {
                mainview.getMusicPlayer().updateEQ(0, 0);
                mainview.getMusicPlayer().updateEQ(1, 0);
                mainview.getMusicPlayer().updateEQ(2, 0);
                mainview.getMusicPlayer().updateEQ(3, 0);
                mainview.getMusicPlayer().updateEQ(4, 0);
            }
        });

        getChildren().addAll(new VBox(15, box, reset), band0, band1, band2, band3, band4);
    }



}
