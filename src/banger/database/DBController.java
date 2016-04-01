package banger.database;


import banger.audio.data.Album;
import banger.audio.data.Artist;
import banger.audio.data.Song;
import banger.util.BangerVars;
import banger.util.PlaylistItem;
import banger.util.PlaylistManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBController {

    private static final DBController dbcontroller = new DBController();
    private static Connection connection;
    private static final String DB_PATH = "res/" + "banger.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Fehler beim Laden des JDBC-Treibers");
            e.printStackTrace();
        }
    }

    public static DBController getInstance(){
        return dbcontroller;
    }

    private static void initDBConnection() {
        try {
            // System.out.println("Creating Connection to Database...");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            //if (!connection.isClosed())
                //System.out.println("Connected to Database.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (!connection.isClosed() && connection != null) {
                        connection.close();
                        if (connection.isClosed())
                            System.out.println("Connection to Database closed");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void createDB() {
        try {
            File f = new File(DB_PATH);
            if (f.exists()) f.delete();
            else f.createNewFile();

            initDBConnection();
            Statement stmt = connection.createStatement(); // new Statement for queries

            /* Drop all tables */
            stmt.executeUpdate("DROP TABLE IF EXISTS artist");
            stmt.executeUpdate("DROP TABLE IF EXISTS album");
            stmt.executeUpdate("DROP TABLE IF EXISTS song");

            /* Create artist table */
            stmt.executeUpdate("CREATE TABLE artist" +
                    "(id INTEGER PRIMARY KEY," +
                    " artist_name varchar(32) DEFAULT NULL UNIQUE" +
                    ")");

            /* Create album table */
            stmt.executeUpdate("CREATE TABLE album" +
                    "(id INTEGER PRIMARY KEY," +
                    " album_name varchar(32) DEFAULT NULL," +
                    " artist bigint(20) NOT NULL DEFAULT 1," +
                    " release varchar(32) DEFAULT NULL," +
                    " UNIQUE(album_name, artist)" +
                    " FOREIGN KEY (artist) REFERENCES artist (id)" +
                    ")");

            /* Create song table */
            stmt.executeUpdate("CREATE TABLE song" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " song_name varchar(32) DEFAULT NULL," +
                    " artist bigint(20) NOT NULL DEFAULT 1," +
                    " album bigint(20) NOT NULL DEFAULT 1," +
                    " genre varchar(32) DEFAULT NULL," +
                    " rating tinyint(4) DEFAULT NULL," +
                    " fileLocation varchar(32) DEFAULT NULL," +
                    " length int(11) DEFAULT NULL," +
                    " FOREIGN KEY (artist) REFERENCES artist (id)," +
                    " FOREIGN KEY (album) REFERENCES album (id)" +
                    ")");

            /* Fill artist table with default value */
            stmt.executeUpdate("INSERT INTO artist (artist_name) values('Unknown Artist')");

            /* Fill album table with default value */
            stmt.executeUpdate("INSERT INTO album (album_name, artist, release) values('Unknown Album', 1, 0)");

            System.out.println("Database setup successfull.");
            connection.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        } catch (java.io.IOException ex){
            ex.printStackTrace();
        }
    }

    public static void createFromDirectory(String path){
        createDB();
        addFromDirectory(path);
    }

    public static void addFromDirectory(String path) {
        try {
            System.out.println("\nLoading songs...\n");
            initDBConnection();
            ArrayList<String> list = getAllFiles(path);

            Statement stmt = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement("INSERT OR IGNORE INTO artist (artist_name) values(?)");
            PreparedStatement ps2 = connection.prepareStatement("INSERT OR IGNORE INTO album (album_name, artist, release) values(?, ?, ?)");
            PreparedStatement ps3 = connection.prepareStatement("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values (?, ?, ?, ?, ?, ?, ?)");
/*
            // single artists
            for (int i = 0; i < list.size(); i++) {
                try {
                    AudioFile f = AudioFileIO.read(new File(list.get(i)));
                    Tag tag = f.getTag();

                    if(!f.getFile().getName().endsWith(".wav")) {
                        String artistName = null;
                        if (tag != null) artistName = tag.getFirst(FieldKey.ARTIST);

                        if (artistName == null || artistName.isEmpty()) continue;
                            // add artist to artist table
                        else {
                            ps.setString(1, artistName);
                            ps.addBatch();
                        }
                    }
                } catch (InvalidAudioFrameException e){
                    continue;
                }
            }
            ps.executeBatch();*/

            // album artists
            for (int i = 0; i < list.size(); i++) {
                try {
                    AudioFile f = AudioFileIO.read(new File(list.get(i)));
                    Tag tag = f.getTag();

                    if(!f.getFile().getName().endsWith(".wav")) {
                        String artistName = null;
                        if (tag != null) artistName = tag.getFirst(FieldKey.ALBUM_ARTIST);

                        if (artistName == null || artistName.isEmpty())
                            if (tag != null) artistName = tag.getFirst(FieldKey.ARTIST);

                        if (artistName == null || artistName.isEmpty()) continue;
                            // add artist to artist table
                        else {
                            ps.setString(1, artistName);
                            ps.addBatch();
                        }
                    }
                } catch (InvalidAudioFrameException e){
                    continue;
                }
            }
            ps.executeBatch();
            ps.close();

            ResultSet rs = stmt.executeQuery("SELECT id, artist_name FROM artist");
            while (rs.next()) System.out.println(rs.getInt("id") + ": " + rs.getString("artist_name"));

            for (int i = 0; i < list.size(); i++) {
                try {
                    AudioFile f = AudioFileIO.read(new File(list.get(i)));
                    Tag tag = f.getTag();

                    if(!f.getFile().getName().endsWith(".wav")) {
                        String albumName = null;
                        String artistName = null;
                        if (tag != null) albumName = tag.getFirst(FieldKey.ALBUM);
                        if (albumName == null || albumName.isEmpty()) albumName = "Unknown Album";
                        if (tag != null) artistName = tag.getFirst(FieldKey.ALBUM_ARTIST).replace("'", "''");
                        if ((artistName == null || artistName.isEmpty()) && tag != null)
                            artistName = tag.getFirst(FieldKey.ARTIST).replace("'", "''");
                        if (artistName == null || artistName.isEmpty()) artistName = "Unknown Artist";
                        int artistID = stmt.executeQuery("SELECT id FROM artist WHERE (artist_name = '" + artistName + "')").getInt("id");
                        String release = null;
                        if (tag != null && tag.getFirst(FieldKey.YEAR).matches(".*\\d.*")) release = tag.getFirst(FieldKey.YEAR);

                        // add album to album table
                        ps2.setString(1, albumName);
                        ps2.setInt(2, artistID);
                        ps2.setString(3, release);
                        ps2.addBatch();
                    }
                } catch (InvalidAudioFrameException e){
                    continue;
                }
            }
            ps2.executeBatch();
            ps2.close();

            rs = stmt.executeQuery("SELECT id, album_name, release FROM album");
            while (rs.next()) System.out.println(rs.getInt("id") + ": " + rs.getString("album_name")+ ", " + rs.getInt("release"));


            for (int i = 0; i < list.size(); i++) {
                String filePath = list.get(i);
                PreparedStatement test = connection.prepareStatement("SELECT id FROM song WHERE (fileLocation='" + filePath.replace("'", "''") + "')");
                ResultSet testRes = test.executeQuery();

                if (!testRes.next()) { // no duplicates
                    try {
                        AudioFile f = AudioFileIO.read(new File(filePath));
                        Tag tag = f.getTag();

                        if (!f.getFile().getName().endsWith(".wav")) {
                            AudioHeader audioheader = f.getAudioHeader();
                            String albumName = null;
                            String artistName = null;
                            String songTitle = null;
                            String genre = null;
                            int length = 0;

                            if (tag != null) albumName = tag.getFirst(FieldKey.ALBUM).replace("'", "''");
                            if (albumName == null || albumName.isEmpty()) albumName = "Unknown Album";
                            if (tag != null) artistName = tag.getFirst(FieldKey.ALBUM_ARTIST).replace("'", "''");
                            if ((artistName == null || artistName.isEmpty()) && tag != null)
                                artistName = tag.getFirst(FieldKey.ARTIST).replace("'", "''");
                            if (artistName == null || artistName.isEmpty()) artistName = "Unknown Artist";
                            int artistID = stmt.executeQuery("SELECT id FROM artist WHERE (artist_name = '" + artistName + "')").getInt("id");
                            int albumID = stmt.executeQuery("SELECT id FROM album WHERE (album_name = '" + albumName + "')").getInt("id");

                            if (tag != null) songTitle = tag.getFirst(FieldKey.TITLE);
                            if (tag != null) genre = tag.getFirst(FieldKey.GENRE);
                            if (audioheader != null) length = audioheader.getTrackLength();

                            if (songTitle == null || songTitle.isEmpty()) songTitle = f.getFile().getName();

                            // add song to song table
                            ps3.setString(1, songTitle);
                            ps3.setInt(2, artistID);
                            ps3.setInt(3, albumID);
                            ps3.setString(4, genre);
                            ps3.setInt(5, 5);
                            ps3.setString(6, filePath);
                            ps3.setInt(7, length);
                            ps3.addBatch();

                            // System.out.println(songTitle + ", " + artistName);
                        } else {
                            ps3.setString(1, f.getFile().getName());
                            ps3.setInt(2, 1);
                            ps3.setInt(3, 1);
                            ps3.setString(4, "Unknown");
                            ps3.setInt(5, 5);
                            ps3.setString(6, filePath);
                            ps3.setInt(7, 0);
                            ps3.addBatch();
                        }
                    } catch (InvalidAudioFrameException e) { // only wav
                        ps3.setString(1, new File(filePath).getName());
                        ps3.setInt(2, 1);
                        ps3.setInt(3, 1);
                        ps3.setString(4, "Unknown");
                        ps3.setInt(5, 5);
                        ps3.setString(6, filePath);
                        ps3.setInt(7, 0);
                        ps3.addBatch();
                    }
                } else {
                    System.out.println("Duplicate detected: " + filePath);
                }
            }
            ps3.executeBatch();
            ps3.close();

            System.out.println("Loaded " + list.size() + " songs.");
            stmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> getAllFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        ArrayList<String> list = new ArrayList<>();

        for(File f : files)
        {
            if (!f.isDirectory())
            {
                for (int x = 0; x < BangerVars.FILE_EXTENSIONS.length; x++)
                    if (f.getAbsolutePath().endsWith(BangerVars.FILE_EXTENSIONS[x])) list.add(f.getAbsolutePath());
            } else {
                ArrayList<String> temp = getAllFiles(path + "/" + f.getName());
                for (int i = 0; i < temp.size(); i++)
                    list.add(temp.get(i));
            }
        }
        return list;
    }

    public static ObservableList<Song> shuffleAllFiles() {
        try{
            initDBConnection();

            List<Song> list = new ArrayList<>();
            ObservableList<Song> result = FXCollections.observableList(list);

            Statement stmt = connection.createStatement();
            ResultSet rs;

            rs = stmt.executeQuery("SELECT *, album.album_name, artist.artist_name " +
                    "FROM song " +
                    "INNER JOIN album ON (song.album = album.id) " +
                    "INNER JOIN artist ON (song.artist = artist.id)" +
                    "ORDER BY RANDOM()");

            int total = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("song_name");
                String artist = rs.getString("artist_name");
                String album = rs.getString("album_name");
                String genre = rs.getString("genre");
                byte rating = rs.getByte("rating");
                String fileLocation = rs.getString("fileLocation");
                int length = rs.getInt("length");
                result.add(new Song(id, name, artist, album, genre, rating, fileLocation, length));
                total++;
            }
            System.out.println("Total songs: " + total);
            connection.close();
            return result;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<Song> getAllSongs() {
        try{
            initDBConnection();

            List<Song> list = new ArrayList<>();
            ObservableList<Song> result = FXCollections.observableList(list);

            Statement stmt = connection.createStatement();
            ResultSet rs;

            rs = stmt.executeQuery("SELECT *, album.album_name, artist.artist_name " +
                    "FROM song " +
                    "INNER JOIN album ON (song.album = album.id) " +
                    "INNER JOIN artist ON (song.artist = artist.id)");

            int total = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("song_name");
                String artist = rs.getString("artist_name");
                String album = rs.getString("album_name");
                String genre = rs.getString("genre");
                byte rating = rs.getByte("rating");
                String fileLocation = rs.getString("fileLocation");
                int length = rs.getInt("length");
                result.add(new Song(id, name, artist, album, genre, rating, fileLocation, length));
                total++;
            }
            System.out.println("Total songs: " + total);
            connection.close();
            return result;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<Album> getAllAlbums() {
        try {
            initDBConnection();

            ObservableList<Album> albums = FXCollections.observableArrayList();

            Statement s = connection.createStatement();
            ResultSet rs;

            rs = s.executeQuery("SELECT * FROM album " +
                                "INNER JOIN artist ON (album.artist = artist.id)");

            while(rs.next()) {
                albums.add(new Album(
                   rs.getInt("id"),
                    rs.getString("album_name"),
                    rs.getString("artist_name"),
                    rs.getInt("release")
                ));
            }

            connection.close();
            return albums;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<Artist> getAllArtists() {
        try {
            initDBConnection();

            ObservableList<Artist> artists = FXCollections.observableArrayList();

            Statement s = connection.createStatement();
            ResultSet rs;

            rs = s.executeQuery("SELECT * FROM artist");

            while(rs.next()) {
                artists.add(new Artist(
                    rs.getInt("id"),
                    rs.getString("artist_name")
                ));
            }

            connection.close();
            return artists;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<Song> getSongsFrom(Artist artist) {
        try {
            initDBConnection();

            ObservableList<Song> songs = FXCollections.observableArrayList();

            Statement s = connection.createStatement();
            ResultSet rs;

            rs = s.executeQuery("select * from song " +
                    "inner join album on song.album = album.id " +
                    "inner join artist on song.artist = artist.id " +
                    "where artist.id = " + artist.getId());

            while(rs.next()) {
                songs.add(new Song(
                        rs.getInt("id"),
                        rs.getString("song_name"),
                        rs.getString("artist_name"),
                        rs.getString("album_name"),
                        rs.getString("genre"),
                        rs.getInt("rating"),
                        rs.getString("filelocation"),
                        rs.getInt("length")
                ));
            }

            connection.close();
            return songs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<Song> getSongsFrom(Album album) {
        try {
            initDBConnection();

            ObservableList<Song> songs = FXCollections.observableArrayList();

            Statement s = connection.createStatement();
            ResultSet rs;

            rs = s.executeQuery("select * from song " +
                                "inner join album on song.album = album.id " +
                                "inner join artist on song.artist = artist.id " +
                                "where album.id = " + album.getId());

            while(rs.next()) {
                songs.add(new Song(
                    rs.getInt("id"),
                    rs.getString("song_name"),
                    rs.getString("artist_name"),
                    rs.getString("album_name"),
                    rs.getString("genre"),
                    rs.getInt("rating"),
                    rs.getString("filelocation"),
                    rs.getInt("length")
                ));
            }

            connection.close();
            return songs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<Album> getAlbumsFrom(Artist artist) {
        try {
            initDBConnection();

            ObservableList<Album> albums = FXCollections.observableArrayList();

            Statement s = connection.createStatement();
            ResultSet rs;

            rs = s.executeQuery("select * from album " +
                                "inner join artist on album.artist = artist.id " +
                                "where artist.id = " + artist.getId());

            while(rs.next()) {
                albums.add(new Album(
                    rs.getInt("id"),
                    rs.getString("album_name"),
                    rs.getString("artist_name"),
                    rs.getInt("release")
                ));
            }

            connection.close();
            return albums;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<Song> getSongsFromPlaylist(String playlist){
        try {
            initDBConnection();
            PlaylistItem[] items = PlaylistManager.getItems(playlist);

            ObservableList<Song> songs = FXCollections.observableArrayList();

            PreparedStatement s = connection.prepareStatement("SELECT *, album.album_name, artist.artist_name " +
                    "FROM song " +
                    "INNER JOIN album ON (song.album = album.id) " +
                    "INNER JOIN artist ON (song.artist = artist.id) " +
                    "WHERE (song.song_name = ?) " +
                    "AND (song.length = ?)");
            ResultSet rs;

            for (int i = 0; i < items.length; i++) {
                s.setString(1, items[i].getName());
                s.setInt(2, items[i].getLength());

                rs = s.executeQuery();
                int id = rs.getInt("id");
                String name = rs.getString("song_name");
                String artist = rs.getString("artist_name");
                String album = rs.getString("album_name");
                String genre = rs.getString("genre");
                byte rating = rs.getByte("rating");
                String fileLocation = rs.getString("fileLocation");
                int length = rs.getInt("length");

                songs.add(new Song(id, name, artist, album, genre, rating, fileLocation, length));
            }

            connection.close();
            return songs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<Song> searchFor(String key){
        try {
            initDBConnection();

            ObservableList<Song> songs = FXCollections.observableArrayList();
            Statement s = connection.createStatement();
            ResultSet rs;

            rs = s.executeQuery("SELECT *, album.album_name, artist.artist_name " +
                    "FROM song " +
                    "INNER JOIN album ON (song.album = album.id) " +
                    "INNER JOIN artist ON (song.artist = artist.id) " +
                    "WHERE (song.song_name LIKE '%" + key + "%') " +
                    "OR (album.album_name LIKE '%" + key + "%')" +
                    "OR (artist.artist_name LIKE '%" + key + "%')");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("song_name");
                String artist = rs.getString("artist_name");
                String album = rs.getString("album_name");
                String genre = rs.getString("genre");
                byte rating = rs.getByte("rating");
                String fileLocation = rs.getString("fileLocation");
                int length = rs.getInt("length");

                songs.add(new Song(id, name, artist, album, genre, rating, fileLocation, length));
            }

            connection.close();
            return songs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteSongs(Song[] songs){
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Wirklich löschen?");
            alert.setContentText("Wollen Sie die ausgewählten Dateien nur aus dem MusicPlayer entfernen oder auch von der Festplatte?");
            alert.setHeaderText("");
            alert.setGraphic(null);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add("banger/gui/menubar/dialog.css");
            ButtonType library = new ButtonType("MusicPlayer");
            ButtonType disc = new ButtonType("Festplatte");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(library, disc, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("");
            alert.setGraphic(null);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add("banger/gui/menubar/dialog.css");
            if (result.get() == library){
                int counter = deleteDatabaseEntries(songs);
                alert.setTitle("Songs entfernt");
                alert.setContentText(counter + " Song[s] aus dem MusicPlayer entfernt.");
                alert.show();
            } else if (result.get() == disc) {
                int counter = deleteDatabaseEntries(songs);
                deleteFiles(songs);
                alert.setTitle("Songs gelöscht");
                alert.setContentText(counter + " Song[s] von der Festplatte gelöscht.");
                alert.show();
            } else {
                alert.setTitle("Abgebrochen");
                alert.setContentText("Keine Dateien gelöscht.");
                alert.show();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static int deleteDatabaseEntries(Song[] songs) throws SQLException {
        initDBConnection();
        int counter = 0;
        PreparedStatement ps = connection.prepareStatement("DELETE FROM song WHERE id = ?");
        for (int i = 0; i < songs.length; i++){
            ps.setInt(1, songs[i].getId());
            ps.addBatch();
            counter++;
        }
        ps.executeBatch();
        ps.close();
        connection.close();
        return counter;
    }

    private static void deleteFiles(Song[] songs){
        for(int i = 0; i < songs.length; i++){
            File f = new File(songs[i].getFileLocation());
            System.out.println(f.getName());
            f.delete();
        }
    }
}