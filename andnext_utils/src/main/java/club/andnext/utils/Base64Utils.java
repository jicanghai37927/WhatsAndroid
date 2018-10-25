package club.andnext.utils;

import android.util.Base64;

public class Base64Utils {

    public static final String encode(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static final byte[] decode(String text) {
        return Base64.decode(text, Base64.DEFAULT);
    }
}
