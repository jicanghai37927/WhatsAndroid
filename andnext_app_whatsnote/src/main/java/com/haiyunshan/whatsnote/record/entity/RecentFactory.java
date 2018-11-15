package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import com.haiyunshan.whatsnote.record.dataset.RecentDataset;
import com.haiyunshan.whatsnote.record.dataset.RecentEntry;
import com.haiyunshan.whatsnote.record.dataset.RecordEntry;

import java.util.ArrayList;

public class RecentFactory {

    public static final boolean put(Context context, String id) {
        int index = 0;

        RecentDataset ds = RecordManager.getInstance(context).getRecentDataset();

        RecentEntry entry = ds.get(id);
        if (entry == null) {
            entry = new RecentEntry(id);
            ds.add(index, entry);

            RecordManager.getInstance(context).save(ds);
        }

        return true;
    }

    public static final RecentRecordSet createRecordSet(Context context, String tag) {
        TagEntity tagEntity = null;
        if (!TextUtils.isEmpty(tag)) {
            tagEntity = TagFactory.create(context, tag);
        }

        RecentRecordSet rs = create(context, tagEntity);

        return rs;
    }

    static final RecentRecordSet create(Context context, TagEntity tag) {
        RecordManager mgr = RecordManager.getInstance(context);

        RecentRecordSet rs = new RecentRecordSet(context, tag);

        RecentDataset ds = mgr.getRecentDataset();
        int size = ds.size();
        if (size > 0) {
            rs.recordList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                RecentEntry e = ds.get(i);
                RecordEntry en = mgr.getRecordDataset().get(e.getId());
                if (en == null) {
                    continue;
                }

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
}
