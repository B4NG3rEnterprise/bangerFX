package banger.gui.menubar;

import banger.gui.MainView;
import banger.util.DeviceItem;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class BangerBar extends MenuBar {

    MainView mainview;

    public BangerBar(MainView mainview){
        super();

        this.mainview = mainview;
        init();
    }

    public void init() {
        getMenus().add(initDeviceMenu());
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
}
