package banger.util;

import banger.gui.MainView;
import banger.gui.options.KeyBinding;
import banger.gui.options.KeyIDName;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.prefs.Preferences;


public class InputHandler implements EventHandler<KeyEvent> {

    private MainView mainview;
    private ObservableList<KeyBinding> keyBindings;
    private Wini ini;


    public InputHandler(MainView mainview) {
        this.mainview = mainview;
        refreshKeyBindings();
    }

    @Override
    public void handle(KeyEvent t) {
        System.out.println(t.getText());
        Iterator<KeyBinding> it = keyBindings.listIterator();
        KeyBinding kb;
        while (it.hasNext()) {
            kb = it.next();
            if ((kb.getKeyCodeCombination()!= null && kb.getKeyCodeCombination().match(t))|| (kb.getKeyCode() != null && t.getCode() == kb.getKeyCode())) {
                switch (kb.getKeyIDName().getID()) {
                    case 0:
                        System.out.println("Excecuted Play/Pause");
                        if (mainview.getMusicPlayer().isPlaying()) mainview.getMusicPlayer().pause();
                        else if (mainview.getMusicPlayer().getNowPlaying() != null && !mainview.getMusicPlayer().isPlaying())
                            mainview.getMusicPlayer().play();
                        break;
                    case 1:
                        System.out.println("Executed PlaySelected");
                        if (mainview.getLibrary().isInFocus()) {
                            mainview.getMusicPlayer().play(mainview.getLibrary().getSelectedItem());
                            mainview.getLibrary().updateQueue();
                        } else if (mainview.getFilebrowser().isFocused()) {
                            System.out.println(mainview.getFilebrowser().getSelectionModel().getSelectedItem());
                        }
                        break;
                    case 2:
                        System.out.println("Executed forward");
                        mainview.getMusicPlayer().skipForward();
                        break;
                    case 3:
                        System.out.println("Executed backward");
                        mainview.getMusicPlayer().skipBackward();
                        break;
                    default:
                        System.out.println("no action assingned to this id");
                        break;
                }
            }
        }
    }

    public void refreshKeyBindings() {
        ArrayList<KeyBinding> temp1 = new ArrayList<KeyBinding>();
        try {
            ini = new Wini(new File("res/options.ini"));

            //Einlesen der Tastenkombinationen aus der options.ini
            for (String optionKey : ini.get("KeyBindings").keySet()) {
                KeyBinding keyBinding;
                KeyCode x = KeyCode.getKeyCode(ini.get("KeyBindings", optionKey));
                if ((x == KeyCode.SHIFT) || (x == KeyCode.CONTROL) || (x == KeyCode.ALT) || (x == KeyCode.META) || (x == KeyCode.SHORTCUT)) {
                    keyBinding = new KeyBinding(KeyIDName.getByID(Integer.parseInt(optionKey)), x);
                } else {
                    KeyCombination temp = KeyCombination.keyCombination(ini.get("KeyBindings", optionKey));
                    String[] sa = ini.get("KeyBindings", optionKey).split("\\+");
                    keyBinding = new KeyBinding
                            (KeyIDName.getByID(Integer.parseInt(optionKey)),
                                    new KeyCodeCombination(KeyCode.valueOf(sa[sa.length - 1]), temp.getShift(), temp.getControl(), temp.getAlt(), temp.getMeta(), temp.getShortcut()));
                }
                temp1.add(keyBinding);
            }
            keyBindings = FXCollections.observableList(temp1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to read options.ini");
        }
    }

    public ObservableList<KeyBinding> getKeyBindings() {
        return this.keyBindings;
    }

    public Wini getIni() {
        return this.ini;
    }
}
