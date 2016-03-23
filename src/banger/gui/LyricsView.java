package banger.gui;

import banger.audio.Song;
import banger.util.LyricsGetter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import sun.applet.Main;

/**
 * Created by Merlin on 22.03.2016.
 */
public class LyricsView extends ScrollPane {

    private MainView mainview;
    private String songtext;
    private Text lyrics;

    public LyricsView(MainView mainview){
        super();

        this.mainview = mainview;

        init();
    }

    private void init() {
        lyrics = new Text(songtext);
        lyrics.setTextAlignment(TextAlignment.CENTER);
        StackPane nodeContainer = new StackPane();
        nodeContainer.setStyle("-fx-padding: 20; ");
        nodeContainer.getChildren().add(lyrics);
        nodeContainer.setAlignment(lyrics, Pos.CENTER);
        setContent(lyrics);
        setPrefHeight(400);
        setMaxHeight(mainview.stage.getMaxHeight());

        setStyle("-fx-background-color: transparent, -fx-box-border,-fx-control-inner-background;");

        setContent(nodeContainer);
        viewportBoundsProperty().addListener(
            new ChangeListener<Bounds>() {
                public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
                    nodeContainer.setPrefSize(
                            Math.max(lyrics.getBoundsInParent().getMaxX(), newBounds.getWidth()),
                            Math.max(lyrics.getBoundsInParent().getMaxY(), newBounds.getHeight())
                    );
                }
            });
    }

    public void updateLyricsTest(){
        songtext = LyricsGetter.getLyricsMusixMatch("Macklemore", "American");
    }

    public void updateLyrics(){
        new Thread(() -> {
            Song current = mainview.getMusicPlayer().getNowPlaying();
            String color = "#FA7D38";  // #FA7D38
            String artist = current.getArtist();
            if (artist.equals("Unknown Artist")) artist = "";
            songtext = LyricsGetter.getLyricsMusixMatch(artist, current.getName());
            if (songtext == null) songtext = LyricsGetter.getLyricsSongTexte(artist, current.getName());
            if (songtext == null) songtext = LyricsGetter.getLyricsGenius(artist, current.getName());
            if (songtext == null) songtext = "Leider kein Songtext vorhanden.";
            lyrics.setText(songtext);
        }).start();
        setContent(lyrics);
    }

    public void updateLyrics(Song s){
        new Thread(() -> {
            String artist = s.getArtist();
            if (artist.equals("Unknown Artist")) artist = "";
            songtext = LyricsGetter.getLyricsMusixMatch(artist, s.getName());
            if (songtext == null) songtext = LyricsGetter.getLyricsSongTexte(artist, s.getName());
            if (songtext == null) songtext = LyricsGetter.getLyricsGenius(artist, s.getName());
            if (songtext == null) songtext = "Leider kein Songtext vorhanden.";
            lyrics.setText(songtext);
        }).start();
        setContent(lyrics);
    }
}
