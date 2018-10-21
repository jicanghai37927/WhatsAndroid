package com.haiyunshan.preview;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import club.andnext.utils.ByteBuffer;
import club.andnext.utils.CharsetUtils;
import club.andnext.utils.ContentUtils;

import java.io.*;

public class PreviewUtils {

    public static final String getText(Context context, PreviewEntity entity) {
        ByteBuffer stream = getBytes(context, entity);
        if (stream == null || stream.size() == 0) {
            return entity.getExtraText();
        }

        int length = 6 * 1024;
        length = (stream.size() > length)? length: stream.size();
        String charset = CharsetUtils.getCharset(stream.getData(), length, "utf-8");

        String str = "";
        try {
            str = new String(stream.getData(), 0, stream.size(), charset);
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
            buf = ByteBuffer.create(context, data, entity.getSize());
        } else {
            buf = ByteBuffer.create(new File(path));
        }

        return buf;
    }


}
