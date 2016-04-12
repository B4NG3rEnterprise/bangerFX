package banger.util;

import banger.audio.data.Song;
import banger.database.DBController;
import banger.gui.MainView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Nick on 12.04.2016.
 */
public class Rating extends HBox {

    @FXML
    RatingStar one;
    @FXML
    RatingStar two;
    @FXML
    RatingStar three;
    @FXML
    RatingStar four;
    @FXML
    RatingStar five;
    @FXML
    RatingStar empty;

    private MainView mv;
    private int song;
    private RatingStar[] stars;

    private int rating;

    public Rating(MainView mv, int rating, int id) {
        this.mv = mv;
        this.song = id;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("rating.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stars = new RatingStar[]{one, two, three, four, five};

        for (int i = 0; i < 5; i++) {
            stars[i].setNumber(i);
        }

        this.rating = rating;
        hover(rating);
    }


    public void hover(int rating) {
        for (int i = 0; i < rating; i++) {
            stars[i].setImage(new Image("file:/" + new File("res/png/star_filled.png").getAbsolutePath()));
        }
        for (int i = rating; i < 5; i++) {
            stars[i].setImage(new Image("file:/" + new File("res/png/star_empty.png").getAbsolutePath()));
        }
    }

    public int getRating() {
        return this.rating;
    }

    @FXML
    protected void exitedView() {
        hover(rating);
    }

    @FXML
    protected void enteredView(MouseEvent event) {
        hover(((RatingStar)event.getSource()).getNumber()+1);
    }

    @FXML
    protected void clickedView(MouseEvent event) {
        this.rating = ((RatingStar)event.getSource()).getNumber()+1;
        DBController.getSongByID(song).rate(rating);
        hover(((RatingStar)event.getSource()).getNumber()+1);
    }

    @FXML
    protected  void clickedEmpty() {
        this.rating = 0;
        DBController.getSongByID(song).rate(rating);
        hover(0);
    }

    @FXML
    protected void enteredEmpty() {
        hover(0);
    }

    @FXML
    protected void exitedEmpty() {
        hover(rating);
    }
}

