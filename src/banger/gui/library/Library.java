package banger.gui.library;

import banger.audio.Album;
import banger.audio.Artist;
import banger.audio.Song;
import banger.database.DBController;
import banger.gui.MainView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Library extends StackPane {

    private MainView mainview;
    private ObservableList<Artist> artists;
    private ObservableList<Album> albums;
    private ObservableList<Song> songs;


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
        artists = DBController.getAllArtists();
        albums = DBController.getAllAlbums();
        songs = DBController.getAllSongs();

        TitleView a = new TitleView(mainview, artists);
        ListView t = new ListView(mainview, songs);

        currentView = t;

        Node v = (Node) currentView;

        if (v instanceof ScrollPane) {
            this.widthProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("OK");
                ((ScrollPane) v).setVvalue(0);
                ((ScrollPane) v).setHvalue(0);
            });
        }

        getChildren().add(v);
    }


    public void refreshData() {
        songs = DBController.getAllSongs();
        currentView.refreshData(songs);
    }

    public void updateQueue(Song selected) {
        long seed = System.nanoTime();
        ObservableList<Song> list = getAllFrom(selected);
        if (mainview.getStatusbar().isShuffling()) Collections.shuffle(list, new Random(seed));
        list.add(0, selected);
        mainview.setQueueItems(list);
    }

    public ObservableList<Song> getAllFrom(Song s) {
        List<Song> list = new ArrayList<>();
        ObservableList<Song> result = FXCollections.observableList(list);
        for (int i = songs.indexOf(s) + 1; i < songs.size(); i++)
            list.add(songs.get(i));
        return result;
    }

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
//                currentView = new AlbumView(this.mainview) ;
            } else if (view == this.VIEW_LIST) {
                currentView = new ListView(this.mainview, songs);
            } else if (view == this.VIEW_TITLE) {
                currentView = new TitleView(this.mainview,artists);
            } else if (view == this.VIEW_LYRICS) {
//                currentView =  new LyricsView(this.mainview);
            }
            this.getChildren().add((Node) currentView);
            currentViewNumber = view;
        }
    }

    public Song[] getSelectedItems() {
        return currentView.getSelectedItems();
    }
}
