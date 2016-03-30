package banger.gui;

import banger.audio.MusicPlayer;
import banger.audio.listeners.PlayPauseListener;
import banger.audio.listeners.QueueListener;
import banger.audio.listeners.SkipListener;
import banger.gui.coverview.CoverView;
import banger.gui.library.Library;
import banger.gui.library.views.LyricsView;
import banger.gui.menubar.BangerBar;
import banger.gui.sidebar.PlaylistSelector;
import banger.gui.sidebar.filebrowser.FileBrowser;
import banger.gui.sidebar.viewselector.ViewSelector;
import banger.gui.statusbar.StatusBar;
import banger.util.BangerVars;
import banger.util.InputHandler;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class MainView extends Application{

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
        statusbar.setCustomBackground(Paint.valueOf(BangerVars.STATUSBAR_COLOR));
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

        player.addPlayPauseListener(new PlayPauseListener(stage, statusbar, lyricsview, coverview));
        player.addQueueListener(new QueueListener(queue));
        player.addSkipListener(new SkipListener(library, queue));

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

    public BangerBar getBangerBar() { return bangerBar; }

    public SearchBar getSearchBar() { return searchBar; }
}
