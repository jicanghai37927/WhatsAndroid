package club.andnext.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5Utils {

    public static String getFileMD5(File file) {

        if (!(file.isFile() && file.exists() && file.canRead())) {
            return null;
        }

        int size = 200 * 1024;

        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[size];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");

            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                digest.update(buffer, 0, len);
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return bytesToHexString(digest.digest());
    }

    static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }

        return stringBuilder.toString().toLowerCase();
    }
}
