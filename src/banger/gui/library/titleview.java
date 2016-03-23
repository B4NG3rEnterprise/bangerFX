package banger.gui.library;

import banger.audio.Song;
import banger.gui.MainView;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TitleView extends TableView<Song> implements View {

    private MainView mainview;
    private ObservableList<Song> songs;

    public TitleView(MainView m, ObservableList<Song> s) {
        mainview = m;
        songs = s;

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getStylesheets().add("banger/gui/library/tableview.css");

        TableColumn song_name = new TableColumn("Name");
        song_name.setCellValueFactory(
                new PropertyValueFactory<Song, String>("name"));
        TableColumn artist_name = new TableColumn("Artist");
        artist_name.setCellValueFactory(
                new PropertyValueFactory<Song, String>("artist"));
        TableColumn album_name = new TableColumn("Album");
        album_name.setCellValueFactory(
                new PropertyValueFactory<Song, String>("album"));
        TableColumn genre = new TableColumn("Genre");
        genre.setCellValueFactory(
                new PropertyValueFactory<Song, String>("genre"));
        TableColumn length = new TableColumn("Length");
        length.setCellValueFactory(
                new PropertyValueFactory<Song, Integer>("length"));

        setItems(songs);
        getColumns().addAll(song_name, artist_name, album_name, genre, length);

        setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                mainview.play(getSelectionModel().getSelectedItem());
                mainview.getLibrary().updateQueue(getSelectionModel().getSelectedItem());
            }
        });
    }

    public void select(Song song) {
        getSelectionModel().clearSelection();
        getSelectionModel().select(song);
    }

    public Song getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }

    public void refreshData(ObservableList<Song> songs) {
        this.songs = songs;
        setItems(songs);
        updateBounds();
    }
}
