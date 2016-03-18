package banger.gui.menubar;

import banger.database.DBController;
import banger.gui.MainView;
import banger.util.DeviceItem;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;

public class BangerBar extends MenuBar {

    MainView mainview;

    public BangerBar(MainView mainview){
        super();

        this.mainview = mainview;
        init();
    }

    public void init() {
        MenuItem cd = new MenuItem("Choose Directory...");
        cd.setOnAction(e -> {
            Stage stage = new Stage();
            stage.centerOnScreen();
            DirectoryChooser dc = new DirectoryChooser();
            // dc.setInitialDirectory(new File("F:/Musik"));
            File directory = dc.showDialog(stage);
            DBController.setContent(directory.toString());
            mainview.getLibrary().fillTable();
        });

        Menu options = new Menu("Options");
        options.getItems().add(initDeviceMenu());
        options.getItems().add(cd);
        getMenus().add(options);
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

    private Menu initOptionsMenu(){
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
}
