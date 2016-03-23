package banger.gui.menubar;

import banger.database.DBController;
import banger.gui.MainView;
import banger.util.DeviceItem;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class BangerBar extends MenuBar {

    MainView mainview;

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
            DBController.setContent(directory.toString());
            mainview.getLibrary().refreshData();
        });

        Menu file = new Menu("Datei");
        file.getItems().add(new MenuItem("Importieren..."));
        file.getItems().add(new MenuItem("Exportieren..."));
        MenuItem close = new MenuItem("Schließen");
        close.setOnAction(event -> {
            mainview.getMusicPlayer().kill();
            System.exit(0);
        });
        file.getItems().add(close);

        Menu edit = new Menu("Bearbeiten");
        edit.getItems().add(initPlaylistMenu());
        edit.getItems().add(new MenuItem("Playlist löschen"));
        edit.getItems().add(new MenuItem("Optionen..."));
        edit.getItems().add(new MenuItem("Equalizer"));

        Menu view = new Menu("Ansicht");
        view.getItems().add(new MenuItem("Widget-Ansicht"));
        view.getItems().add(new MenuItem("Benachrichtigungen"));
        view.getItems().add(new MenuItem("Hintergrundfarbe"));
        view.getItems().add(new MenuItem("Fonts"));

        Menu help = new Menu("Hilfe");
        help.getItems().add(new MenuItem("Hilfe..."));
        help.getItems().add(new MenuItem("Über"));
        help.getItems().add(new MenuItem("Get Premium"));

        Menu extra = new Menu("Extra");
        extra.getItems().add(initDeviceMenu());
        extra.getItems().add(changeDir);
        MenuItem lyrics = new MenuItem("Get Lyrics");
        lyrics.setOnAction(event -> {
            mainview.showLyrics();
        });
        extra.getItems().add(lyrics);

        getMenus().addAll(file, edit, view, help, extra);
    }

    private Menu initPlaylistMenu(){
        Menu playlistMenu = new Menu("Hinzufügen zu...");

        // just examples, loop through playlists and add them to the menu
        playlistMenu.getItems().add(new MenuItem("Sport"));
        playlistMenu.getItems().add(new MenuItem("Chillen"));

        playlistMenu.getItems().add(new MenuItem("Neue Playlist erstellen..."));

        return playlistMenu;
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
