package banger.gui.sidebar;

import banger.audio.data.Song;
import banger.database.DBController;
import banger.gui.MainView;
import banger.util.PlaylistManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PlaylistSelector extends ListView<String> {

    private MainView mainview;

    public PlaylistSelector(MainView mainview){
        super();

        this.mainview = mainview;

        init();
    }

    private void init(){
        updatePlaylists();
        getStylesheets().add("banger/gui/sidebar/list.css");

        setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                // System.out.println("Playlist selected: " + getSelectionModel().getSelectedItem());
                ObservableList<Song> q = DBController.getSongsFromPlaylist(getSelectionModel().getSelectedItem());
                mainview.getMusicPlayer().play(q.get(0));
                mainview.getMusicPlayer().updateQueue(q);
            }
        });

        ContextMenu cm = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            String selected = getSelectionModel().getSelectedItem();
            PlaylistManager.deletePlaylist(selected);
            updatePlaylists();
        });
        cm.getItems().add(delete);

        addEventHandler(MouseEvent.MOUSE_CLICKED,
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    if (e.getButton() == MouseButton.SECONDARY)
                        cm.show(mainview.stage, e.getScreenX(), e.getScreenY());
                }
            });

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE && getItems().size() > 0) {
                PlaylistManager.deletePlaylist(getSelectionModel().getSelectedItem());
                updatePlaylists();
            }
        });
    }

    public void updatePlaylists(){
        String[] playlists = PlaylistManager.getPlaylists();
        ObservableList<String> data = FXCollections.observableArrayList();

        mainview.getBangerBar().updatePlaylistMenu();
        mainview.getLibrary().refreshData();

        if (playlists != null) {
            for (int i = 0; i < playlists.length; i++) {
                data.add(playlists[i]);
            }
            setItems(data);
        }
    }

}
