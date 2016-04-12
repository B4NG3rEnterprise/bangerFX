package banger.util;

import javafx.scene.image.ImageView;

/**
 * Created by Nick on 12.04.2016.
 */
public class RatingStar extends ImageView {

    private int number;


    public RatingStar() {
        super();
    }


    public void setNumber(int number) {
        this.number = number;
    }
    public int getNumber() { return this.number; }
}
