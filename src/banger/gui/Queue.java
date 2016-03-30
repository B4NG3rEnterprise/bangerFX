package banger.gui;

import banger.audio.data.Song;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class Queue extends TableView<Song> {

    private MainView mainview;
    private ObservableList<Song> songs;

    public Queue(MainView mainview) {
        super();

        this.mainview = mainview;

        init();
    }

    public void init(){
        getStylesheets().add("banger/gui/library/views/listview.css");
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setPrefWidth(200);
        setMaxWidth(400);

        TableColumn song_name = new TableColumn("Queue");
        song_name.setCellValueFactory(
                new PropertyValueFactory<Song, String>("name"));
        TableColumn artist_name = new TableColumn("");
        artist_name.setCellValueFactory(
                new PropertyValueFactory<Song, String>("artist"));

        song_name.prefWidthProperty().bind(this.widthProperty().divide(2));
        artist_name.prefWidthProperty().bind(this.widthProperty().divide(2));

        getColumns().addAll(song_name, artist_name);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    Song current = getSelectionModel().getSelectedItem();
                    mainview.getMusicPlayer().play(current);
                    mainview.getLibrary().select(current);
                }
            }
        });
    }
}

