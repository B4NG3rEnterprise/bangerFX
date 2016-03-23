package banger.gui.library;

import banger.audio.Album;
import banger.audio.Artist;
import banger.audio.Song;
import banger.gui.MainView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;


public class AlbumView extends TableView<Album> implements View {

    MainView mainview;
    private ObservableList<Artist> artists;

    public AlbumView(MainView m, ObservableList<Artist> ar) {
        //super();
        mainview = m;
        artists = ar;

        TableColumn cover = new TableColumn<Album, Image>();
        cover.setCellValueFactory(new PropertyValueFactory<Album, Image>("cover"));
        cover.setCellFactory(new Callback<TableColumn<Album, Image>, TableCell<Album, Image>>() {
            @Override
            public TableCell<Album, Image> call(TableColumn<Album, Image> param) {
                return new TableCell<Album, Image>() {
                    @Override
                    protected void updateItem(Image item, boolean empty) {
                        if (item != null) {
                            ImageView i = new ImageView(item);
                            i.setFitWidth(100);
                            i.setPreserveRatio(true);
                            setGraphic(i);
                        }
                    }
                };
            }
        });

        TableColumn info = new TableColumn<Album, String>();
        info.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Album, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Album, String> param) {
                return new SimpleStringProperty(param.getValue().getAlbumName() + "\n" + param.getValue().getArtist() + "\n" + param.getValue().getRelease());
            }
        });
        info.setCellFactory(new Callback<TableColumn<Album, String>, TableCell<Album, String>>() {
            @Override
            public TableCell<Album, String> call(TableColumn<Album, String> param) {
                return new TableCell<Album, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (item != null) {
                            ObservableList<String> ot = FXCollections.observableArrayList(item.split("\n"));
                            ListView list = new ListView(ot);
                            setGraphic(list);
                        }
                    }
                };
            }
        });

        TableColumn songs = new TableColumn<Album, String>();
        songs.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Album, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Album, String> param) {
                ObservableList<Song> songs = param.getValue().getSongs();
                StringBuilder sb = new StringBuilder();

                for(Song s : songs) {
                    sb.append(s.getId() + "\t" + s.getName() + "\t" + s.getArtist() + "\t" + s.getAlbum() + "\t" + s.getGenre() + "\t" + s.getRating() + "\t" + s.getFileLocation() + "\t" + s.getLength() + "\n");
                }

                return new SimpleStringProperty(sb.toString());
            }
        });
        songs.setCellFactory(new Callback<TableColumn<Album, String>, TableCell<Album, String>>() {
            @Override
            public TableCell<Album, String> call(TableColumn<Album, String> param) {
                return new TableCell<Album, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (item != null) {
                            ObservableList songs = FXCollections.observableArrayList();

                            for(String s : item.split("\n")) {
                                String[] ss = s.split("\t");
                                if (ss[0].length() > 0)
                                    songs.add(new Song(Integer.valueOf(ss[0]),
                                            ss[1],
                                            ss[2],
                                            ss[3],
                                            ss[4],
                                            Byte.valueOf(ss[5]),
                                            ss[6],
                                            Integer.valueOf(ss[7])));
                            }

                            TableView table = new TableView();

                            TableColumn song_name = new TableColumn();
                            song_name.setCellValueFactory(
                                    new PropertyValueFactory<Song, String>("name"));
                            TableColumn genre = new TableColumn();
                            genre.setCellValueFactory(
                                    new PropertyValueFactory<Song, String>("genre"));
                            TableColumn length = new TableColumn();
                            length.setCellValueFactory(
                                    new PropertyValueFactory<Song, Integer>("length"));

                            table.getColumns().addAll(song_name, genre, length);
                            table.setItems(songs);

                            setGraphic(table);
                        }
                    }
                };
            }
        });

        getColumns().addAll(cover, info, songs);

        ObservableList<Album> albums = FXCollections.observableArrayList();
        for(Artist a : artists)
            albums.addAll(a.getAlbums());
        setItems(albums);

        setPrefHeight(600);
    }

    public void refreshData(ObservableList<Song> songs) {
        new AlbumView(mainview, artists);
    }

    public void select(Song song) {

    }

    public Song getSelectedItem() {
        return null;
    }

}
