package banger.util;

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
    private RatingStar[] stars;

    private int rating;

    public Rating(MainView mv, int rating) {
        this.mv = mv;
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

    public void rate(int rating) {
        this.rating = rating;
        hover(rating);
    }

    public void hover(int rating) {
        for (int i = 0; i < rating; i++) {
            stars[i].setImage(new Image("file:/" + new File("res/png/star_filled.png").getAbsolutePath()));
        }
        for (int i = rating; i < 5; i++) {
            stars[i].setImage(new Image("file:/" + new File("res/png/star_empty.jpg").getAbsolutePath()));

        }
    }

    public int getRating() {
        return this.rating;
    }

    @FXML
    protected void exitedView(MouseEvent event) {
        hover(rating);
    }

    @FXML
    protected void enteredView(MouseEvent event) {
        hover(((RatingStar)event.getSource()).getNumber()+1);
    }

    @FXML
    protected void clickedView(MouseEvent event) {
        this.rating = ((RatingStar)event.getSource()).getNumber()+1;

        hover(((RatingStar)event.getSource()).getNumber()+1);
    }

    @FXML
    protected  void clickedEmpty() {
        this.rating = 0;
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

