package banger.gui;

import banger.audio.data.Song;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;

public class Queue extends TableView<Song> {

    private MainView mainview;
    private ObservableList<Song> songs;
    private DataFormat songFormat = new DataFormat("Song");

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

        setOnDragDetected(new EventHandler<MouseEvent>() { //drag
            @Override
            public void handle(MouseEvent event) {
                // drag was detected, start drag-and-drop gesture
                Song selected = getSelectionModel().getSelectedItem();
                if(selected !=null){
                    Dragboard db = startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.put(songFormat, selected);
                    db.setContent(content);
                    event.consume();
                }
            }
        });

        setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                // data is dragged over the target
                Dragboard db = event.getDragboard();
                if (event.getDragboard().hasString()){
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (event.getDragboard().hasString()) {
                    Song s = (Song) db.getContent(songFormat);
                    // getItems().add(s);
                    // setItems(tableContent);
                    System.out.println(s.getName());
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }
}

