package banger.gui.library.views;

import banger.audio.data.Song;
import banger.database.DBController;
import banger.gui.MainView;
import banger.util.PlaylistManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;

import java.util.Optional;

public class ListView extends TableView<Song> implements View {

    private MainView mainview;
    private ObservableList<Song> songs;
    private Menu playlistMenu;
    private ContextMenu cm;

    public ListView(MainView m) {
        mainview = m;

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

                //update queue in musicplayer
                mainview.getMusicPlayer().updateQueue(FXCollections.observableArrayList(songs));
            }
        });

        cm = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            DBController.deleteSongs(getSelectedItems());
            refreshData();
        });
        playlistMenu = initPlaylistMenu();
        cm.getItems().addAll(delete, playlistMenu);

        addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        if (e.getButton() == MouseButton.SECONDARY)
                            cm.show(mainview.stage, e.getScreenX(), e.getScreenY());
                            playlistMenu = initPlaylistMenu();
                            cm.getItems().remove(1); // remove menu
                            cm.getItems().add(1, playlistMenu); // add menu again
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

    public void refreshData() {
        this.songs = DBController.getAllSongs();
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

    public Menu initPlaylistMenu(){
        playlistMenu = new Menu("Hinzufügen zu...");

        // just examples, loop through playlists and add them to the menu
        String[] playlists = PlaylistManager.getPlaylists();
        if (playlists != null) {
            for (int i = 0; i < playlists.length; i++) {
                MenuItem item = new MenuItem(playlists[i]);
                item.setOnAction(event -> {
                    // TODO add songs to playlist
                    Song[] songs = getSelectedItems();
                    PlaylistManager.addToPlaylist(item.getText(), songs);
                });
                playlistMenu.getItems().add(item);
                // System.out.println(item.toString());
            }
        }
        MenuItem newpl = new MenuItem("Neue Playlist erstellen...");
        newpl.setOnAction(event -> {
            // TODO add songs so playlist
            Song[] songs = getSelectedItems();

            if (songs != null) { // show name input and create new playlist
                String name = "New Playlist";
                TextInputDialog dialog = new TextInputDialog(name);
                dialog.setHeaderText("");
                dialog.setGraphic(null);
                dialog.setTitle("Playlist Name");
                dialog.setContentText("Enter a playlist name:");
                dialog.initStyle(StageStyle.UNDECORATED);
                dialog.getDialogPane().getStylesheets().add("banger/gui/menubar/dialog.css");
                dialog.getEditor().getStyleClass().add("editor");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    name = result.get();
                }

                PlaylistManager.createPlaylist(name, songs);
            } else { // show alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Keine Dateien ausgewählt");
                alert.setContentText("Bitte wählen Sie zuerst Dateien aus!");
                alert.setHeaderText("");
                alert.setGraphic(null);
                alert.initStyle(StageStyle.UNDECORATED);
                alert.getDialogPane().getStylesheets().add("banger/gui/menubar/dialog.css");
                alert.show();
            }

            // update PlaylistSelector
            mainview.getPlaylistSelector().updatePlaylists();
        });
        playlistMenu.getItems().add(newpl);

        return playlistMenu;
    }
}
