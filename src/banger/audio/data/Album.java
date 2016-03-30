package banger.audio.data;

import banger.database.DBController;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class Album {

    private int id;
    private String albumName;
    private String artist;
    private int release;
    private Image cover = new Image("file:/D:/Data/Programming/bangerFX/res/png/Cover0.jpg");

    public Album(int id, String albumName, String artist, int release) {
        this.id = id;
        this.albumName = albumName;
        this.artist = artist;
        this.release = release;
    }

    public int getId() {
        return id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtist() {
        return artist;
    }

    public int getRelease() {
        return release;
    }

    public ObservableList<Song> getSongs() {
        return DBController.getSongsFrom(this);
    }

    public Image getCover() {
        return cover;
    }


}
