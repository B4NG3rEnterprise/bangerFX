package banger.audio.data;


import banger.database.DBController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

import java.awt.image.BufferedImage;
import java.io.File;

public class Song {
    private int id;
    private String name;
    private String artist;
    private String album;
    private String genre;
    private int rating;
    private String fileLocation;
    private int length;

    public Song(int id, String name, String artist, String album, String genre, int rating, String fileLocation, int length){
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.rating = rating;
        this.fileLocation = fileLocation;
        this.length = length;
    }

    public void rate(int rating){
        DBController.rate(this.id, rating);
        this.rating = rating;
    }

    public String toString(){
        return id + ": " + name + " - " + artist;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public int getRating() {
        return rating;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public int getLength() { return length; }

    public Image getCover() {
        AudioFile f = null;

        try {
            f = AudioFileIO.read(new File(getFileLocation()));
        } catch (Exception e) {
        }

        Image img = (new Image("file:/" + new File("res/png/Cover0.jpg").getAbsolutePath()));

        try {
            img = SwingFXUtils.toFXImage((BufferedImage) f.getTag().getFirstArtwork().getImage(), null);
        } catch (Exception e) {
            System.out.println("Failed to catch artwork from songfile.");
        }
        return img;
    }


    @Override
    public boolean equals(Object o) {
        boolean res = false;
        if (o instanceof Song) {
            if (this.id == ((Song) o).id)
                res = true;
        }
        return res;
    }

    /*
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeObject(name);
        out.writeObject(artist);
        out.writeObject(album);
        out.writeObject(genre);
        out.writeInt(rating);
        out.writeObject(fileLocation);
        out.writeInt(length);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        id = in.readInt();
        name = (String) in.readObject();
        artist = (String) in.readObject();
        album = (String) in.readObject();
        genre = (String) in.readObject();
        rating = in.readInt();
        fileLocation = (String) in.readObject();
        length = in.readInt();

    }
    */
}
