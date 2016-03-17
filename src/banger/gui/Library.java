package banger.gui;

import banger.audio.Song;
import banger.database.DBController;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class Library extends TableView implements EventHandler<Event> {

    public Library() {
        super();
        init();
    }

    public void init(){
        ObservableList<Song> show = DBController.shuffleAll();

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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

        this.setItems(show);
        this.getColumns().addAll(song_id, song_name, artist_name, album_name);
    }

    public void handle(Event event){
        EventType type = event.getEventType();

        if(type.equals(MouseEvent.MOUSE_CLICKED)) {
            handle((MouseEvent) event);
        } else {

            System.out.println(type);
            System.out.println(type.getClass());
            System.out.println(type.getName());
        }
    }
}
