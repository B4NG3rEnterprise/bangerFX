package banger.gui.sidebar.filebrowser;

import banger.audio.data.Song;
import banger.database.DBController;
import banger.gui.MainView;
import banger.gui.options.Options;
import banger.util.BangerVars;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.StageStyle;

import java.io.File;

public class FileBrowser extends TreeView<TreeFile> {

    private MainView mainview;
    private String path = Options.fileBrowserPath;

    public FileBrowser(MainView mainview){
        super();

        this.mainview = mainview;

        init();
    }

    @SuppressWarnings("unchecked")
    private void init(){
        getStylesheets().add("banger/gui/sidebar/filebrowser/filebrowser.css");

        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);
        getSelectionModel().clearSelection();

        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
                handleMouseClicked(event);
        };

        addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);

        // check if program is run the first time, if so let user choose a music directory
        if (path.equals("null")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("");
            alert.setGraphic(null);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add("banger/gui/menubar/dialog.css");
            alert.setTitle("Erstes Mal gestartet");
            alert.setContentText("Sie haben das Programm zuvor noch nicht genutzt. Bitte wählen Sie eine Directory mit Musik aus!");
            alert.showAndWait();

            DirectoryChooser dc = new DirectoryChooser();
            File directory = dc.showDialog(mainview.stage);
            DBController.createFromDirectory(directory.toString());
            Options.setDirectory(directory.getPath());
            setPath(directory.getPath());
            //mainview.getLibrary().refreshData();
        }
        findFiles(new File(path), null);
        this.getRoot().setExpanded(true);
    }

    public void setPath(String path){
        this.path = path;
        findFiles(new File(path), null);
    }

    private void findFiles(File dir, TreeItem<TreeFile> parent) {
        TreeItem<TreeFile> root = new TreeItem<>(new TreeFile(dir));

        GlyphsDude.setIcon(root, MaterialDesignIcon.FOLDER, "15px");
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    // System.out.println("directory:" + file.getCanonicalPath());
                    findFiles(file,root);
                } else {
                    // System.out.println("     file:" + file.getCanonicalPath());
                    for (int x = 0; x < BangerVars.FILE_EXTENSIONS.length; x++)
                        if (file.getAbsolutePath().endsWith(BangerVars.FILE_EXTENSIONS[x])) {
                            TreeItem<TreeFile> item = new TreeItem<>(new TreeFile(file));
                            GlyphsDude.setIcon(item, MaterialDesignIcon.MUSIC_NOTE, "15px");
                            root.getChildren().add(item);
                        }
                }

            }
            if(parent == null){
                this.setRoot(root);
            } else {
                parent.getChildren().add(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMouseClicked(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            TreeFile name = getSelectionModel().getSelectedItem().getValue();
            // System.out.println("Node click: " + name.getFile().toString());
            if (event.getClickCount() == 2) {
                for (String ext : BangerVars.FILE_EXTENSIONS)
                    if (name.getFile().toString().endsWith(ext)){
                        Song s = DBController.getSongFromPath(name.getFile().getPath());
                        mainview.getMusicPlayer().play(s);
                        mainview.getLibrary().select(s);
                    }
            }
        }

    }
}

