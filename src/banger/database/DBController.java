package banger.database;


import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class DBController {

    private static final DBController dbcontroller = new DBController();
    private static Connection connection;
    private static final String DB_PATH = "res/" + "testdb.db";

    public static void main(String[] args) {
        DBController dbc = DBController.getInstance();
        dbc.initDBConnection();
        dbc.handleDB();
    }

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

    private void initDBConnection() {
        try {
            if (connection != null)
                return;
            System.out.println("Creating Connection to Database...");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            if (!connection.isClosed())
                System.out.println("...Connection established");
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

    private void handleDB() {
        try {
            Statement stmt = connection.createStatement(); // new Statement for queries

            /* Drop all tables */
            stmt.executeUpdate("DROP TABLE IF EXISTS artist");
            stmt.executeUpdate("DROP TABLE IF EXISTS album");
            stmt.executeUpdate("DROP TABLE IF EXISTS song");

            /* Create artist table */
            stmt.executeUpdate("CREATE TABLE artist" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " artist_name varchar(32) DEFAULT NULL" +
                    ")");

            /* Create album table */
            stmt.executeUpdate("CREATE TABLE album" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " album_name varchar(32) DEFAULT NULL," +
                    " artist bigint(20) NOT NULL," +
                    " release smallint(5) DEFAULT NULL," +
                    " FOREIGN KEY (artist) REFERENCES artist (id)" +
                    ")");

            /* Create song table */
            stmt.executeUpdate("CREATE TABLE song" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " song_name varchar(32) DEFAULT NULL," +
                    " artist bigint(20) NOT NULL," +
                    " album bigint(20) NOT NULL," +
                    " genre varchar(32) DEFAULT NULL," +
                    " rating tinyint(4) DEFAULT NULL," +
                    " fileLocation varchar(32) DEFAULT NULL," +
                    " length int(11) DEFAULT NULL," +
                    " FOREIGN KEY (artist) REFERENCES artist (id)," +
                    " FOREIGN KEY (album) REFERENCES album (id)" +
                    ")");

            /* Fill artist table */
            stmt.executeUpdate("INSERT INTO artist (artist_name) values('Macklemore and Ryan Lewis')");
            stmt.executeUpdate("INSERT INTO artist (artist_name) values('Hellberg')");

            /* Fill album table */
            stmt.executeUpdate("INSERT INTO album (album_name, artist, release) values('This Unruly Mess I''ve Made', 1, 2016)");
            stmt.executeUpdate("INSERT INTO album (album_name, artist, release) values('This Is Me EP', 2, 2015)");

            /* Fill song table */
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Light Tunnels', 1, 1, 'Rap', 5, '/music/macklemore', 500)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Downtown', 1, 1, 'Rap', 4, '/music/macklemore', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Brad Pitt''s Cousin', 1, 1, 'Rap', 3, '/music/macklemore', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Buckshot', 1, 1, 'Rap', 2, '/music/macklemore', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Growing Up', 1, 1, 'Rap', 5, '/music/macklemore', 300)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Kevin', 1, 1, 'Rap', 4, '/music/macklemore', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('St. Ides', 1, 1, 'Rap', 3, '/music/macklemore', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Need To Know', 1, 1, 'Rap', 2, '/music/macklemore', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Dance Off', 1, 1, 'Rap', 5, '/music/macklemore', 300)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Let''s Eat', 1, 1, 'Rap', 4, '/music/macklemore', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Bolo Tie', 1, 1, 'Rap', 3, '/music/macklemore', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('The Train', 1, 1, 'Rap', 2, '/music/macklemore', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('White Privilege II', 1, 1, 'Rap', 2, '/music/macklemore', 330)");

            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('A Heartbeat Away', 2, 2, 'Elektro', 5, '/music/hellberg', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('The Girl', 2, 2, 'Elektro', 5, '/music/hellberg', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Back2You', 2, 2, 'Elektro', 5, '/music/hellberg', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Wasted Summer', 2, 2, 'Elektro', 5, '/music/hellberg', 330)");
            stmt.executeUpdate("INSERT INTO song (song_name, artist, album, genre, rating, fileLocation, length) values('Love You Now', 2, 2, 'Elektro', 5, '/music/hellberg', 330)");

            ResultSet rs;

            /*
            rs = stmt.executeQuery("SELECT *, album.album_name, artist.artist_name " +
                    "FROM song " +
                    "INNER JOIN album ON (song.album = album.id) " +
                    "INNER JOIN artist ON (song.artist = artist.id)");
            */

            rs = stmt.executeQuery("SELECT *, album.album_name, artist.artist_name " +
                                "FROM song " +
                                "INNER JOIN album ON (song.album = album.id) " +
                                "INNER JOIN artist ON (song.artist = artist.id)" +
                                "WHERE artist_name='Hellberg'" +
                                "ORDER BY RANDOM()");

            while (rs.next()) {
                System.out.println("ID = " + rs.getInt("id"));
                System.out.println("Title = " + rs.getString("song_name"));
                System.out.println("Artist = " + rs.getString("artist_name"));
                System.out.println("Album = " + rs.getString("album_name"));
            }
            rs.close();

            connection.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
    }
}