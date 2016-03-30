package banger.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class CoverGetter {

	public static void main(String[] args) throws IOException {
		String artist = JOptionPane.showInputDialog("Please provide an artist name");
		String title = JOptionPane.showInputDialog("Please provide a song name");
		getCoverMusixMatch(artist, title);
	}

	private final static boolean DEBUG = true;

	private CoverGetter(){}


	public static ImageIcon getCoverMusixMatch(String art, String tit) {
		try {
			String artist = "";
			if (!art.isEmpty()) artist = art.replaceAll("\\(.+?\\)", "").replaceAll("\\[.+?\\]", "").replaceAll("[^A-Za-z0-9'.\\s]","");
			String title = tit.replaceAll("\\(.+?\\)", "").replaceAll("\\[.+?\\]", "").replaceAll("[^A-Za-z0-9'.\\s]","");

			// prepare url for searchQuery on genius.com
			String url = title.toLowerCase() + " " + artist.toLowerCase();
			url = url.trim().replaceAll("[^\\S\\r\\n]+", " ").replaceAll(" ", "%20");

			// connect to genius.com with the search link
			URLConnection connection = new URL("https://www.musixmatch.com/search/" + url + "/tracks").openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
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
			Elements results = document.getElementsByClass("title");
			url = results.get(0).attr("href");


			// connect to genius.com with the url from the results
			connection = new URL("https://www.musixmatch.com/" + url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.connect();

			// save lyric website into a string
			br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
			sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line + "<br>");
			}

			// get the lyrics
			document = Jsoup.parse(sb.toString());
			Element cover = document.getElementsByAttributeValue("property", "og:image").first();

            URL link = new URL(cover.attr("content"));
			InputStream is = link.openStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

			byte[] buffer = new byte[2048];
			int length;

			while ((length = is.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
			is.close();

            ImageIcon result = new ImageIcon(bos.toByteArray());

            JLabel label = new JLabel();
            label.setIcon(result);
            JOptionPane.showMessageDialog(null, label, "Cover for " + title + " by " + artist,
                    JOptionPane.INFORMATION_MESSAGE);

			return result;
		} catch (SocketTimeoutException e) {
			return null;
		} catch(UnknownHostException uhe){
			return null;
		} catch (Exception e){
			return null;
		}
	}
}