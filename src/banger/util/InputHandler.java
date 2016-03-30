package banger.util;

import banger.gui.MainView;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class InputHandler implements EventHandler<KeyEvent> {

    private MainView mainview;

    public InputHandler(MainView mainview){
        this.mainview = mainview;
    }

    public void handle(KeyEvent t){
        if (t.getEventType() == KeyEvent.KEY_PRESSED)
            System.out.println(t.getCode());
        else if (t.getCode() == KeyCode.ENTER){
            if (mainview.getLibrary().isInFocus()) {
                mainview.getMusicPlayer().play(mainview.getLibrary().getSelectedItem());
                mainview.getLibrary().updateQueue(mainview.getLibrary().getSelectedItem());
            } else if (mainview.getFilebrowser().isFocused()){
                System.out.println(mainview.getFilebrowser().getSelectionModel().getSelectedItem());
            }
        }
        else if (t.getCode() == KeyCode.UP)
            System.out.println("UP");
        else if (t.getCode() == KeyCode.DOWN)
            System.out.println("DOWN");
        else if (t.getCode() == KeyCode.LEFT)
            mainview.getMusicPlayer().skipBackward();
        else if (t.getCode() == KeyCode.RIGHT)
            mainview.getMusicPlayer().skipForward();
        else if (t.getCode() == KeyCode.SPACE) {
            if (mainview.getMusicPlayer().isPlaying()) mainview.getMusicPlayer().pause();
            else if (mainview.getMusicPlayer().getNowPlaying() != null && !mainview.getMusicPlayer().isPlaying()) mainview.getMusicPlayer().play();
        }
    }
}
