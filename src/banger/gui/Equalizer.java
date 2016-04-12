package banger.gui;

import banger.util.Utility;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Equalizer extends HBox {

    private MainView mainview;

    private Slider[] bands;
    private Label[] freq;

    private CheckBox box;
    private Button reset;

    public Equalizer(MainView mainview) {
        super(20);

        this.mainview = mainview;

        init();
    }

    public void init() {
        bands = new Slider[10];
        freq = new Label[10];

        box = new CheckBox("Enable Equalizer");
        reset = new Button("Reset");
        reset.setOnMouseClicked(e -> {
            for (Slider band : bands)
                band.setValue(0);
        });

        for (int i = 0; i < bands.length; i++) {
            bands[i] = new Slider(-15, 15, 0);
            bands[i].setOrientation(Orientation.VERTICAL);
            bands[i].setShowTickMarks(true);
            bands[i].valueProperty().bindBidirectional(mainview.getMusicPlayer().getEQBandGain(i));
        }

        float[] eqFreq = mainview.getMusicPlayer().getEQFrequencies();
        for (int i = 0; i < freq.length; i++) {
            freq[i] = new Label(Utility.withSuffix((long) eqFreq[i]));
        }

        box.selectedProperty().bindBidirectional(mainview.getMusicPlayer().isEQActive());

        getChildren().add(new VBox(15, box, reset));
        getChildren().addAll(new VBox(new HBox(15, bands), new HBox(15, freq)));
    }



}
