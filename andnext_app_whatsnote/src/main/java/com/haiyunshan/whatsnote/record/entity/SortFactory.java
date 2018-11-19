package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.record.dataset.SortDataset;
import com.haiyunshan.whatsnote.record.dataset.SortEntry;

import java.util.ArrayList;

class SortFactory {

    public static final SortEntity create(Context context, String id) {
        SortDataset ds = SortManager.getInstance(context).getDataset();
        SortEntry entry = ds.get(id);
        if (entry == null) {
            entry = ds.get(0);
        }

        return new SortEntity(context, entry);
    }

    public static final SortEntity create(SortEntity entity) {
        return new SortEntity(entity);
    }

    public static final SortEntity all(Context context) {
        SortManager mgr = SortManager.getInstance(context);

        SortDataset ds = mgr.getDataset();

        SortEntity entity = new SortEntity(context, null);
        entity.childList = new ArrayList<>(ds.size());

        for (int i = 0, size = ds.size(); i < size; i++) {
            SortEntry e = ds.get(i);
            if (!e.isVisible()) {
                continue;
            }

            SortEntity en = new SortEntity(context, e);
            entity.childList.add(en);
        }

        return entity;
    }
}
