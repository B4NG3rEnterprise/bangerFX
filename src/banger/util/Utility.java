package banger.util;

import javafx.scene.paint.Color;

import java.util.Locale;

public class Utility {

    private Utility() {}

    public static boolean isDark(Color color){
        return color.getBrightness()<0.75;
    }

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format(Locale.US, "%f%c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp-1));
    }

}
