package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import com.haiyunshan.whatsnote.record.dataset.RecordDataset;
import com.haiyunshan.whatsnote.record.dataset.RecordEntry;
import com.haiyunshan.whatsnote.record.dataset.TagDataset;
import com.haiyunshan.whatsnote.record.dataset.TagEntry;

import java.util.ArrayList;

public class TagFactory {

    public static final TagEntity obtain(Context context) {
        RecordManager mgr = RecordManager.getInstance(context);
        if (mgr.tagEntity != null) {
            return mgr.tagEntity;
        }

        mgr.tagEntity = create(context);
        return mgr.tagEntity;
    }

    public static final TagRecordSet createRecordSet(Context context, String tag) {
        TagEntity tagEntity = null;
        if (!TextUtils.isEmpty(tag)) {
            tagEntity = TagFactory.create(context, tag);
        }

        TagRecordSet rs = create(context, tagEntity);

        return rs;
    }

    static final TagRecordSet create(Context context, TagEntity tag) {
        RecordManager mgr = RecordManager.getInstance(context);

        TagRecordSet rs = new TagRecordSet(context, tag);

        RecordDataset ds = mgr.getRecordDataset();
        int size = ds.size();
        if (size > 0) {
            rs.recordList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                RecordEntry en = ds.get(i);

                if (tag != null) {
                    if (en.indexOfTag(tag.getId()) < 0) {
                        continue;
                    }
                }

                RecordEntity r = new RecordEntity(context, en.getId(), en);
                if (r.isTrash()) {
                    continue;
                }

                rs.recordList.add(r);
            }
        }

        return rs;
    }

    static final TagEntity create(Context context) {

        String id = "/";
        TagEntry entry = null;
        RecordManager mgr = RecordManager.getInstance(context);

        TagEntity entity = new TagEntity(context, id, entry);
        TagDataset ds = mgr.getTagDataset();
        int size = ds.size();
        if (size != 0) {
            entity.childList = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                TagEntry e = ds.get(i);

                TagEntity en = new TagEntity(context, e.getId(), e);
                entity.childList.add(en);
            }
        }

        return entity;
    }

    static final TagEntity create(Context context, String id) {
        RecordManager mgr = RecordManager.getInstance(context);
        TagDataset ds = mgr.getTagDataset();
        TagEntry entry = ds.get(id);
        if (entry == null) {
            return null;
        }

        TagEntity entity = new TagEntity(context, id, entry);
        return entity;
    }
}
