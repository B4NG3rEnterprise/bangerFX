package banger.util; /**
 * Created by Merlin on 05.03.2016.
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class LyricsGetter {

    private LyricsGetter(){};

    /* public static void main(String[] args) throws IOException {
        String artist = JOptionPane.showInputDialog("Please provide an artist name");
        String title = JOptionPane.showInputDialog("Please provide a song name");
        getLyricsDemo(artist, title);
    }

    public static void getLyricsDemo(String artist, String title) throws MalformedURLException, IOException{

        // prepare url for searchQuery on genius.com
        String url = artist.replace(" ", "+").toLowerCase() + "+" + title.replace(" ", "+").toLowerCase();

        // connect to genius.com with the search link
        URLConnection connection = new URL("http://genius.com/search?q=" + url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();

        // save result website into a string
        BufferedReader br  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        // get the first result and save its url to a string
        Document document = Jsoup.parse(sb.toString());
        Elements results = document.getElementsByClass("song_link");
        for (int i = 0; i < results.size(); i++){
            if (results.get(i).attr("href").contains("Spotify")) continue;
            else {
                url = results.get(i).attr("href");
                break;
            }
        }

        // connect to genius.com with the url from the results
        connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();

        // save lyric website into a string
        br  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
        sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        // get the lyrics
        document = Jsoup.parse(sb.toString());
        Element lyrics = document.getElementsByClass("lyrics").first();
        lyrics.getElementsByClass("referent").unwrap(); // remove <a/>-Tags

        // display lyrics
        JLabel label = new JLabel("<html>" + lyrics.toString() + "</html>");
        label.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(null, label, "Lyrics for " + title + " by " + artist,
                JOptionPane.INFORMATION_MESSAGE);
    }
    */

    public static String getLyrics(String artist, String title) {
        try {
            // prepare url for searchQuery on genius.com
            String url = artist.replace(" ", "+").toLowerCase() + "+" + title.replace(" ", "+").toLowerCase();

            // connect to genius.com with the search link
            URLConnection connection = new URL("http://genius.com/search?q=" + url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            // save result website into a string
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            // get the first result and save its url to a string
            Document document = Jsoup.parse(sb.toString());
            Elements results = document.getElementsByClass("song_link");
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).attr("href").contains("Spotify")) continue;
                else {
                    url = results.get(i).attr("href");
                    break;
                }
            }

            // connect to genius.com with the url from the results
            connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            // save lyric website into a string
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            // get the lyrics
            document = Jsoup.parse(sb.toString());
            Element lyrics = document.getElementsByClass("lyrics").first();
            lyrics.getElementsByClass("referent").unwrap(); // remove <a/>-Tags
            String result = lyrics.toString().replace("<br>", "\n").replaceAll("\\<.*?>","");

            return result;
        } catch (Exception e){
            return "Tut uns leid! Es scheint, als könnten die Lyrics nicht abgerufen werden. Überprüfe bitte deine Interneverbindung!";
        }
    }
}