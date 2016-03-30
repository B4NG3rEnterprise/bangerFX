package banger.gui.coverview;

import banger.audio.data.Song;
import banger.gui.MainView;
import banger.util.CoverGetter;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CoverView extends HBox {

    @FXML
    Label title;
    @FXML
    Label artist;
    @FXML
    Label album;
    @FXML
    ImageView cover;

    private MainView mainView;

    public CoverView(MainView mainView) {
        this.mainView = mainView;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("coverView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cover.setImage(new Image("file:/" + new File("res/png/Cover0.jpg").getAbsolutePath()));

    }

    public void updateView(Song currentlyPlaying) {

        title.setText(currentlyPlaying.getName());
        artist.setText(currentlyPlaying.getArtist());
        album.setText(currentlyPlaying.getAlbum());


        AudioFile f = null;

        try {
            f = AudioFileIO.read(new File(currentlyPlaying.getFileLocation()));
        } catch (Exception e) {
        }

        Image img = (new Image("file:/" + new File("res/png/Cover0.jpg").getAbsolutePath()));

        try {
            img = SwingFXUtils.toFXImage((BufferedImage) f.getTag().getFirstArtwork().getImage(), null);
            cover.setImage(img);
        } catch (Exception e) {
            System.out.println("Failed to catch artwork from songfile.");
            cover.setImage(img);
        }

    }

}
