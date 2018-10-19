package com.haiyunshan.preview;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import club.andnext.utils.CharsetUtils;
import club.andnext.utils.ContentUtils;

import java.io.*;

public class PreviewUtils {

    public static final String getText(Context context, PreviewEntity entity) {
        ByteBuffer stream = getBytes(context, entity);
        if (stream == null || stream.size() == 0) {
            return "";
        }

        int length = 6 * 1024;
        length = (stream.size() > length)? length: stream.size();
        String charset = CharsetUtils.getCharset(stream.buf, length, "utf-8");

        String str = "";
        try {
            str = new String(stream.buf, 0, stream.count, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static final ByteBuffer getBytes(Context context, PreviewEntity entity) {
        ByteBuffer buf = null;

        String uri = entity.getUri();
        if (TextUtils.isEmpty(uri)) {
            return buf;
        }

        Uri data = Uri.parse(uri);
        String path = ContentUtils.getFilePath(context, data);
        if (TextUtils.isEmpty(path)) {
            buf = getBytes(context, data, entity.getSize());
        } else {
            buf = getBytes(new File(path));
        }

        return buf;
    }

    static final ByteBuffer getBytes(Context context, Uri data, long size) {
        ByteBuffer buf = null;

        {
            InputStream is = null;

            try {
                is = context.getContentResolver().openInputStream(data);
                buf = getBytes(is, (int)size);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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
        }

        return buf;
    }

    static final ByteBuffer getBytes(File file) {
        if (!file.exists()) {
            return null;
        }

        ByteBuffer buf = null;

        {
            FileInputStream fis = null;

            try {
                fis = new FileInputStream(file);

                buf = getBytes(fis, (int) (file.length()));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return buf;
    }

    static final ByteBuffer getBytes(InputStream is, int size) throws IOException {

        ByteBuffer data = null;

        if (size <= 0) {
            size = 600 * 1024;

            ByteBuffer stream = new ByteBuffer(size);

            byte[] buf = new byte[200 * 1024];
            int length;
            while ((length = is.read(buf)) >= 0) {
                stream.write(buf, 0, length);
            }

        } else {

            byte[] buf = new byte[size];
            int offset = 0;
            while (true) {
                int num = is.read(buf, offset, (size - offset));
                if (num < 0) {
                    break;
                }

                offset += num;
                if (offset == size) {
                    break;
                }
            }

            data = new ByteBuffer(buf);
        }

        return data;
    }
}
