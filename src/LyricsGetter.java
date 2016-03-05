/**
 * Created by Merlin on 05.03.2016.
 */
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class LyricsGetter {
    public static void main(String[] args) throws IOException {
        String artist = JOptionPane.showInputDialog("Please provide an artist name");
        String title = JOptionPane.showInputDialog("Please provide a song name");
        getLyricsBySearch(artist, title);
    }

    public static void getLyricsBySearch(String artist, String title) throws IOException{

        // prepare url for searchQuery on genius.com
        String url = artist.replace(" ", "+").toLowerCase() + "+" + title.replace(" ", "-").toLowerCase();

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
        url = document.getElementsByClass("song_link").first().attr("href");

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

        // extract the lyrics
        document = Jsoup.parse(sb.toString());
        Element lyrics = document.getElementsByClass("lyrics").first();
        lyrics.getElementsByClass("referent").unwrap(); // remove <a/>-Tags

        // display lyrics in JOptionPane
        JLabel label = new JLabel("<html>" + lyrics.toString() + "</html>");
        label.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(null, label, "Lyrics for " + title + " by " + artist,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
