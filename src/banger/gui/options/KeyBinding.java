package banger.gui.options;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Created by Nick on 30.03.2016.
 */
public class KeyBinding {

    private KeyIDName keyIDName;
    private KeyCodeCombination combo;
    private KeyCode code;
    private String display;

    public KeyBinding(KeyIDName kin, KeyCodeCombination kcc) {
        this.keyIDName = kin;
        this.combo = kcc;
        display = kcc.getDisplayText();
    }

    public KeyBinding(KeyIDName kin, KeyCode kc) {
        this.keyIDName = kin;
        this.code = kc;
        display = kc.getName();

    }

    public KeyCodeCombination getKeyCodeCombination() {
        return this.combo;
    }

    public KeyIDName getKeyIDName() {
        return this.keyIDName;
    }

    public KeyCode getKeyCode() {
        return this.code;
    }

    public String getDisplay() {
        return this.display;
    }

}
