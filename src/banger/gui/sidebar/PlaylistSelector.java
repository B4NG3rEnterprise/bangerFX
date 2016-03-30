package banger.gui.sidebar;

import banger.gui.MainView;
import banger.util.PlaylistManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.StageStyle;

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
               System.out.println("Playlist selected: " + getSelectionModel().getSelectedItem());
            }
        });

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE && getItems().size() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Playlist löschen?");
                alert.setContentText("Wollen Sie die Playlist wirklich löschen?");
                alert.setHeaderText("");
                alert.setGraphic(null);
                alert.initStyle(StageStyle.UNDECORATED);
                alert.getDialogPane().getStylesheets().add("banger/gui/menubar/dialog.css");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        PlaylistManager.deletePlaylist(getSelectionModel().getSelectedItem());
                        updatePlaylists();
                        getSelectionModel().selectFirst();
                        mainview.getBangerBar().updatePlaylistMenu();
                    }
                });
            }
        });
    }

    public void updatePlaylists(){
        String[] playlists = PlaylistManager.getPlaylists();
        ObservableList<String> data = FXCollections.observableArrayList();

        if (playlists != null) {
            for (int i = 0; i < playlists.length; i++) {
                data.add(playlists[i]);
            }
            setItems(data);
        }
    }

}
