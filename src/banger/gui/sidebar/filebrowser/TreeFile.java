package banger.gui.sidebar.filebrowser;

import java.io.File;

/**
 * Created by Merlin on 22.03.2016.
 */
public class TreeFile {
    private String name;
    private File file;

    public TreeFile(File f){
        this.file = f;
        this.name = f.getName();
    }

    public String toString() {
        return name;
    }
}
