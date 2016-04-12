package banger.util;

import java.awt.*;
import java.util.Locale;

public class Utility {

    private Utility() {}

    public static boolean isDark(String color){
        String fontColor = color;
        boolean isDark = false;

        // remove hash character from string
        String rawFontColor = fontColor.substring(1,fontColor.length());

        // convert hex string to int
        int rgb = Integer.parseInt(rawFontColor, 16);
        Color c = new Color(rgb);
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float brightness = hsb[2];
        if (brightness < 0.5) isDark = true;

        return isDark;
    }

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format(Locale.US, "%f%c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp-1));
    }

}
