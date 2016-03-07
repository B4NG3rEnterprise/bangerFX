import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jouvieje.bass.Bass;
import util.DeviceItem;

public class PlayerInterface extends Application {

    Stage stage;
    Scene scene;
    MusicPlayer p;
    MenuBar menuBar = new MenuBar();
    int clicked = 0;

    public PlayerInterface() {}

    public void start(Stage primaryStage) {
        p = new MusicPlayer();
        this.stage = primaryStage;

        Button play = new Button("Play | Pause");

        Slider s = new Slider(0, 100, p.getVolume()*100);
        s.setMaxWidth(100);
        s.setShowTickMarks(false);

        s.setOnMouseClicked(e -> {
            p.setVolume((float)s.getValue()/100);
        });

        s.setOnMouseDragged(e -> {
            p.setVolume((float)s.getValue()/100);
        });

        s.setOnScroll(e -> {
            double d = e.getDeltaY();
            if(d > 0) {
                s.setValue(s.getValue() + 2);
                p.setVolume((float)s.getValue()/100);
            } else {
                s.setValue(s.getValue() - 2);
                p.setVolume((float)s.getValue()/100);
            }
        });

        Slider t = new Slider(0, p.getLength(), 0);
        t.setMinWidth(800);
        t.setMajorTickUnit(p.getLength());
        t.setShowTickLabels(true);

        Thread time = new Thread(() -> {
            System.out.println("Alive.");
            double pos;
            while((pos = p.getPosition()) <= p.getLength()) {
                long now = System.currentTimeMillis();
                while (System.currentTimeMillis() - now < 500) {
                    Thread.yield();
                }
                if(p.isPlaying())
                    t.setValue(p.getPosition());
            }
        });

        t.setOnMousePressed(e -> {
            p.pause();
            p.setPosition(t.getValue());
        });

        t.setOnMouseReleased(e -> {
            p.play();
            p.setPosition(t.getValue());
            if (!time.isAlive()) time.start();
        });

        t.setOnMouseDragged(e -> {

        });

        play.setOnMousePressed(e -> {
            if (p.isPlaying()) {
                p.pause();
            } else {
                p.play();
                if (!time.isAlive()) time.start();
            }
        });

        menuBar.getMenus().add(initDeviceMenu());
        scene = new Scene(new HBox(20, play, s, t, menuBar));

        stage.setScene(scene);
        stage.setTitle("MusicPlayer");
        stage.setMinWidth(1400);
        stage.setOnCloseRequest(e -> {
            p.stop();
            System.exit(0);
        });
        stage.show();
    }

    private Menu initDeviceMenu(){
        Menu deviceMenu = new Menu("Devices");
        ObservableList<DeviceItem> deviceList = p.getDevices();
        for (int i = 0; i < deviceList.size(); i++) {
            int deviceNum = deviceList.get(i).getDeviceInt();
            MenuItem m = new MenuItem(deviceList.get(i).toString());
            m.setOnAction(e -> p.setOutputDevice(deviceNum));
            deviceMenu.getItems().add(m);
        }
        return deviceMenu;
    }
}
