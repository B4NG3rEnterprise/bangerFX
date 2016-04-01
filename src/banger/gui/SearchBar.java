package banger.gui;


import banger.audio.data.Song;
import banger.database.DBController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SearchBar extends HBox {

    MainView mainview;
    TextField tf;
    Button clear;

    public SearchBar(MainView mainview){
        super();

        this.mainview = mainview;

        init();
    }

    public void init(){
        tf = new TextField();
        clear = new Button("Clear");
        GlyphsDude.setIcon(clear, MaterialDesignIcon.CLOSE_CIRCLE, "1.4em", ContentDisplay.GRAPHIC_ONLY);
        clear.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 4 0 0 -27;"
        );
        clear.setOnAction(event -> {
            tf.setText("");
        });
        tf.setPromptText("Search something...");
        tf.setOnKeyReleased(event -> {
            ObservableList<Song> songs = DBController.searchFor(tf.getText());
            mainview.getQueue().setItems(songs);

            /* if (event.getCode() == KeyCode.ENTER){
                clear.fire();
            } */
        });

        setHgrow(tf, Priority.ALWAYS);
        setPadding(new Insets(5));

        getChildren().addAll(tf, clear);
    }

    public TextField getTextField() { return tf; }
}
