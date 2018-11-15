package com.haiyunshan.whatsnote.article.entity;

import android.content.Context;
import android.net.Uri;
import club.andnext.utils.BitmapUtils;
import club.andnext.utils.ContentUtils;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.article.dataset.PictureEntry;

import java.io.File;

public class PictureEntity extends ParagraphEntity<PictureEntry> {

    Uri uri;

    public PictureEntity(Document d, PictureEntry entry) {
        super(d, entry);
    }

    @Override
    void save() {
        super.save();
    }

    public int getWidth() {
        return entry.getWidth();
    }

    public int getHeight() {
        return entry.getHeight();
    }

    public Uri getUri() {
        if (uri != null) {
            return uri;
        }

        File file = getManager().getFile(this);
        if ((file != null) && file.exists()) {
            uri = Uri.fromFile(file);
        } else {
            uri = Uri.parse(getEntry().getUri());
        }

        return uri;
    }

    public String getSignature() {
        StringBuilder sb = new StringBuilder();

        sb.append("picture://"); // article id
        sb.append(getDocument().getId());

        sb.append('/'); // entry id
        sb.append(this.getEntry().getId());

        sb.append("?hash="); // picture hash
        sb.append(this.getEntry().getSignature());

        return sb.toString();
    }

    public static final PictureEntity create(Document document, Uri uri) {
        Context context = document.getContext();
        int[] size = BitmapUtils.getSize(context, uri);
        if (size == null) { // if we cannot get the photo size, ignore this uri
            return null;
        }

        PictureEntry entry;

        {
            String id = UUIDUtils.next();
            int width = size[0];
            int height = size[1];
            String ext = ContentUtils.getExtension(uri);
            String text = uri.getLastPathSegment();

            entry = new PictureEntry(id, width, height, ext, uri);
            entry.setText(text);
        }

        PictureEntity entity = new PictureEntity(document, entry);
        {
            entity.uri = uri;
        }

        return entity;
    }

}
