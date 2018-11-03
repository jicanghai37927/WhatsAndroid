package club.andnext.utils;

import android.graphics.Color;

public class ColorUtils {

    public static final String format(int color) {
        if (color == Color.TRANSPARENT) {
            return "#00000000";
        }

        String str = Integer.toHexString(color);
        if (str.length() < 6) { // 补足6位
            int count = (6 - str.length());
            for (int i = 0; i < count; i++) {
                str = "0" + str;
            }
        } else if (str.length() == 7) { // 补足8位
            str = "0" + str;
        }

        return "#" + str;
    }

    public static final int parse(String colorString) {
        return Color.parseColor(colorString);
    }
}
