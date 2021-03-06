package banger.gui;

import banger.Main;
import banger.audio.MusicPlayer;
import banger.audio.listeners.PlayPauseListener;
import banger.audio.listeners.QueueListener;
import banger.gui.coverview.CoverView;
import banger.gui.library.Library;
import banger.gui.menubar.BangerBar;
import banger.gui.options.Options;
import banger.gui.sidebar.PlaylistSelector;
import banger.gui.sidebar.filebrowser.FileBrowser;
import banger.gui.sidebar.viewselector.ViewSelector;
import banger.gui.statusbar.StatusBar;
import banger.util.InputHandler;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainView extends Application {

    private MusicPlayer player;
    public static Stage stage;
    private Scene scene;
    private final int MIN_WIDTH = 1200;
    private final int MIN_HEIGHT = MIN_WIDTH / 16 * 9;

    private StatusBar statusbar;
    private Library library;
    private BangerBar bangerBar;
    private SearchBar searchBar;
    private Queue queue;
    private CoverView coverview;
    private FileBrowser filebrowser;
    private InputHandler handler;
    private PlaylistSelector selector;
    private ViewSelector viewSelector;

    private BorderPane splashLayout;
    private Stage mainStage;
    private static final int SPLASH_WIDTH = 1000;
    private static final int SPLASH_HEIGHT = 480;

    public void init() {
        Image banger = new Image(Main.class.getResourceAsStream("/png/bangernew.png"));
        ImageView splash = new ImageView();
        splash.setImage(banger);
        splash.setStyle("-fx-background-color: rgba(0, 0, 0, 0)");
        splash.setFitWidth(SPLASH_WIDTH);
        splash.setFitHeight(SPLASH_HEIGHT);
        splashLayout = new BorderPane();
        splashLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0)");
        splashLayout.setCenter(splash);
    }

    public void start(final Stage initStage) throws Exception {
        Thread t = new Thread(()->{
            try {
                Thread.sleep(500);
                Platform.runLater(() -> hideSplash(initStage, () -> showMainStage()));
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        showSplash(initStage);
        t.start();
    }

    public void showMainStage() {
        long start = System.currentTimeMillis();
        mainStage = new Stage(StageStyle.DECORATED);

        mainStage.getIcons().add(new Image(Main.class.getResourceAsStream("/png/BangerFX icon.png")));
        mainStage.setTitle("B4NG3rFX");
        mainStage.setMinWidth(MIN_WIDTH);
        mainStage.setMaxWidth(1700);
        mainStage.setMinHeight(MIN_HEIGHT);
        mainStage.setMaxHeight(1700 / 16 * 9);
        mainStage.setOnCloseRequest(e -> {
            player.kill();
            System.exit(0);
        });
        this.stage = mainStage;

        Options.init();

        // Sidebar links
        viewSelector = new ViewSelector(this);
        filebrowser = new FileBrowser(this);
        filebrowser.setMinSize(0, 0);

        handler = new InputHandler(this);

        player = new MusicPlayer(this);

        library = new Library(this);
        library.setMinSize(0, 0);
        library.setPrefSize(500, 600);

        statusbar = new StatusBar(this);
        statusbar.setCustomBackground(Paint.valueOf(Options.backgroundColor));
        statusbar.setMinSize(0, 40);

        bangerBar = new BangerBar(this);
        bangerBar.setMinSize(0, 0);

        queue = new Queue(this);
        queue.setMinSize(0, 0);

        coverview = new CoverView(this);
        coverview.setMinSize(0, 0);

        searchBar = new SearchBar(this);

        //Sidebar rechts
        VBox right = new VBox();
        right.setVgrow(queue, Priority.ALWAYS);
        right.getChildren().addAll(searchBar, new Separator(Orientation.HORIZONTAL), queue, new Separator(Orientation.HORIZONTAL), coverview, new Separator(Orientation.HORIZONTAL));

        Label plLabel = new Label("Playlists");
        plLabel.setPadding(new Insets(5));

        selector = new PlaylistSelector(this);
        selector.setMinSize(0, 0);

        VBox left = new VBox();
        left.getChildren().addAll(viewSelector, new Separator(Orientation.HORIZONTAL), filebrowser, new Separator(Orientation.HORIZONTAL), plLabel, selector);

        BorderPane root = new BorderPane();

        root.setTop(bangerBar);
        root.setRight(right);
        root.setCenter(library);
        root.setBottom(statusbar);
        root.setLeft(left);

        scene = new Scene(root);

        scene.addEventHandler(KeyEvent.KEY_RELEASED, handler);

        player.addPlayPauseListener(new PlayPauseListener(mainStage, statusbar, library, coverview));
        player.addQueueListener(new QueueListener(queue));

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Built stage in " + elapsed + "ms");

        mainStage.setScene(scene);
        mainStage.show();
    }

    private void showSplash(final Stage initStage) {
        Scene splashScene = new Scene(splashLayout);
        splashScene.setFill(Color.TRANSPARENT);
        initStage.initStyle(StageStyle.TRANSPARENT);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        initStage.getIcons().add(new Image(Main.class.getResourceAsStream("/png/BangerFX icon.png")));
        initStage.show();
    }

    private void hideSplash(final Stage initStage, InitCompletionHandler initCompletionHandler){
        initStage.toFront();
        FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
        fadeSplash.setFromValue(1.0);
        fadeSplash.setToValue(0.0);
        fadeSplash.setOnFinished(actionEvent -> initStage.hide());
        fadeSplash.play();
        initCompletionHandler.complete();
    }

    public interface InitCompletionHandler {
        void complete();
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

    public ViewSelector getViewSelector() {
        return this.viewSelector;
    }
}
