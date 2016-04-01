package banger.gui.library.views;

import banger.audio.data.Song;
import banger.gui.MainView;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ListView extends TableView<Song> implements View {

    private MainView mainview;
    private ObservableList<Song> songs;

    public ListView(MainView m, ObservableList<Song> s) {
        mainview = m;
        songs = s;

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getStylesheets().add("banger/gui/library/views/listview.css");

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
                mainview.getMusicPlayer().play(getSelectionModel().getSelectedItem());
                mainview.getLibrary().updateQueue();
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

    public Song[] getSelectedItems(){
        ObservableList<Song> songs = getSelectionModel().getSelectedItems();
        Song[] result = new Song[songs.size()];
        for (int i = 0; i < songs.size(); i++)
            result[i] = songs.get(i);
        if (result.length == 0) result = null;
        return result;
    }
}
