package banger.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class CoverGetter {

	public static void main(String[] args) throws IOException {
		String artist = JOptionPane.showInputDialog("Please provide an artist name");
		String title = JOptionPane.showInputDialog("Please provide a song name");
		getLyricsBySearch(artist, title);
	}

	public static void getLyricsBySearch(String artist, String title) throws MalformedURLException, IOException {

		// prepare url for searchQuery on genius.com
		String url = artist.replace(" ", "+").toLowerCase() + "+" + title.replace(" ", "+").toLowerCase();

		// connect to genius.com with the search link
		URLConnection connection = new URL("http://genius.com/search?q=" + url).openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		connection.connect();

		// save result website into a string
		BufferedReader br = new BufferedReader(
				new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		// get the first result and save its url to a string
		Document document = Jsoup.parse(sb.toString());
		Elements results = document.getElementsByClass("song_link");
		for (int i = 0; i < results.size(); i++) {
			if (results.get(i).attr("href").contains("Spotify"))
				continue;
			else {
				url = results.get(i).attr("href");
				break;
			}
		}

		// connect to genius.com with the url from the results
		connection = new URL(url).openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		connection.connect();

		// save lyric website into a string
		br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
		sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		// get the lyrics
		document = Jsoup.parse(sb.toString());
		Elements lyrics = document.getElementsByAttribute("property");
		lyrics = lyrics.attr("property", "og:image");

		File f = new File(System.getProperty("user.dir") + "\\image.jpg");

		try {
			URL link = null;

			for (int i = 0; i < lyrics.size(); i++) {
				if (lyrics.get(i).attr("content").startsWith("https://images.rapgenius.com")) {
					link = new URL(lyrics.get(i).attr("content"));

					InputStream is = link.openStream();
					OutputStream os = new FileOutputStream(f);

					byte[] b = new byte[2048];
					int length;

					while ((length = is.read(b)) != -1) {
						os.write(b, 0, length);
					}

					is.close();
					os.close();

					// display lyrics
					JLabel label = new JLabel();
					label.setIcon(new ImageIcon(f.getAbsolutePath()));
					JOptionPane.showMessageDialog(null, label, "Cover for " + title + "by " + artist,
							JOptionPane.INFORMATION_MESSAGE);
					break;
				}
			}
		} catch (NullPointerException e) {

		}

	}
}