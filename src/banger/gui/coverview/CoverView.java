package banger.gui.coverview;

import banger.gui.MainView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;

public class CoverView extends HBox {

    @FXML
    Label title;
    @FXML
    Label artist;
    @FXML
    Label album;
    @FXML
    ImageView cover;

    private MainView mainView;

    public CoverView(MainView mainView) {
        this.mainView = mainView;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("coverView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

       cover.setImage(new Image("file:/"+new File("res/png/Cover0.jpg").getAbsolutePath()));

    }

    public void updateView(String ntitle, String nartist, String nalbum, String coverLocation) {

        title.setText(ntitle);
        artist.setText(nartist);
        album.setText(nalbum);

        cover.setImage(new Image("file:/C:/users/nick/desktop/cover0.jpg"));
    }

}
