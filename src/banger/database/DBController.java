package banger.database;


import banger.audio.Song;
import banger.util.BangerVars;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

            System.out.println("Database setup successfull.");
            connection.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
    }

    public static void setContent(String path){
        try {
            System.out.println("\nLoading songs...\n");

            initDBConnection();
            createDB();

            initDBConnection();
            Statement stmt = connection.createStatement();
            ArrayList<String> list = getAllFiles(path);

            /* Fill artist table with default value */
            stmt.executeUpdate("INSERT INTO artist (artist_name) values('Unknown Artist')");

            /* Fill album table with default value */
            stmt.executeUpdate("INSERT INTO album (album_name, artist) values('Unknown Album', 1)");

            PreparedStatement ps = connection.prepareStatement("INSERT OR IGNORE INTO artist (artist_name) values(?)");
            PreparedStatement ps2 = connection.prepareStatement("INSERT OR IGNORE INTO album (album_name, artist, release) values(?, ?, ?)");
            PreparedStatement ps3 = connection.prepareStatement("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values (?, ?, ?, ?, ?, ?, ?)");


            // single artists
            for (int i = 0; i < list.size(); i++) {
                try {
                    AudioFile f = new AudioFile();
                    f = AudioFileIO.read(new File(list.get(i)));
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
            ps.executeBatch();

            // album artists
            for (int i = 0; i < list.size(); i++) {
                try {
                    AudioFile f = AudioFileIO.read(new File(list.get(i)));
                    Tag tag = f.getTag();

                    if(!f.getFile().getName().endsWith(".wav")) {
                        String artistName = null;
                        if (tag != null) artistName = tag.getFirst(FieldKey.ALBUM_ARTIST);

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
                try{
                    String filePath = list.get(i);


                    AudioFile f = AudioFileIO.read(new File(filePath));
                    Tag tag = f.getTag();

                    if(!f.getFile().getName().endsWith(".wav")) {
                        AudioHeader audioheader = f.getAudioHeader();
                        String albumName = null;
                        String artistName = null;
                        String songTitle = null;
                        String genre = null;
                        int length = 0;

                        if (tag != null) albumName = tag.getFirst(FieldKey.ALBUM).replace("'", "''");
                        if (albumName == null || albumName.isEmpty()) albumName = "Unknown Album";
                        if (tag != null) artistName = tag.getFirst(FieldKey.ARTIST).replace("'", "''");
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

                        // System.out.println(list.get(i));
                        System.out.println(songTitle + ", " + artistName);
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
                } catch (InvalidAudioFrameException e){
                    String filePath = list.get(i);
                    ps3.setString(1, new File(filePath).getName());
                    ps3.setInt(2, 1);
                    ps3.setInt(3, 1);
                    ps3.setString(4, "Unknown");
                    ps3.setInt(5, 5);
                    ps3.setString(6, filePath);
                    ps3.setInt(7, 0);
                    ps3.addBatch();
                }
            }
            ps3.executeBatch();
            ps3.close();

            System.out.println("Loaded " + list.size() + " songs.");
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> getAllFiles(String path){
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

    public static ObservableList<Song> shuffleAllFiles(){
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

    public static ObservableList<Song> getAllFiles(){
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
}