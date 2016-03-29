package banger.gui.sidebar.viewselector;

import banger.gui.MainView;
import banger.gui.library.Library;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ViewSelector extends AnchorPane {

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

    public ViewSelector(MainView mv) {
        this.mv = mv;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("viewSelector.fxml"));
        loader.setRoot(this);
        loader.setController(this);


        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
