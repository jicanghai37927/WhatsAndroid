package club.andnext.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {

    public static int[] getSize(Context context, Uri uri){

        int[] values = getJpgSize(context, uri);
        if (values != null) {
            return values;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        InputStream is = null;
        try {

            is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options); // 此时返回的bitmap为null
            values = new int[]{ options.outWidth, options.outHeight };

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return values;
    }

    static int[] getJpgSize(Context context, Uri uri) {
        int[] result = null;

        String ext = ContentUtils.getExtension(uri);
        ext = ext.toLowerCase();
        if (!(ext.endsWith("jpg") || ext.endsWith("jpeg"))) {
            return result;
        }

        InputStream is = null;

        try {
            ExifInterface exif = null;

            // try input stream
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                is = context.getContentResolver().openInputStream(uri);
                exif = new ExifInterface(is);
            }

            // try file
            if (exif == null) {
                String path = ContentUtils.getFilePath(context, uri);
                if (TextUtils.isEmpty(path)) {
                    exif = new ExifInterface(path);
                }
            }

            if (exif != null) {
                int width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                int height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);

                if (width > 0 && height > 0) {

                    int orient = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    // 宽、高互换
                    if (orient == ExifInterface.ORIENTATION_TRANSPOSE
                            || orient == ExifInterface.ORIENTATION_ROTATE_90
                            || orient == ExifInterface.ORIENTATION_TRANSVERSE
                            || orient == ExifInterface.ORIENTATION_ROTATE_270) {
                        int tmp = width;
                        width = height;
                        height = tmp;
                    }

                    result = new int[]{ width, height };
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

}
