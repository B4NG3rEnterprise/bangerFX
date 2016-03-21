package banger.gui;

import banger.audio.Song;
import banger.database.DBController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Library extends TableView<Song> implements EventHandler<Event> {

    private MainView mainview;
    private ObservableList<Song> songs;

    public Library(MainView mainview) {
        super();

        this.mainview = mainview;

        init();
    }

    public void fillTable(){
        songs = DBController.getAllFiles();
        setItems(songs);
    }

    public void init(){
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn song_id = new TableColumn("ID");
        song_id.setCellValueFactory(
                new PropertyValueFactory<Song, String>("id"));
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
                new PropertyValueFactory<Song, Integer>("genre"));
        TableColumn length = new TableColumn("Length");
        length.setCellValueFactory(
                new PropertyValueFactory<Song, Integer>("length"));

        fillTable();
        getColumns().addAll(song_id, song_name, artist_name, album_name, genre, length);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    mainview.play((getSelectionModel().getSelectedItem()));
                    updateQueue();
                }
            }
        });
    }

    public void updateQueue(){
        long seed = System.nanoTime();
        Song selected = getSelectionModel().getSelectedItem();
        if (selected == null) selected = getItems().get(0);
        ObservableList<Song> list = getAllFrom(selected);
        if (mainview.getStatusbar().isShuffling()) Collections.shuffle(list, new Random(seed));
        list.add(0, selected);
        mainview.setQueueItems(list);
    }

    public ObservableList<Song> getAllFrom(Song s){
        List<Song> list = new ArrayList<>();
        ObservableList<Song> result = FXCollections.observableList(list);
        for (int i = songs.indexOf(s) + 1; i < songs.size(); i++)
            list.add(songs.get(i));
        return result;
    }
}
