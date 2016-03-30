package banger.gui;


import banger.audio.data.Song;
import banger.database.DBController;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SearchBar extends HBox {

    MainView mainview;
    TextField tf;
    Button submit;

    public SearchBar(MainView mainview){
        super();

        this.mainview = mainview;

        init();
    }

    public void init(){
        tf = new TextField();
        submit = new Button("GO");
        submit.setOnAction(event -> {
            // TODO display items in ListView instead of Queue
            ObservableList<Song> songs = DBController.searchFor(tf.getText());
            mainview.getQueue().setItems(songs);
        });
        tf.setPromptText("Search something...");
        tf.setOnKeyReleased(event -> {
            submit.fire(); // live search

            /* if (event.getCode() == KeyCode.ENTER){
                submit.fire();
            } */
        });

        setHgrow(tf, Priority.ALWAYS);
        setPadding(new Insets(5));

        getChildren().addAll(tf); // add button!!
    }

    public TextField getTextField() { return tf; }
}
