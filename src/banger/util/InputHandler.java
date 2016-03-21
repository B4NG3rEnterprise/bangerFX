package banger.util;

import banger.gui.MainView;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Created by Merlin on 21.03.2016.
 */
public class InputHandler implements EventHandler<KeyEvent> {

    private MainView mainView;

    public InputHandler(MainView mainView){
        this.mainView = mainView;
    }

    public void handle(KeyEvent t){
        if (t.getEventType() == KeyEvent.KEY_PRESSED)
            System.out.println(t.getCode());
        else if (t.getCode() == KeyCode.ENTER)
            System.out.println("ENTER");
        else if (t.getCode() == KeyCode.UP)
            System.out.println("UP");
        else if (t.getCode() == KeyCode.DOWN)
            System.out.println("DOWN");
        else if (t.getCode() == KeyCode.LEFT)
            System.out.println("LEFT");
        else if (t.getCode() == KeyCode.RIGHT)
            System.out.println("RIGHT");
    }
}
