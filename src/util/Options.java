package util;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class Options {
    private final static String PATH = "res/options.ini";
    private static Ini ini;

    private static int audio_device = -1;

    private Options(){}

    private static void initialize(){
        try {
            ini = new Ini(new File(PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveData(String cat, String name, String value){
        initialize();
        ini.clear();
        ini.put(cat, name, value);
        try {
            ini.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main (String[] args){
        saveData("Options", "AudioDevice", "-1");
    }
}
