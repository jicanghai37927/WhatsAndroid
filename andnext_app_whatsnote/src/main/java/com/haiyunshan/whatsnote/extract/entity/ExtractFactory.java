package com.haiyunshan.whatsnote.extract.entity;

import android.content.Context;
import android.database.Cursor;
import com.haiyunshan.whatsnote.article.entity.Document;
import com.haiyunshan.whatsnote.extract.ExtractProvider;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;

public class ExtractFactory {

    public static void check(Context context, RecordEntity target) {
        if (!target.isExtract()) {
            return;
        }

        int count = 0;

        Cursor cursor = context.getContentResolver().query(ExtractProvider.obtainUri(), null, null, null, null);
        if (cursor != null) {
            count = cursor.getCount();

            if (cursor.moveToFirst()) {
                do {

                    String content = cursor.getString(1);
                    long created = cursor.getLong(2);

                    {
                        String name = getTitle(content, 56);
                        RecordEntity entity = target.add(RecordEntity.TYPE_NOTE, name);
                        entity.setCreated(created);

                        Document.create(context, entity, content);
                    }

                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        if (count != 0) {
            context.getContentResolver().delete(ExtractProvider.obtainUri(), null, null);
        }
    }

    static String getTitle(String content, int max) {
        String text = content.trim();
        if (text.length() > max) {
            text = text.substring(0, max).trim();
        }

        int pos = text.indexOf('\n');
        if (pos > 0) {
            return text.substring(0, pos);
        }

        return text;
    }

}
