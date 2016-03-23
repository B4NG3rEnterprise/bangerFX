package banger.gui.filebrowser;

import banger.gui.MainView;
import banger.util.BangerVars;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

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
        setPrefWidth(200);
        setMaxWidth(300);

        getSelectionModel().clearSelection();

        findFiles(new File("D:\\Musik"), null);
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
                            TreeItem<TreeFile> item = new TreeItem<>(new TreeFile(file)); // add icon
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
}

