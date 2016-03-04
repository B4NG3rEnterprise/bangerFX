/**
 * Created by Merlin on 04.03.2016.
 */
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS music;");
            stmt.executeUpdate("CREATE TABLE music (title, artist, album);");

            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO music VALUES (?, ?, ?);");

            ps.setString(1, "Light Tunnels");
            ps.setString(2, "Macklemore");
            ps.setString(3, "This Unruly Mess I've Made");
            ps.addBatch();

            ps.setString(1, "Downtown");
            ps.setString(2, "Macklemore");
            ps.setString(3, "This Unruly Mess I've Made");
            ps.addBatch();

            ps.setString(1, "Brad Pitt's Cousin");
            ps.setString(2, "Macklemore");
            ps.setString(3, "This Unruly Mess I've Made");
            ps.addBatch();

            connection.setAutoCommit(false);
            ps.executeBatch();
            connection.setAutoCommit(true);

            ResultSet rs = stmt.executeQuery("SELECT * FROM music;");
            while (rs.next()) {
                System.out.println("Title = " + rs.getString("title"));
                System.out.println("Artist = " + rs.getString("artist"));
                System.out.println("Album = " + rs.getInt("album"));
            }
            rs.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
    }
}