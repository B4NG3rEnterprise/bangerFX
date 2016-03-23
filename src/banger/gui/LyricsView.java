package banger.gui;

import banger.audio.Song;
import banger.util.BangerVars;
import banger.util.LyricsGetter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


/**
 * Created by Merlin on 22.03.2016.
 */
public class LyricsView extends VBox {

    private MainView mainview;
    private Text lyrics;
    private Button prev, next;
    private String[] songtexts;
    private int currentService, available, btnCounter;
    private Thread thread;

    private final int MUSIX_BOTH = 0;
    private final int SONGTEXTE_BOTH = 1;
    private final int MUSIX_TITLE = 2;
    private final int SONGTEXTE_TITLE = 3;

    public LyricsView(MainView mainview){
        super();

        this.mainview = mainview;

        init();
    }

    private void init() {
        thread = new Thread();
        btnCounter = 0;
        available = 0;
        songtexts = new String[4];

        setSpacing(2);
        getStylesheets().add("banger/gui/lyricsview.css");

        lyrics = new Text();
        lyrics.setTextAlignment(TextAlignment.CENTER);
        lyrics.setStyle("-fx-font-size: 12;");

        Pane overlay = new Pane();
        Stop[] stops = new Stop[] { new Stop(0.0, Color.WHITE), new Stop(0.1, Color.TRANSPARENT), new Stop(0.9, Color.TRANSPARENT), new Stop(1, Color.WHITE) };
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        overlay.setBackground(new Background(new BackgroundFill(linearGradient, null, null)));
        overlay.setMouseTransparent(true);

        StackPane nodeContainer = new StackPane();
        nodeContainer.setStyle("-fx-padding: 35 0; ");
        nodeContainer.getChildren().add(lyrics);
        nodeContainer.setAlignment(lyrics, Pos.CENTER);

        ScrollPane sp = new ScrollPane();
        sp.setContent(nodeContainer);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        StackPane lyricsContainer = new StackPane();
        lyricsContainer.setMinHeight(400); // TODO remove later
        lyricsContainer.getChildren().addAll(sp, overlay);

        HBox hbox = new HBox(2);
        prev = new Button("Prev");
        next = new Button("Next");
        prev.setOnAction(event -> {
            for (int i = currentService; i >= 0; i--) {
                if (songtexts[i] != null && i != currentService) {
                    currentService = i;
                    lyrics.setText(songtexts[i]);
                    btnCounter++;
                    updateButtons();
                    break;
                }
            }
            // System.out.println(currentService + ", " + btnCounter);
        } );
        next.setOnAction(event -> {
            for (int i = currentService; i < songtexts.length; i++) {
                if (songtexts[i] != null && i != currentService) {
                    currentService = i;
                    lyrics.setText(songtexts[i]);
                    btnCounter--;
                    updateButtons();
                    break;
                }
        }
            // System.out.println(currentService + ", " + btnCounter);
        } );

        next.setDisable(true);
        prev.setDisable(true);
        hbox.setPadding(new Insets(5));
        Pane lspacer = new Pane();
        Pane rspacer = new Pane();
        Button help = new Button("<- Other Lyrics ->");
        help.setDisable(true);
        HBox.setHgrow(
                lspacer,
                Priority.SOMETIMES
        );
        HBox.setHgrow(
                rspacer,
                Priority.SOMETIMES
        );
        hbox.getChildren().addAll(prev, lspacer, help, rspacer, next);

        getChildren().addAll(hbox, lyricsContainer);

        sp.viewportBoundsProperty().addListener(
            new ChangeListener<Bounds>() {
                public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
                    nodeContainer.setPrefSize(
                            Math.max(lyrics.getBoundsInParent().getMaxX(), newBounds.getWidth()),
                            Math.max(lyrics.getBoundsInParent().getMaxY(), newBounds.getHeight())
                    );
                }
            }
        );
    }

    public void initLyrics(){
        thread = new Thread(() -> {
            getPossibleLyrics(mainview.getMusicPlayer().getNowPlaying());
        });
        thread.start();
    }

    public synchronized void getPossibleLyrics(Song s){
        btnCounter = 0;
        available = 0;
        prev.setDisable(true);
        next.setDisable(true);
        String artist = s.getArtist();
        String title = s.getName();
        for (int x = 0; x < BangerVars.FILE_EXTENSIONS.length; x++)
            title = title.replace(BangerVars.FILE_EXTENSIONS[x], "");
        if (artist.equals("Unknown Artist")) artist = "";

        lyrics.setText("Lyrics werden geladen...");

        songtexts[MUSIX_BOTH] = LyricsGetter.getLyricsMusixMatch(artist, title);
        if (songtexts[MUSIX_BOTH] != null) lyrics.setText(songtexts[MUSIX_BOTH]);
        songtexts[SONGTEXTE_BOTH] = LyricsGetter.getLyricsSongTexte(artist, title);
        songtexts[MUSIX_TITLE] = LyricsGetter.getLyricsMusixMatch("", title);
        songtexts[SONGTEXTE_TITLE] = LyricsGetter.getLyricsSongTexte("", title);

        for (int i = 0; i < songtexts.length; i++) {
            if (songtexts[i] == null) continue;
            else {
                currentService = i;
                break;
            }
        }

        for (int i = 0; i < songtexts.length; i++) {
            if (songtexts[i] == null) continue;
            else btnCounter++;
        }
        available = btnCounter;

        String songtext = "Keine Lyrics vorhanden.";
        if (available != 0) songtext = songtexts[currentService];

        updateButtons();
        lyrics.setText(songtext);

    }

    private void updateButtons(){
        boolean prevDisabled = false;
        boolean nextDisabled = false;
        if (btnCounter == available && available > 1){
            prevDisabled = true;
            nextDisabled = false;
        } else if (btnCounter == 1 && available > 1){
            prevDisabled = false;
            nextDisabled = true;
        } else if (available <= 1) {
            prevDisabled = true;
            nextDisabled = true;
        }
        prev.setDisable(prevDisabled);
        next.setDisable(nextDisabled);
    }

    /* make this class scrollpane
    private void init() {
        getStylesheets().add("banger/gui/lyricsview.css");

        lyrics = new Text();
        lyrics.setTextAlignment(TextAlignment.CENTER);

        StackPane nodeContainer = new StackPane();
        nodeContainer.setStyle("-fx-padding: 20; ");
        nodeContainer.getChildren().add(lyrics);
        nodeContainer.setAlignment(lyrics, Pos.CENTER);

        setContent(nodeContainer);

        setStyle("-fx-background-color: transparent;");

        viewportBoundsProperty().addListener(
                new ChangeListener<Bounds>() {
                    public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
                        nodeContainer.setPrefSize(
                                Math.max(lyrics.getBoundsInParent().getMaxX(), newBounds.getWidth()),
                                Math.max(lyrics.getBoundsInParent().getMaxY(), newBounds.getHeight())
                        );
                    }
                });

        updateLyricsTest();
    }
    */

    /* make this class stackpane
    private void init() {
        getStylesheets().add("banger/gui/lyricsview.css");

        lyrics = new Text();
        lyrics.setTextAlignment(TextAlignment.CENTER);

        Pane overlay = new Pane();
        Stop[] stops = new Stop[] { new Stop(0.0, Color.WHITE), new Stop(0.15, Color.TRANSPARENT), new Stop(0.85, Color.TRANSPARENT), new Stop(1.0, Color.WHITE) };
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        overlay.setBackground(new Background(new BackgroundFill(linearGradient, null, null)));
        overlay.setMouseTransparent(true);

        StackPane nodeContainer = new StackPane();
        nodeContainer.setStyle("-fx-padding: 20; ");
        nodeContainer.getChildren().addAll(lyrics);
        nodeContainer.setAlignment(lyrics, Pos.CENTER);

        ScrollPane sp = new ScrollPane();
        sp.setContent(nodeContainer);
        sp.requestFocus();

        getChildren().addAll(sp, overlay);

        setStyle("-fx-background-color: transparent, -fx-box-border,-fx-control-inner-background;");

        sp.viewportBoundsProperty().addListener(
                new ChangeListener<Bounds>() {
                    public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
                        nodeContainer.setPrefSize(
                                Math.max(lyrics.getBoundsInParent().getMaxX(), newBounds.getWidth()),
                                Math.max(lyrics.getBoundsInParent().getMaxY(), newBounds.getHeight())
                        );
                    }
                });

        updateLyricsTest();
    }
    */
}
