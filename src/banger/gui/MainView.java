package banger.gui;

import banger.audio.MusicPlayer;
import banger.audio.Song;
import banger.gui.filebrowser.FileBrowser;
import banger.gui.menubar.BangerBar;
import banger.gui.statusbar.StatusBar;
import banger.util.BangerVars;
import banger.util.InputHandler;
import banger.util.LyricsGetter;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.awt.Color;
import java.util.Collections;
import java.util.Iterator;


public class MainView extends Application{

    MusicPlayer player;
    static Stage stage;

    StatusBar statusbar;
    Library library;
    BangerBar bangerBar;
    Queue queue;
    FileBrowser filebrowser;

    public void start(Stage stage) throws Exception {
        this.stage = stage;
        player = new MusicPlayer(this);

        statusbar = new StatusBar(this);
        statusbar.setCustomBackground(Paint.valueOf("#FA7D38"));
        statusbar.setMinSize(0, 40);

        library = new Library(this);
        library.setMinSize(0,0);

        bangerBar = new BangerBar(this);
        bangerBar.setMinSize(0,0);

        queue = new Queue(this);
        queue.setMinSize(0, 0);

        filebrowser = new FileBrowser(this);
        filebrowser.setMinSize(0, 0);

        BorderPane bl = new BorderPane();
        bl.setTop(bangerBar);
        bl.setCenter(library);
        bl.setRight(queue);
        bl.setBottom(statusbar);
        bl.setLeft(filebrowser);

        Scene scene = new Scene(bl);
		scene.getStylesheets().add("banger/gui/statusbar/statusbar.css");

        scene.addEventHandler(KeyEvent.ANY, new InputHandler(this));

		stage.setScene(scene);
        stage.setTitle("B4NG3rFX");
        stage.setMinWidth(1200);
        stage.setMaxWidth(1400);
        stage.setMinHeight(120);
        stage.setOnCloseRequest(e -> {
            player.kill();
            System.exit(0);
        });
        stage.show();

    }

    public MusicPlayer getMusicPlayer() {
        return player;
    }

    public Library getLibrary() {
        return library;
    }

    public Queue getQueue() { return queue; }

    public StatusBar getStatusbar() { return statusbar; }

    public FileBrowser getFilebrowser(){ return filebrowser; }

    public void play(Song s) {
        player.play(s);
        statusbar.play();
    }

    public void play() {
        player.play();
        statusbar.play();
    }

    public void pause() {
        player.pause();
    }

    public void skipForward() {
        for (Iterator<Song> iterator = queue.getItems().iterator(); iterator.hasNext(); ) {
            if (iterator.next().equals(player.getNowPlaying())) {
                if (iterator.hasNext()){
                    Song next = iterator.next();
                    library.getSelectionModel().clearSelection();
                    library.getSelectionModel().select(next);
                    queue.getSelectionModel().clearSelection();
                    queue.getSelectionModel().select(next);
                    play(next);
                } else if (getStatusbar().getRepeatType() == BangerVars.RepeatState.LOOP_QUEUE.ordinal()){
                    Song next = queue.getItems().get(0);
                    library.getSelectionModel().clearSelection();
                    library.getSelectionModel().select(next);
                    queue.getSelectionModel().clearSelection();
                    queue.getSelectionModel().select(next);
                    play(next);
                } break;
            }
        }
    }

    //TODO: Think of some better way!
    public void skipBackward() {
        ObservableList<Song> s = library.getItems();
        Collections.reverse(s); //Problem: Changes the underlying list (and with that even the order in the table)
        for (Iterator<Song> iterator = s.iterator(); iterator.hasNext(); ) {
            if (iterator.next().equals(player.getNowPlaying())) {
                if (iterator.hasNext()) {
                    Song next = iterator.next();
                    library.getSelectionModel().clearSelection();
                    library.getSelectionModel().select(next);
                    queue.getSelectionModel().clearSelection();
                    queue.getSelectionModel().select(next);
                    play(next);
                } break;
            }
        }
        Collections.reverse(s);
    }

    public void setQueueItems(ObservableList<Song> songs){
        queue.setItems(songs);
        queue.getSelectionModel().clearSelection();
        queue.getSelectionModel().select(songs.get(0));
    }

    private static boolean isDark(String color){
        String fontColor = color;
        boolean isDark = false;

        // remove hash character from string
        String rawFontColor = fontColor.substring(1,fontColor.length());

        // convert hex string to int
        int rgb = Integer.parseInt(rawFontColor, 16);
        Color c = new Color(rgb);
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float brightness = hsb[2];
        if (brightness < 0.5) isDark = true;

        return isDark;
    }

    private static Popup createPopup(final TextFlow message) {
        final Popup popup = new Popup();
        String color = "#FA7D38";  // #FA7D38
        TextFlow label = message;
        if (isDark(color)) label.getStylesheets().add("banger/gui/darkpopup.css");
        else label.getStylesheets().add("banger/gui/popup.css");
        label.getStyleClass().add("popup");
        label.setStyle("-fx-background-color: " + color);
        label.setMinWidth(200);
        popup.getContent().add(label);
        popup.setOnShowing(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for(int i = 17; i >= 0; i--) {
                        double opacity = (double) i / 20;
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            popup.setOpacity(opacity);
                        });
                    }
                    Platform.runLater(() -> {
                        popup.hide();
                    });
                }).start();
            }
        });
        return popup;
    }

    public static void showPopupMessage(final TextFlow message) {
        final Popup popup = createPopup(message);
        popup.setOnShown(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                popup.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - popup.getWidth() - 5);
                popup.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - popup.getHeight() - 5);
            }
        });
        popup.setOpacity(0);
        popup.show(stage);
        new Thread(() -> {
            for(int i = 0; i <= 17; i++) {
                double opacity = (double) i / 20;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    popup.setOpacity(opacity);
                });
            }
        }).start();
    }

    public void showLyrics(){
        new Thread(() -> {
            ScrollPane sp = new ScrollPane();
            Song current = getMusicPlayer().getNowPlaying();
            String color = "#FA7D38";  // #FA7D38
            String songtext = LyricsGetter.getLyrics(current.getArtist(), current.getName());

            sp.setContent(new Label(songtext));
            sp.setMaxHeight(500);

            Platform.runLater(() -> {
                Stage lyricStage = new Stage();
                lyricStage.setScene(new Scene(sp));
                lyricStage.show();
            });
        }).start();
    }
}
