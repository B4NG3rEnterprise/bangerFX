package banger.gui.options;

import banger.gui.MainView;
import banger.util.DeviceItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class Options extends VBox {

    private final static String PATH = "res/options.ini";
    private static Wini wini;

    public static String resetColor = "#FA7D38";
    public static String backgroundColor;
    public static boolean notifications;
    public static String fileBrowserPath;
    public static float crossfade;

    @FXML
    TableView<KeyBinding> table;
    @FXML
    Spinner crossfadeSpinner;
    @FXML
    ChoiceBox<String> devices;
    @FXML
    ColorPicker colorPicker;

    private MainView mv;

    public static void init() {
        File f = new File(PATH);
        if(!f.exists())
            createOptions();

        try {
            wini = new Wini(new File(PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        backgroundColor = wini.get("Options", "BackgroundColor").replace('.', '#');
        notifications = Boolean.parseBoolean(wini.get("Options", "Notifications"));
        fileBrowserPath = wini.get("Options", "FilePath");
        crossfade = Float.parseFloat(wini.get("Options", "Crossfade"));
    }

    private static void createOptions() {
        File f = new File(PATH);
        try {
            f.createNewFile();
            wini = new Wini(new File(PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        wini.put("Options", "AudioDevice", -1);
        wini.put("Options", "BackgroundColor", "0xffb366ff");
        wini.put("Options", "Crossfade", 0);
        wini.put("Options", "Notifications", true);
        wini.put("Options", "FilePath", "D:/Musik"); //TODO: Implement dialog to fill this on fist startup!

        wini.put("KeyBindings", "0", "SPACE");
        wini.put("KeyBindings", "1", "ENTER");
        wini.put("KeyBindings", "2", "RIGHT");
        wini.put("KeyBindings", "3", "LEFT");

        try {
            wini.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Options(MainView mv) {
        this.mv = mv;
        wini = mv.getInputHandler().getIni();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("options.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //BackgroundColor
        colorPicker.setValue(Color.valueOf(backgroundColor));
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                backgroundColor = colorPicker.getValue().toString();
                updateColors();
                wini.put("Options", "BackgroundColor", backgroundColor);
                try {
                    wini.store();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        //BackgroundColor END
        //Crossfade
        crossfadeSpinner.setEditable(true);
        crossfadeSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != newValue) {
                crossfade = Float.parseFloat(newValue.toString());
                wini.put("Options", "Crossfade", newValue.toString());
                try {
                    wini.store();
                } catch (IOException e) {
                }
            }
        });
        double currentValue = 0;
        try {
            currentValue = Double.parseDouble(wini.get("Options", "Crossfade"));
        } catch (NullPointerException e) {
            System.out.println("Couldn't find Crossfade in options.ini");
            e.printStackTrace();
        }
        crossfadeSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 30.0, currentValue, 0.5));
        //Crossfade END

        TableColumn command = new TableColumn<>("Befehl");
        command.setCellValueFactory(new PropertyValueFactory<KeyBinding, KeyIDName>("keyIDName"));
        command.setCellFactory(new Callback<TableColumn<KeyBinding, KeyIDName>, TableCell<KeyBinding, KeyIDName>>() {
            @Override
            public TableCell<KeyBinding, KeyIDName> call(TableColumn<KeyBinding, KeyIDName> param) {
                TableCell<KeyBinding, KeyIDName> tempCell = new TableCell<KeyBinding, KeyIDName>() {
                    @Override
                    protected void updateItem(KeyIDName item, boolean empty) {
                        if (item != null) {
                            setText(item.getName());
                        }
                    }
                };
                return tempCell;
            }
        });
        Iterator<DeviceItem> it = mv.getMusicPlayer().getDevices().listIterator();
        while (it.hasNext()) {
            DeviceItem di;
            devices.getItems().add((di = it.next()).toString());
            if (di.toString().equals(wini.get("Options", "AudioDevice"))) {
                devices.setValue(di.toString());
            }
        }

        //Devices
        devices.setOnAction(e -> {
            DeviceItem item = null;
            Iterator<DeviceItem> iter = mv.getMusicPlayer().getDevices().listIterator();
            while (iter.hasNext()) {
                DeviceItem temp = null;
                if ((temp = iter.next()).toString().equals(devices.getValue().toString())) {
                    item = temp;
                    wini.put("Options", "AudioDevice", temp.toString());
                    try {
                        wini.store();
                    } catch (IOException f) {
                        f.printStackTrace();
                        System.out.println("unable to store");
                    }
                }
            }
            mv.getMusicPlayer().setOutputDevice(item.getDeviceInt());
        });
        //Devices End


        TableColumn combination = new TableColumn<>("Tastenkombination");
        combination.setCellValueFactory(new PropertyValueFactory<KeyBinding, String>("display"));
        combination.setCellFactory(new Callback<TableColumn<KeyBinding, String>, TableCell<KeyBinding, String>>() {
            @Override
            public TableCell<KeyBinding, String> call(TableColumn<KeyBinding, String> param) {
                TableCell<KeyBinding, String> tempCell = new TableCell<KeyBinding, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (item != null) {
                            setText(item);
                        }
                    }
                };
                tempCell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() > 1) {
                            TableCell<KeyBinding, KeyCodeCombination> c = (TableCell<KeyBinding, KeyCodeCombination>) event.getSource();
                            System.out.println(((KeyBinding) c.getTableRow().getItem()).getKeyIDName().getName());
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            Scene sc = new Scene(new KeySelectionPopup());
                            stage.setScene(sc);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
                                @Override
                                public void handle(KeyEvent event) {
                                    System.out.println("pressed: " + event.getCode().getName());
                                    if (event.getCode() == KeyCode.ESCAPE) {
                                        stage.close();
                                    } else {
                                        if (event.getCode() == KeyCode.CONTROL) {
                                            wini.put("KeyBindings", "" + ((KeyBinding) c.getTableRow().getItem()).getKeyIDName().getID(), KeyCode.CONTROL.getName());
                                        } else if (event.getCode() == KeyCode.SHORTCUT) {
                                            wini.put("KeyBindings", "" + ((KeyBinding) c.getTableRow().getItem()).getKeyIDName().getID(), KeyCode.SHORTCUT.getName());
                                        } else if (event.getCode() == KeyCode.SHIFT) {
                                            wini.put("KeyBindings", "" + ((KeyBinding) c.getTableRow().getItem()).getKeyIDName().getID(), KeyCode.SHIFT.getName());
                                        } else if (event.getCode() == KeyCode.META) {
                                            wini.put("KeyBindings", "" + ((KeyBinding) c.getTableRow().getItem()).getKeyIDName().getID(), KeyCode.META.getName());
                                        } else if (event.getCode() == KeyCode.ALT) {
                                            wini.put("KeyBindings", "" + ((KeyBinding) c.getTableRow().getItem()).getKeyIDName().getID(), KeyCode.ALT.getName());
                                        } else {
                                            KeyCodeCombination.Modifier[] modifiers = new KeyCodeCombination.Modifier[0];
                                            KeyCodeCombination.Modifier[] temp = modifiers;
                                            int size = 0;
                                            if (event.isControlDown()) {
                                                size += 1;
                                                temp = modifiers;
                                                modifiers = new KeyCodeCombination.Modifier[size];
                                                modifiers[size - 1] = KeyCodeCombination.CONTROL_DOWN;
                                                for (int i = 0; i < temp.length; i++) {
                                                    modifiers[i] = temp[i];
                                                }
                                            }
                                            if (event.isAltDown()) {
                                                size += 1;
                                                temp = modifiers;
                                                modifiers = new KeyCodeCombination.Modifier[size];
                                                modifiers[size - 1] = KeyCodeCombination.ALT_DOWN;
                                                for (int i = 0; i < temp.length; i++) {
                                                    modifiers[i] = temp[i];
                                                }
                                            }
                                            if (event.isMetaDown()) {
                                                size += 1;
                                                temp = modifiers;
                                                modifiers = new KeyCodeCombination.Modifier[size];
                                                modifiers[size - 1] = KeyCodeCombination.META_DOWN;
                                                for (int i = 0; i < temp.length; i++) {
                                                    modifiers[i] = temp[i];
                                                }
                                            }
                                            if (event.isShiftDown()) {
                                                size += 1;
                                                temp = modifiers;
                                                modifiers = new KeyCodeCombination.Modifier[size];
                                                modifiers[size - 1] = KeyCodeCombination.SHIFT_DOWN;
                                                for (int i = 0; i < temp.length; i++) {
                                                    modifiers[i] = temp[i];
                                                }
                                            }
                                            if (event.isShortcutDown()) {
                                                size += 1;
                                                temp = modifiers;
                                                modifiers = new KeyCodeCombination.Modifier[size];
                                                modifiers[size - 1] = KeyCodeCombination.SHORTCUT_DOWN;
                                                for (int i = 0; i < temp.length; i++) {
                                                    modifiers[i] = temp[i];
                                                }
                                            }
                                            KeyCodeCombination kcc = new KeyCodeCombination(event.getCode(), modifiers);
                                            wini.put("KeyBindings", "" + ((KeyBinding) c.getTableRow().getItem()).getKeyIDName().getID(), kcc.getName().toUpperCase());
                                        }
                                        try {
                                            wini.store();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            System.out.println("unable to store into options.ini");
                                        }
                                        mv.getInputHandler().refreshKeyBindings();
                                        updateKeyBindingTable();
                                        stage.close();
                                    }
                                }
                            });
                            stage.show();
                        }
                    }
                });
                return tempCell;
            }
        });
        combination.setStyle("-fx-alignment: CENTER-RIGHT;");

        command.setMinWidth(this.getWidth()/2);
        combination.setMinWidth(this.getWidth()/2);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getColumns().addAll(command, combination);
        updateKeyBindingTable();

    }


    public void updateKeyBindingTable() {
        table.setItems(mv.getInputHandler().getKeyBindings());
    }

    public double getCrossfade() {
        return crossfade;
    }

    public Wini getIni() {
        return this.wini;
    }

    public void updateColors() {
        mv.getViewSelector().updateColor();
    }

}
