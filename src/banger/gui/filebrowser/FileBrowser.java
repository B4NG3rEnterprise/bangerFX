package banger.gui.filebrowser;

import banger.gui.MainView;
import banger.util.BangerVars;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

/**
 * Created by Merlin on 22.03.2016.
 */
public class FileBrowser extends TreeView<TreeFile> {

    private MainView mainview;

    public FileBrowser(MainView mainview){
        super();

        this.mainview = mainview;

        init();
    }

    @SuppressWarnings("unchecked")
    private void init(){
        getStylesheets().add("banger/gui/filebrowser/filebrowser.css");

        setMinWidth(150);
        setPrefWidth(150);
        setMaxWidth(300);

        getSelectionModel().clearSelection();

        findFiles(new File("C:\\Users\\Merlin\\Music"), null);
    }


    private final Node folderIcon = new ImageView(
            new Image("png/interface.png")
    );

    private final Node musicIcon = new ImageView(
            new Image("png/shapes.png")
    );

    private void findFiles(File dir, TreeItem<TreeFile> parent) {
        TreeItem<TreeFile> root = new TreeItem<>(new TreeFile(dir), folderIcon);
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
                            TreeItem<TreeFile> item = new TreeItem<>(new TreeFile(file), musicIcon); // add icon
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
}

