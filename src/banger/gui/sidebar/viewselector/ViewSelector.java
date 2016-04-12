package banger.gui.sidebar.viewselector;

import banger.gui.MainView;
import banger.gui.library.Library;
import banger.gui.options.Options;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ViewSelector extends VBox {

    private Label current;

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
        current = list;
        updateColor();
    }


    @FXML
    protected void showTitle() {
        current = song;
        resizeContainer();
        updateColor();
        mv.getLibrary().setView(Library.VIEW_TITLE);
    }

    @FXML
    protected void showList() {
        current = list;
        resizeContainer();
        updateColor();
        mv.getLibrary().setView(Library.VIEW_LIST);
    }

    @FXML
    protected void showAlbum() {
        current = album;
        resizeContainer();
        updateColor();
        mv.getLibrary().setView(Library.VIEW_ALBUM);
    }

    @FXML
    protected  void showLyrics() {
        current = lyrics;
        resizeContainer();
        updateColor();
        mv.getLibrary().setView(Library.VIEW_LYRICS);
    }

    public void updateColor() {
        song.setBackground(null);
        list.setBackground(null);
        album.setBackground(null);
        lyrics.setBackground(null);
        current.setBackground(new Background(new BackgroundFill(Color.valueOf(Options.backgroundColor), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void resizeContainer() {
        song.setMinWidth(this.getWidth());
        album.setMinWidth(this.getWidth());
        list.setMinWidth(this.getWidth());
        lyrics.setMinWidth(this.getWidth());
    }


}
