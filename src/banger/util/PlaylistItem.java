package banger.util;


public class PlaylistItem {
    private String name;
    private String fileLocation;
    private int length;

    public PlaylistItem(String name, String fileLocation, int length){
        this.name = name;
        this.fileLocation = fileLocation;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return name + ", " + length;
    }
}
