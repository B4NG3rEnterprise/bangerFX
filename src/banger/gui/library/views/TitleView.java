package banger.gui.library.views;

import banger.Main;
import banger.audio.data.Album;
import banger.audio.data.Artist;
import banger.audio.data.Song;
import banger.database.DBController;
import banger.gui.MainView;
import banger.util.Rating;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;


public class TitleView extends ScrollPane implements View {

    MainView mainview;
    private ObservableList<Artist> artists;

    public TitleView(MainView m) {
        super();
        mainview = m;
        artists = DBController.getAllArtists();

        getStyleClass().add("scrollpane");
        getStylesheets().add("/banger/gui/library/views/titleview.css");

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
                Image i = new Image(Main.class.getResourceAsStream("/png/Cover0.jpg"));

                int row = 0;
                for(Artist artist : artists) {
                    ObservableList<Album> albums = artist.getAlbums();

                    //System.out.println(artist.getName() + ": " + artist.getId() + "\t" + artist.getAlbums().size());

                    if (artist.getSongs().size() > 0) {
                        Label artistname = new Label(artist.getName());
                        artistname.getStyleClass().add("artist_label");
                        g.setConstraints(artistname, 0, row, 4, 1);
                        Platform.runLater(() -> g.getChildren().add(artistname));
                        row++;
                    }

                    for(Album album : albums) {
                        //System.out.println("\t" + album.getSongs().size());

                        if (album.getSongs().size() > 0) {
                            ObservableList<Song> songs = album.getSongs();

                            ImageView cover = new ImageView(songs.get(0).getCover());
                            cover.setFitWidth(100);
                            cover.setPreserveRatio(true);
                            cover.setSmooth(true);
                            cover.setCache(true);
                            cover.getStyleClass().add("album_cover");
                            g.setConstraints(cover, 0, row, 1, songs.size() > 1 ? songs.size() : 2, HPos.CENTER, VPos.TOP);

                            Label albumName = new Label(album.getAlbumName());
                            albumName.getStyleClass().add("album_label");
                            g.setConstraints(albumName, 1, row);

                            Label albumRelease = new Label(String.valueOf(album.getRelease()));
                            albumRelease.getStyleClass().add("album_label");
                            g.setConstraints(albumRelease, 1, row + 1, 1, 1, HPos.LEFT, VPos.TOP);

                            TableView<Song> table = new TableView<Song>(album.getSongs());
                            table.getStyleClass().addAll("song_table");

                            TableColumn numberCol = new TableColumn("#");
                            numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, Number>, ObservableValue<Number>>() {
                                public ObservableValue<Number> call(TableColumn.CellDataFeatures<Song, Number> p) {
                                    return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1);
                                }
                            });
                            TableColumn songNameCol = new TableColumn("Name");
                            songNameCol.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));

                            TableColumn ratingCol = new TableColumn("Rating");
                            ratingCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
                                @Override
                                public ObservableValue<String> call (TableColumn.CellDataFeatures<Song, String> p) {
                                    return new SimpleStringProperty(p.getValue().getRating()+"\n"+p.getValue().getName()+"\n"+p.getValue().getArtist()+"\n"+p.getValue().getAlbum()+ "\n"+ p.getValue().getId());
                                }
                            });
                            ratingCol.setCellFactory(new Callback<TableColumn<Song, String>, TableCell<Song, String>>() {
                                public TableCell<Song, String> call(TableColumn<Song, String> param) {
                                    return new TableCell<Song, String>() {
                                      public void updateItem(String item, boolean empty) {
                                          if (item != null) {
                                              String[] temp = item.split("\n");
                                              try {
                                                  setGraphic(new Rating(mainview, Integer.parseInt(temp[0]), Integer.parseInt(temp[4])));
                                              } catch (Exception e) {
                                                  e.printStackTrace();
                                                  System.out.println("wrong String");
                                              }
                                          }
                                      }
                                    };
                                }
                            });

                            TableColumn lengthCol = new TableColumn("Length");
                            lengthCol.setCellValueFactory(new PropertyValueFactory<Song, Integer>("length"));


                            table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems())).add(1.01));

                            numberCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
                            songNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.7));
                            lengthCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
                            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                            table.getColumns().addAll(numberCol, songNameCol, ratingCol, lengthCol);

                            table.setOnMousePressed(event -> {
                                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                                    mainview.getMusicPlayer().play(table.getSelectionModel().getSelectedItem());

                                    //update queue in musicplayer
                                    mainview.getMusicPlayer().updateQueue(songs);
                                }
                            });

                            cover.setOnMousePressed(event -> {
                                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                                    mainview.getMusicPlayer().play(songs.get(0));

                                    //update queue in musicplayer
                                    mainview.getMusicPlayer().updateQueue(songs);
                                }
                            });

                            GridPane.setHgrow(table, Priority.ALWAYS);
                            g.setConstraints(table, 2, row, 1, songs.size() > 1 ? songs.size() : 2, HPos.CENTER, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);

                            Platform.runLater(() -> g.getChildren().addAll(cover, albumName, albumRelease, table));

                            row += songs.size() > 1 ? songs.size() : 2;
                        }
                    }
                }

                //endregion

                return g;
            }
        };
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();

        task.setOnFailed(event -> {
            System.out.println("FAILED");
            task.getException().printStackTrace();
        });

        task.setOnCancelled(event -> {
            System.out.println("CANCELLED");
        });

        task.setOnSucceeded(event -> {
            System.out.println("SUCCESS");
            this.setContent(task.getValue());
        });

    }

    public void refreshData() {

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

