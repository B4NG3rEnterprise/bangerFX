package banger.util;

import banger.audio.Song;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class PlaylistManager {

    private static final String PLAYLIST_DIR = "res/playlists/";
    private final static Charset ENCODING = StandardCharsets.UTF_8;


    private PlaylistManager(){}

    public static void createPlaylist(String name, Song[] songs){
        File playlist = new File(PLAYLIST_DIR + name + ".m3u");
        // TODO make alert

        playlist.getParentFile().mkdirs();

        if (!playlist.exists())
            addToPlaylist(name, songs);
        else {
            int ext = 1;
            File playlistExt = new File(PLAYLIST_DIR + name + "(" + ext + ")" + ".m3u");
            while (playlistExt.exists()) {
                playlistExt = new File(PLAYLIST_DIR + name + "(" + ext + ")" + ".m3u");
            }
            String nameExt = playlistExt.getName();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Playlist existiert");
            alert.setContentText("Playlist existiert bereits. Die aktuelle Playlist wird als " + nameExt + " gepeichert.");
            alert.setHeaderText("");
            alert.setGraphic(null);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add("banger/gui/menubar/dialog.css");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    addToPlaylist(nameExt, songs);
                }
            });
        }
    }

    public static void addToPlaylist(String name, Song[] songs) {
        if (songs != null) {
            File playlist = new File(PLAYLIST_DIR + name + ".m3u");

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(playlist, true)))) {
                for (Song s : songs) {
                    writer.println(format(s));
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("No songs to add");
        }
    }

    public static String[] getPlaylists(){
        File[] files = new File(PLAYLIST_DIR).listFiles(new FilenameFilter() {
            public boolean accept(File directory, String fileName) {
                return fileName.endsWith(".m3u");
            }
        });
        if (files != null) {
            String[] result = new String[files.length];
            for (int i = 0; i < files.length; i++)
                result[i] = files[i].getName().replace(".m3u", "");
            return result;
        } else return null;
    }

    public static void deletePlaylist(String name){
        File playlist = new File(PLAYLIST_DIR + name + ".m3u");
        deleteFile(playlist);
    }

    private static String format(Song s){
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U");
        sb.append("\n#EXTINF:" + s.getLength() + "," + s.getName());
        String loc = s.getFileLocation();
        sb.append("\n" + loc.substring(loc.lastIndexOf("/") + 1));
        return sb.toString();
    }

    private static void deleteFile(File f){
        try {
            if (f.exists()){
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
