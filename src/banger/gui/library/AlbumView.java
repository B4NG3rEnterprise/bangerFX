package banger.gui.library;

import banger.Test;
import banger.audio.Album;
import banger.audio.Artist;
import banger.audio.Song;
import banger.gui.MainView;
import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.util.Iterator;


public class AlbumView extends ScrollPane implements View {

    MainView mainview;
    private ObservableList<Artist> artists;

    public AlbumView(MainView m, ObservableList<Artist> ar) {
        super();
        mainview = m;
        artists = ar;

        getStyleClass().add("scrollpane");
        getStylesheets().add("/banger/gui/library/albumview.css");

        Task<GridPane> task = new Task<GridPane>() {
            @Override
            protected GridPane call() throws Exception {
                GridPane g = new GridPane();
                g.getStyleClass().add("gridpane");
                //setAlignment(Pos.TOP_LEFT);
                //g.setGridLinesVisible(true);
                g.setHgap(10);
                g.setVgap(5);

                ColumnConstraints covercol = new ColumnConstraints(50, 100, 200);
                ColumnConstraints infocol = new ColumnConstraints(100, 150, 200);
                ColumnConstraints songcol = new ColumnConstraints(400, 450, 500);

                g.getColumnConstraints().addAll(covercol, infocol, songcol);

                //region Creation
                Image i = new Image(Test.class.getResourceAsStream("/png/Cover0.jpg"));

                Iterator<Artist> artistIterator = artists.iterator();

                int row = 0;
                while(artistIterator.hasNext()) {
                    Artist artist = artistIterator.next();
                    ObservableList<Album> albums = artist.getAlbums();

                    if (artist.getSongs().size() > 0) {
                        Label artistname = new Label(artist.getName());
                        artistname.getStyleClass().add("artist_label");
                        g.setConstraints(artistname, 0, row, 4, 1);
                        Platform.runLater(() -> g.getChildren().add(artistname));
                        row++;
                    }

                    for(Album album : albums) {
                        if (album.getSongs().size() > 0) {
                            ObservableList<Song> songs = album.getSongs();

                            ImageView cover = new ImageView(i);
                            cover.setFitWidth(100);
                            cover.setPreserveRatio(true);
                            cover.setSmooth(true);
                            cover.setCache(true);
                            g.setConstraints(cover, 0, row, 1, songs.size() > 1 ? songs.size() : 2, HPos.CENTER, VPos.TOP);

                            Label albumName = new Label(album.getAlbumName());
                            albumName.getStyleClass().add("album_label");
                            g.setConstraints(albumName, 1, row);

                            Label albumRelease = new Label(String.valueOf(album.getRelease()));
                            albumRelease.getStyleClass().add("album_label");
                            g.setConstraints(albumRelease, 1, row + 1, 1, 1, HPos.LEFT, VPos.TOP);

                            TableView<Song> table = new TableView(album.getSongs());
                            table.getStyleClass().addAll("song_table");

                            TableColumn numberCol = new TableColumn("#");
                            numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, Number>, ObservableValue<Number>>() {
                                @Override
                                public ObservableValue<Number> call(TableColumn.CellDataFeatures<Song, Number> p) {
                                    return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1);
                                }
                            });
                            TableColumn song_name = new TableColumn("Name");
                            song_name.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));
                            TableColumn genre = new TableColumn("Genre");
                            genre.setCellValueFactory(new PropertyValueFactory<Song, String>("genre"));
                            TableColumn length = new TableColumn("Length");
                            length.setCellValueFactory(new PropertyValueFactory<Song, Integer>("length"));

                            table.widthProperty().addListener(new ChangeListener<Number>()
                            {
                                @Override
                                public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
                                {
                                    //Don't show header
                                    Pane header = (Pane) table.lookup("TableHeaderRow");
                                    if (header.isVisible()){
                                        header.setMaxHeight(0);
                                        header.setMinHeight(0);
                                        header.setPrefHeight(0);
                                        header.setVisible(false);
                                    }
                                }
                            });

                            table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems())).add(1.01));

                            table.getColumns().addAll(numberCol, song_name, genre, length);

                            table.setOnMousePressed(event -> {
                                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                                    mainview.play(table.getSelectionModel().getSelectedItem());
                                    mainview.getLibrary().updateQueue(table.getSelectionModel().getSelectedItem());
                                }
                            });
                            GridPane.setHgrow(table, Priority.ALWAYS);
                            g.setConstraints(table, 2, row, 1, songs.size() > 1 ? songs.size() : 2, HPos.CENTER, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);

                            Platform.runLater(() -> g.getChildren().addAll(cover, albumName, albumRelease, table));

                            row += songs.size() > 1 ? songs.size() : 2;
                        }
                        updateValue(g);
                    }
                }

                //endregion

                return g;
            }
        };
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();

        task.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("CHANGE");
            this.setContent(newValue);
        });

        task.setOnFailed(event -> {
            System.out.println("FAILED");
            System.out.println(task.getException());
        });

        task.setOnCancelled(event -> {
            System.out.println("CANCELLED");
        });

        task.setOnSucceeded(event -> {
            System.out.println("SUCCESS");
            this.setContent(task.getValue());
        });

    }

    public void refreshData(ObservableList<Song> songs) {
        new AlbumView(mainview, artists);
    }

    public void select(Song song) {

    }

    public Song getSelectedItem() {
        return null;
    }

    public Song[] getSelectedItems(){
        return null;
    }
}

