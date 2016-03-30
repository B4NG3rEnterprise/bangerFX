package banger.gui.sidebar.viewselector;

import banger.gui.MainView;
import banger.gui.library.Library;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ViewSelector extends VBox {

    @FXML
    Label song;
    @FXML
    Label list;
    @FXML
    Label album;
    @FXML
    Label lyrics;

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

        list.setBackground(new Background(new BackgroundFill(Color.web("#FA7D38"), CornerRadii.EMPTY, Insets.EMPTY)));
    }


    @FXML
    protected void showTitle() {
        resizeContainer();
        clearLabels();
        song.setBackground(new Background(new BackgroundFill(Color.web("#FA7D38"), CornerRadii.EMPTY, Insets.EMPTY)));
        mv.getLibrary().setView(Library.VIEW_TITLE);
    }

    @FXML
    protected void showList() {
        resizeContainer();
        clearLabels();
        list.setBackground(new Background(new BackgroundFill(Color.web("#FA7D38"), CornerRadii.EMPTY, Insets.EMPTY)));
        mv.getLibrary().setView(Library.VIEW_LIST);
    }

    @FXML
    protected void showAlbum() {
        resizeContainer();
        clearLabels();
        album.setBackground(new Background(new BackgroundFill(Color.web("#FA7D38"), CornerRadii.EMPTY, Insets.EMPTY)));
        mv.getLibrary().setView(Library.VIEW_ALBUM);
    }

    @FXML
    protected  void showLyrics() {
        resizeContainer();
        clearLabels();
        lyrics.setBackground(new Background(new BackgroundFill(Color.web("#FA7D38"), CornerRadii.EMPTY, Insets.EMPTY)));
        mv.getLibrary().setView(Library.VIEW_LYRICS);
    }

    private void clearLabels() {
        song.setBackground(null);
        list.setBackground(null);
        album.setBackground(null);
        lyrics.setBackground(null);
    }

    public void resizeContainer() {
        song.setMinWidth(this.getWidth());
        album.setMinWidth(this.getWidth());
        list.setMinWidth(this.getWidth());
        lyrics.setMinWidth(this.getWidth());
    }

}
