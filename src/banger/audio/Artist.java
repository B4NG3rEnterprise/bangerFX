package banger.audio;

import banger.database.DBController;
import javafx.collections.ObservableList;

public class Artist {

    private int id;
    private String name;

    public Artist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ObservableList<Album> getAlbums() {
        return DBController.getAlbumsFrom(this);
    }
}
