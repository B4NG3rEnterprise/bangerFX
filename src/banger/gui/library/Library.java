package banger.gui.library;

import banger.audio.data.Album;
import banger.audio.data.Artist;
import banger.audio.data.Song;
import banger.database.DBController;
import banger.gui.MainView;
import banger.gui.library.views.AlbumView;
import banger.gui.library.views.TitleView;
import banger.gui.library.views.View;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

public class Library extends StackPane {

    private MainView mainview;
    private ObservableList<Artist> artists;
    private ObservableList<Album> albums;
    private ObservableList<Song> songs;

    //title: album -> songs; album: cover, table: table ._.
    public static final int VIEW_TITLE = 0;
    public static final int VIEW_ALBUM = 1;
    public static final int VIEW_TABLE = 2;

    View currentView;

    public Library(MainView mainview) {
        super();
        this.mainview = mainview;
        artists = DBController.getAllArtists();
        albums = DBController.getAllAlbums();
        songs = DBController.getAllSongs();

        AlbumView a = new AlbumView(mainview, artists);
        TitleView t = new TitleView(mainview, songs);

        currentView = t;

        Node v = (Node) currentView;


        if (v instanceof ScrollPane) {
            this.widthProperty().addListener((observable, oldValue, newValue) -> {
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

    public void updateQueue(Song selected){
        ArrayList<Song> list = getAllFrom(selected);
        list.add(0, selected);
        mainview.getMusicPlayer().updateQueue(list);
    }

    public ArrayList<Song> getAllFrom(Song s){
        ArrayList<Song> list = new ArrayList<>();
        for (int i = songs.indexOf(s) + 1; i < songs.size(); i++)
            list.add(songs.get(i));
        return list;
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
        //TODO
    }

    public Song[] getSelectedItems() { return currentView.getSelectedItems(); }
}
