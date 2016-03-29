package banger.gui;

import banger.audio.MusicPlayer;
import banger.audio.Song;
import banger.gui.coverview.CoverView;
import banger.gui.sidebar.PlaylistSelector;
import banger.gui.sidebar.filebrowser.FileBrowser;
import banger.gui.library.Library;
import banger.gui.library.LyricsView;
import banger.gui.menubar.BangerBar;
import banger.gui.sidebar.viewselector.ViewSelector;
import banger.gui.statusbar.StatusBar;
import banger.util.BangerVars;
import banger.util.InputHandler;
import banger.util.Option;
import com.sun.org.apache.xpath.internal.operations.Or;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.Color;
import java.util.Iterator;

public class MainView extends Application {

    private final boolean TEST_LYRICS = false;

    MusicPlayer player;
    static Stage stage;
    Scene scene;
    final int MIN_WIDTH = 1200;
    final int MIN_HEIGHT = MIN_WIDTH / 16 * 9;

    StatusBar statusbar;
    Library library;
    BangerBar bangerBar;
    SearchBar searchBar;
    Queue queue;
    CoverView coverview;
    FileBrowser filebrowser;
    InputHandler handler;
    LyricsView lyricsview;
    PlaylistSelector selector;
    ViewSelector viewSelector;

    public void start(Stage stage) throws Exception {
        stage.setTitle("B4NG3rFX");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMaxWidth(1800);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMaxHeight(1800 / 16 * 9);
        stage.setOnCloseRequest(e -> {
            player.kill();
            System.exit(0);
        });
        this.stage = stage;

        handler = new InputHandler(this);

        player = new MusicPlayer(this);

        statusbar = new StatusBar(this);
        statusbar.setCustomBackground(Paint.valueOf("#FA7D38"));
        statusbar.setMinSize(0, 40);

        library = new Library(this);
        library.setMinSize(0, 0);
        library.setPrefSize(600, 500);

        bangerBar = new BangerBar(this);
        bangerBar.setMinSize(0, 0);

        queue = new Queue(this);
        queue.setMinSize(0, 0);

        coverview = new CoverView(this);
        coverview.setMinSize(0, 0);
        coverview = new CoverView(this);

        searchBar = new SearchBar(this);

        //Sidebar rechts
        VBox v1 = new VBox();
        v1.setVgrow(queue, Priority.ALWAYS);
        v1.getChildren().addAll(searchBar, new Separator(Orientation.HORIZONTAL), queue, new Separator(Orientation.HORIZONTAL), coverview, new Separator(Orientation.HORIZONTAL));

        lyricsview = new LyricsView(this);
        lyricsview.setMinSize(0, 0);

        // Sidebar links
        viewSelector = new ViewSelector(this);
        filebrowser = new FileBrowser(this);
        filebrowser.setMinSize(0, 0);

        Label plLabel = new Label("Playlists");
        plLabel.setPadding(new Insets(5));

        selector = new PlaylistSelector(this);
        selector.setMinSize(0, 0);

        VBox v2 = new VBox();
        v2.getChildren().addAll(viewSelector, new Separator(Orientation.HORIZONTAL), filebrowser, new Separator(Orientation.HORIZONTAL), plLabel, selector);

        BorderPane bl = new BorderPane();

        if (TEST_LYRICS) {
            library.setMaxWidth(150);

            bl.setTop(bangerBar);
            bl.setCenter(lyricsview);
            bl.setRight(v1);
            bl.setBottom(statusbar);
            bl.setLeft(library);
        } else {
            bl.setTop(bangerBar);
            bl.setCenter(library);
            bl.setRight(v1);
            bl.setBottom(statusbar);
            bl.setLeft(v2);
        }

        scene = new Scene(bl);
        scene.addEventHandler(KeyEvent.ANY, handler);

        stage.setScene(scene);
        stage.show();
    }

    public MusicPlayer getMusicPlayer() {
        return player;
    }

    public Library getLibrary() {
        return library;
    }

    public Queue getQueue() {
        return queue;
    }

    public StatusBar getStatusbar() {
        return statusbar;
    }

    public FileBrowser getFilebrowser() {
        return filebrowser;
    }

    public InputHandler getInputHandler() {
        return handler;
    }

    public PlaylistSelector getPlaylistSelector() {
        return selector;
    }

    public BangerBar getBangerBar() {
        return bangerBar;
    }

    public void play(Song s) {
        player.play(s);
        statusbar.play();

        if (Option.notifications) {
            // popup
            TextFlow flow = new TextFlow();
            Text song = new Text(s.getName());
            Text artist = new Text(" - " + s.getArtist());
            song.setStyle("-fx-font-weight: bold; -fx-font-size: 120%;");
            Text album = new Text("\n" + s.getAlbum());
            album.setStyle("-fx-font-size: 90%;");
            flow.getChildren().addAll(song, artist, album);
            showPopupMessage(flow);
        }

        coverview.updateView(s.getName(), s.getArtist(), s.getAlbum(), null);
    }

    public void play() {
        player.play();
        statusbar.play();
    }

    public void pause() {
        player.pause();
        statusbar.pause();
    }

    public void skipForward() {
        for (Iterator<Song> iterator = queue.getItems().iterator(); iterator.hasNext(); ) {
            if (iterator.next().equals(player.getNowPlaying())) {
                if (iterator.hasNext()) {
                    Song next = iterator.next();
                    library.select(next);
                    queue.getSelectionModel().clearSelection();
                    queue.getSelectionModel().select(next);
                    play(next);
                } else if (getStatusbar().getRepeatType() == BangerVars.RepeatState.LOOP_QUEUE.ordinal()) {
                    Song next = queue.getItems().get(0);
                    library.select(next);
                    queue.getSelectionModel().clearSelection();
                    queue.getSelectionModel().select(next);
                    play(next);
                }
                break;
            }
        }
    }

    //TODO: Think of some better way!
    public void skipBackward() {
        ObservableList<Song> s = queue.getItems();
        for (int i = 0; i < s.size(); i++) {
            if (s.get(i).equals(player.getNowPlaying())) {
                if (i > 0) {
                    Song next = s.get(i - 1);
                    library.select(next);
                    queue.getSelectionModel().clearSelection();
                    queue.getSelectionModel().select(next);
                    play(next);
                } else {
                    getMusicPlayer().setPosition(0);
                }
                break;
            }
        }
    }

    public void setQueueItems(ObservableList<Song> songs) {
        queue.setItems(songs);
        queue.getSelectionModel().clearSelection();
        queue.getSelectionModel().select(songs.get(0));
    }

    public boolean isDark(String color) {
        String fontColor = color;
        boolean isDark = false;

        // remove hash character from string
        String rawFontColor = fontColor.substring(1, fontColor.length());

        // convert hex string to int
        int rgb = Integer.parseInt(rawFontColor, 16);
        Color c = new Color(rgb);
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float brightness = hsb[2];
        if (brightness < 0.5) isDark = true;

        return isDark;
    }

    public void showPopupMessage(final TextFlow message) {
        final Popup popup = new banger.gui.Popup(message, this);
        popup.setOnShown(e -> {
            //Add the popup to the monitor with the application on it.
            Rectangle2D primaryScreenBounds;
            if (!stage.isIconified()) {
                ObservableList<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                primaryScreenBounds = screens.get(0).getVisualBounds();
            } else {
                primaryScreenBounds = Screen.getPrimary().getBounds();
            }
            popup.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - popup.getWidth() - 5);
            popup.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - popup.getHeight() - 5);
        });
        popup.setOpacity(0);
        popup.show(stage);
        new Thread(() -> {
            for (int i = 0; i <= 17; i++) {
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
}
