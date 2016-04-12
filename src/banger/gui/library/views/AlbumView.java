package banger.gui.library.views;

import banger.audio.data.Album;
import banger.audio.data.Artist;
import banger.audio.data.Song;
import banger.database.DBController;
import banger.gui.MainView;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;


public class AlbumView extends TilePane implements View {

    private MainView mainview;
    private ObservableList<Album> albums;

    public AlbumView(MainView m) {
        mainview = m;
        albums = DBController.getAllAlbums();


        for (Album a : albums){
            ObservableList<Song> songs = a.getSongs();
            if(songs.size() > 0) {
                ImageView cover = new ImageView(a.getCover());
                cover.setFitWidth(200);
                cover.setPreserveRatio(true);
                cover.setSmooth(true);
                cover.setCache(true);
                cover.setOnMousePressed(event -> {
                    if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                        mainview.getMusicPlayer().play(songs.get(0));

                        //update queue in musicplayer
                        mainview.getMusicPlayer().updateQueue(songs);
                    }
                });
                getChildren().add(cover);
            } else
                continue;
        }

        setPadding(new Insets(15, 15, 15, 15));
    }

    public void refreshData() {

    }

    public void select(Song song) {

    }

    public Song getSelectedItem() {
        return null;
    }

    public Song[] getSelectedItems() {
        return new Song[0];
    }
}
