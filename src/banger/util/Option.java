package banger.util;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class Option {
    private final static String PATH = "res/options.ini";
    private static Ini ini;

    public static String audio_device = "-1";
    public static String backgroundColor = "#FA7D38";
    public static boolean notifications = true;

    private Option(){}

    private static void initialize(){
        try {
            ini = new Ini(new File(PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getOption(String cat, String name){
        Ini.Section section = ini.get("Options");
        System.out.println(backgroundColor);
    }

    public static void saveOption(String cat, String name, String value){
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
        saveOption("Options", "AudioDevice", "-1");
    }
}
