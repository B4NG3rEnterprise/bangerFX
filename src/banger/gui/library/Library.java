package banger.gui.library;

import banger.audio.data.Song;
import banger.gui.MainView;
import banger.gui.library.views.*;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

public class Library extends StackPane {

    private MainView mainview;

    //views
    private TitleView titleView;
    private ListView listView;
    private LyricsView lyricsView;
    private AlbumView albumView;

    //TITLE: alben links, songliste rechts
    public static final int VIEW_TITLE = 0;

    //ALBUM: nur Albumcover mit Artist/Name angezeigt
    public static final int VIEW_ALBUM = 1;

    //LIST: klassisch, ausschließlich Liste/Tabelle;   AUSGANGSANSICHT
    public static final int VIEW_LIST = 2;

    //LYRICS: lyrics
    public static final int VIEW_LYRICS = 3;

    private int currentViewNumber = 2;

    View currentView;

    public Library(MainView mainview) {
        super();
        this.mainview = mainview;

        titleView = new TitleView(mainview);
        listView = new ListView(mainview);
        // lyricsView = new LyricsView(mainview);
        // albumView = new AlbumView(mainview);

        currentView = listView;

        Node v = (Node) currentView;
        if (v instanceof ScrollPane) {
            this.widthProperty().addListener((observable, oldValue, newValue) -> {
                ((ScrollPane) v).setVvalue(0);
                ((ScrollPane) v).setHvalue(0);
            });
        }

        getChildren().add(v);
    }

    public int getCurrentView() { return currentViewNumber; }


    public void refreshData() {
        currentView.refreshData();
    }

    /*public ObservableList<Song> getAllFrom(Song s){
        ObservableList<Song> list = FXCollections.observableArrayList();
        for (int i = songs.indexOf(s) + 1; i < songs.size(); i++)
            list.add(songs.get(i));
        return list;
    }*/

    public void select(Song current) {
        currentView.select(current);
    }

    public Song getSelectedItem() {
        return currentView.getSelectedItem();
    }

    public boolean isInFocus() {
        return isFocused() || ((Node) currentView).isFocused();
    }

    public void setView(int view) {
        if (currentViewNumber != view) {
            this.getChildren().remove(currentView);
            if (view == this.VIEW_ALBUM) {
                albumView = new AlbumView(mainview);
                currentView = albumView ;
            } else if (view == this.VIEW_LIST) {
                currentView = listView;
            } else if (view == this.VIEW_TITLE) {
                currentView = titleView;
            } else if (view == this.VIEW_LYRICS) {
                lyricsView = new LyricsView(mainview);
                currentView =  lyricsView;
                lyricsView.initLyrics();
            }
            this.getChildren().add((Node) currentView);
            currentViewNumber = view;
        }
    }

    public Song[] getSelectedItems() {
        return currentView.getSelectedItems();
    }
}
