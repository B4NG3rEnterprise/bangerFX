package banger.gui.options;

/**
 * Created by Nick on 30.03.2016.
 */
public enum KeyIDName {

    PLAYPAUSE(0,"Play/Pause"),PLAYSELECTED(1,"Play selected Song"), FORWARD(2,"Play next song"), BACKWARD(3, "Play previous song");

    private int id;
    private String name;
    KeyIDName(int id, String name) {
        this.id= id;
        this.name = name;
    }

    public int getID (){
        return this.id;
    }
    public String getName() {
        return this.name;
    }

    public static KeyIDName getByName(String name) {
        KeyIDName result = null;
        for (int i = 0;i < KeyIDName.values().length;i++) {
            if(KeyIDName.values()[i].getName().equals(name))
                result = KeyIDName.values()[i];
        }
        return result;
    }

    public static KeyIDName getByID(int id) {
        KeyIDName result = null;
        for (int i = 0;i < KeyIDName.values().length;i++) {
            if(KeyIDName.values()[i].getID()==id)
                result = KeyIDName.values()[i];
        }
        return result;
    }

    public String toString() {
        return this.getName();
    }
}
