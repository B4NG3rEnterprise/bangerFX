package banger.util;

import banger.audio.data.Song;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.StageStyle;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class PlaylistManager {

    private static final String PLAYLIST_DIR = "res/playlists/";

    private PlaylistManager(){}

    public static void createPlaylist(String name, Song[] songs){
        File playlist = new File(PLAYLIST_DIR + name + ".m3u");
        playlist.getParentFile().mkdirs();

        if (!playlist.exists() && songs != null){
            createFile(playlist);
            addToPlaylist(name, songs);
        }
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

    public static PlaylistItem[] getItems(String playlist){
        ArrayList<PlaylistItem> songs = new ArrayList<>();
        try (BufferedReader reader
                     = new BufferedReader(new FileReader(PLAYLIST_DIR + playlist + ".m3u"))){
            String currentLine;
            if (reader.readLine().equals("#EXTM3U")) {
                while ((currentLine = reader.readLine()) != null) {
                    if(currentLine.startsWith("#EXTINF")){
                        int length = Integer.parseInt(currentLine.substring(currentLine.indexOf(":") + 1, currentLine.indexOf(",")));
                        String name = currentLine.substring(currentLine.indexOf(",") + 1);
                        String fileLocation = reader.readLine();
                        songs.add(new PlaylistItem(name, fileLocation, length));
                    }
                }
            } else {
                System.out.println("Thats no music file");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        PlaylistItem[] result = new PlaylistItem[songs.size()];
        result = songs.toArray(result);

        return result;
    }

    public static void deletePlaylist(String name){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Playlist löschen?");
        alert.setContentText("Wollen Sie die Playlist wirklich löschen?");
        alert.setHeaderText("");
        alert.setGraphic(null);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.getDialogPane().getStylesheets().add("banger/gui/menubar/dialog.css");
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDefaultButton(false);
        EventHandler<KeyEvent> fireOnEnter = event -> {
            if (KeyCode.ENTER.equals(event.getCode())
                    && event.getTarget() instanceof Button) {
                ((Button) event.getTarget()).fire();
            }
        };
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getButtonTypes().stream()
                .map(dialogPane::lookupButton)
                .forEach(button ->
                        button.addEventHandler(
                                KeyEvent.KEY_PRESSED,
                                fireOnEnter
                        )
                );

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                File playlist = new File(PLAYLIST_DIR + name + ".m3u");
                deleteFile(playlist);
            }
        });
    }

    private static String format(Song s){
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTINF:" + s.getLength() + "," + s.getName());
        String loc = s.getFileLocation();
        sb.append("\n" + loc.substring(loc.lastIndexOf("\\") + 1));
        return sb.toString();
    }

    private static void createFile(File f){
        List<String> lines = Arrays.asList("#EXTM3U");
        Path file = Paths.get(f.toURI());
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void exportPlaylists(String destination){
        try {
            String[] playlists = getPlaylists();

            for (int i = 0; i < playlists.length; i++) {
                File source = new File(PLAYLIST_DIR + playlists[i] + ".m3u");
                File target = new File(destination + "/playlists/" + playlists[i] + ".m3u");
                target.mkdirs();
                Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void importPlaylists(List<File> lists){
        try {
            for (int i = 0; i < lists.size(); i++) {
                File source = lists.get(i);
                File target = new File(PLAYLIST_DIR + lists.get(i).getName());
                Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
