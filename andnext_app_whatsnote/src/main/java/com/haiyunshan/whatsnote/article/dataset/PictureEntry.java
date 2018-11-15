package com.haiyunshan.whatsnote.article.dataset;

import android.net.Uri;
import com.google.gson.annotations.SerializedName;

public class PictureEntry extends ParagraphEntry {

    public static final String TYPE = "picture";

    @SerializedName("width")
    int width;

    @SerializedName("height")
    int height;

    @SerializedName("extension")
    String extension; // extension

    @SerializedName("signature")
    String signature; // use for cache bitmap

    @SerializedName("uri")
    String uri; // source uri

    public PictureEntry(String id, int width, int height, String ext, Uri uri) {
        super(id);
        this.type = TYPE;

        this.width = width;
        this.height = height;
        this.extension = ext;
        this.uri = uri.toString();

        this.signature = String.valueOf(uri.hashCode());
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSignature() {
        return signature == null? "": signature;
    }

    public String getExtension() {
        return extension == null? "": extension;
    }

    public void setExtension(String ext) {
        this.extension = ext;
    }

    public String getUri() {
        return uri == null? "": uri;
    }

    public void setUri(Uri uri) {
        if (uri != null) {
            this.uri = uri.toString();
            this.signature = String.valueOf(uri.hashCode());
        } else {
            this.uri = null;
            this.signature = null;
        }
    }
}
