package banger.gui.menubar;

import banger.audio.data.Song;
import banger.database.DBController;
import banger.gui.MainView;
import banger.util.DeviceItem;
import banger.util.Option;
import banger.util.PlaylistManager;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Optional;

public class BangerBar extends MenuBar {

    MainView mainview;
    Menu file, edit, view, help, extra, playlistMenu;

    public BangerBar(MainView mainview){
        super();

        this.mainview = mainview;
        init();
    }

    public void init() {
        getStylesheets().add("banger/gui/menubar/bangerbar.css");
        MenuItem changeDir = new MenuItem("Choose Directory...");
        changeDir.setOnAction(e -> {
            Stage stage = new Stage();
            stage.centerOnScreen();
            DirectoryChooser dc = new DirectoryChooser();
            // dc.setInitialDirectory(new File("F:/Musik"));
            File directory = dc.showDialog(stage);
            DBController.createFromDirectory(directory.toString());
            mainview.getLibrary().refreshData();
        });

        file = new Menu("Datei");
        MenuItem deleteSelected = new MenuItem("Songs löschen...");
        deleteSelected.setOnAction(event -> {
            Song[] songs = mainview.getLibrary().getSelectedItems();
            if (songs != null) {
                DBController.deleteSongs(mainview.getLibrary().getSelectedItems());
                mainview.getLibrary().refreshData();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Keine Dateien ausgewählt");
                alert.setContentText("Bitte wählen Sie zuerst Dateien aus!");
                alert.setHeaderText("");
                alert.setGraphic(null);
                alert.initStyle(StageStyle.UNDECORATED);
                alert.getDialogPane().getStylesheets().add("banger/gui/menubar/dialog.css");
                alert.show();
            }
        });
        file.getItems().add(deleteSelected);
        MenuItem imp = new MenuItem("Importieren...");
        imp.setOnAction(event -> {
            Stage stage = new Stage();
            stage.centerOnScreen();
            DirectoryChooser dc = new DirectoryChooser();
            // dc.setInitialDirectory(new File("F:/Musik"));
            File directory = dc.showDialog(stage);
            DBController.addFromDirectory(directory.toString());
            mainview.getLibrary().refreshData();
        });
        file.getItems().add(imp);
        file.getItems().add(new MenuItem("Exportieren..."));
        MenuItem close = new MenuItem("Schließen");
        close.setOnAction(event -> {
            mainview.getMusicPlayer().kill();
            System.exit(0);
        });
        file.getItems().add(close);

        edit = new Menu("Bearbeiten");
        playlistMenu = initPlaylistMenu();
        edit.getItems().add(playlistMenu);
        edit.getItems().add(new MenuItem("Playlist löschen"));
        edit.getItems().add(new MenuItem("Optionen..."));
        edit.getItems().add(new MenuItem("Equalizer"));

        view = new Menu("Ansicht");
        view.getItems().add(new MenuItem("Widget-Ansicht"));
        CheckMenuItem notifications = new CheckMenuItem("Benachrichtigungen");
        notifications.setSelected(Option.notifications);
        notifications.setOnAction(event -> {
            Option.notifications = !Option.notifications;
        });
        view.getItems().add(notifications);
        view.getItems().add(new MenuItem("Hintergrundfarbe"));
        view.getItems().add(new MenuItem("Fonts"));

        help = new Menu("Hilfe");
        help.getItems().add(new MenuItem("Hilfe..."));
        help.getItems().add(new MenuItem("Über"));
        help.getItems().add(new MenuItem("Get Premium"));

        extra = new Menu("Extra");
        extra.getItems().add(initDeviceMenu());
        extra.getItems().add(changeDir);

        getMenus().addAll(file, edit, view, help, extra);
    }

    private Menu initPlaylistMenu(){
        Menu playlistMenu = new Menu("Hinzufügen zu...");

        // just examples, loop through playlists and add them to the menu
        String[] playlists = PlaylistManager.getPlaylists();
        if (playlists != null) {
            for (int i = 0; i < playlists.length; i++) {
                MenuItem item = new MenuItem(playlists[i]);
                item.setOnAction(event -> {
                    // TODO add songs to playlist
                    Song[] songs = mainview.getLibrary().getSelectedItems();
                    PlaylistManager.addToPlaylist(item.getText(), songs);
                });
                playlistMenu.getItems().add(item);
                // System.out.println(item.toString());
            }
        }
        MenuItem newpl = new MenuItem("Neue Playlist erstellen...");
        newpl.setOnAction(event -> {
            // TODO add songs so playlist
            Song[] songs = mainview.getLibrary().getSelectedItems();

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

            // add MenuItem to playlistMenu
            updatePlaylistMenu();
        });
        playlistMenu.getItems().add(newpl);

        return playlistMenu;
    }

    public void updatePlaylistMenu(){
        playlistMenu = initPlaylistMenu();
        edit.getItems().remove(0); // remove menu
        edit.getItems().add(0, playlistMenu); // add menu again
    }

    private Menu initDeviceMenu(){
        Menu deviceMenu = new Menu("Devices");
        ObservableList<DeviceItem> deviceList = mainview.getMusicPlayer().getDevices();
        for (int i = 0; i < deviceList.size(); i++) {
            int deviceNum = deviceList.get(i).getDeviceInt();
            MenuItem m = new MenuItem(deviceList.get(i).toString());
            m.setOnAction(e -> mainview.getMusicPlayer().setOutputDevice(deviceNum));
            deviceMenu.getItems().add(m);
        }
        return deviceMenu;
    }

    private Menu initSettingsMenu(){
        Menu settingsMenu = new Menu("Settings");

        MenuItem mi = new MenuItem("Fun");

        settingsMenu.getItems().add(mi);
        return settingsMenu;
    }
}
