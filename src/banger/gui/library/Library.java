package banger.gui.library;

import banger.audio.data.Album;
import banger.audio.data.Artist;
import banger.audio.data.Song;
import banger.database.DBController;
import banger.gui.MainView;
import banger.gui.library.views.ListView;
import banger.gui.library.views.LyricsView;
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
                ((ScrollPane) v).setVvalue(0);
                ((ScrollPane) v).setHvalue(0);
            });
        }

        getChildren().add(v);
    }

    public int getCurrentView() { return currentViewNumber; }


    public void refreshData() {
        songs = DBController.getAllSongs();
        currentView.refreshData(songs);
    }

    public void updateQueue(Song selected){
        ArrayList<Song> list = getAllFrom(selected);
        //list.add(0, selected);
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
        if (currentViewNumber != view) {
            this.getChildren().remove(currentView);
            if (view == this.VIEW_ALBUM) {
//                currentView = new TitleView(this.mainview) ;
            } else if (view == this.VIEW_LIST) {
                currentView = new ListView(this.mainview, songs);
            } else if (view == this.VIEW_TITLE) {
                currentView = new TitleView(this.mainview,artists);
            } else if (view == this.VIEW_LYRICS) {
                currentView =  new LyricsView(this.mainview);
            }
            this.getChildren().add((Node) currentView);
            currentViewNumber = view;
        }
    }

    public Song[] getSelectedItems() {
        return currentView.getSelectedItems();
    }
}
