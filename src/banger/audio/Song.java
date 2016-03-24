package banger.audio;


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

    public String toString(){
        return id + ": " + name + " - " + artist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public int getLength() { return length; }

    public void setLength(int length) { this.length = length; }
}
