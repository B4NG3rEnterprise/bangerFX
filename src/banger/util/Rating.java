package banger.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private RatingStar[] stars;

    private int rating;

    public Rating(int rating) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("rating.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


        stars = new RatingStar[]{one, two, three, four, five};

        for (int i = 0; i<5; i++) {
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
        stars[i].setImage(new Image("file:/" + new File("res/png/star_filled.jpg").getAbsolutePath()));
    }
    for (int i = rating; i < 5; i++) {
        stars[i].setImage(new Image("file:/" + new File("res/png/star_empty.jpg").getAbsolutePath()));

    }
    }

    public int getRating() { return this.rating; }

    @FXML
    protected void exitedView(ActionEvent ae) {
        hover(rating);
    }

    @FXML
    protected void enteredView(ActionEvent ae) {
        hover(((RatingStar)ae.getSource()).getNumber());
    }

    @FXML
    protected  void clickedView(ActionEvent ae) {
        this.rating = ((RatingStar)ae.getSource()).getNumber();
        hover(((RatingStar)ae.getSource()).getNumber());
    }
}

