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

public class Queue extends TableView<Song> implements EventHandler<Event> {

    private MainView mainview;

    public Queue(MainView mainview) {
        super();

        this.mainview = mainview;

        init();
    }

    public void fillTable(){
        ObservableList<Song> show = DBController.shuffleAllFiles();
        setItems(show);
    }

    public void init(){
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setMaxWidth(213);

        TableColumn song_name = new TableColumn("Queue");
        song_name.setCellValueFactory(
                new PropertyValueFactory<Song, String>("name"));
        TableColumn artist_name = new TableColumn("");
        artist_name.setCellValueFactory(
                new PropertyValueFactory<Song, String>("artist"));

        song_name.setMaxWidth(100);
        artist_name.setMaxWidth(100);

        fillTable();
        getColumns().addAll(song_name, artist_name);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    mainview.play((getSelectionModel().getSelectedItem()));
                }
            }
        });
    }

    public void handle(Event event){
        EventType type = event.getEventType();

        if(type.equals(MouseEvent.MOUSE_CLICKED)) {
            //handle((MouseEvent) event);
        } else {

            System.out.println(type);
            System.out.println(type.getClass());
            System.out.println(type.getName());
        }
    }
}

