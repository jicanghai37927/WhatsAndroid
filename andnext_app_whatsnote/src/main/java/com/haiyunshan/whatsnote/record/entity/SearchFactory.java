package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import com.haiyunshan.whatsnote.record.dataset.*;

import java.util.ArrayList;

class SearchFactory {

    static final boolean add(Context context, String id) {
        SearchDataset ds = RecordManager.getInstance(context).getSearchDataset();

        SearchEntry entry = ds.get(id);
        if (entry != null) { // move to first
            ds.remove(entry);
        } else {
            entry = new SearchEntry(id);
        }

        {
            ds.add(0, entry);
        }

        RecordManager.getInstance(context).save(ds);

        return true;
    }

    static final SearchEntity create(Context context) {

        SearchEntry entry = null;
        RecordManager mgr = RecordManager.getInstance(context);

        SearchEntity entity = new SearchEntity(context, entry);
        SearchDataset ds = mgr.getSearchDataset();
        int size = ds.size();
        if (size != 0) {
            entity.childList = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                SearchEntry e = ds.get(i);

                SearchEntity en = new SearchEntity(context, e);
                entity.childList.add(en);
            }
        }

        return entity;
    }

    public static final SearchRecordSet createRecordSet(Context context, String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return new SearchRecordSet(context, "");
        }

        RecordManager mgr = RecordManager.getInstance(context);
        SearchRecordSet rs = new SearchRecordSet(context, keyword);

        {
            RecordDataset ds = mgr.getRecordDataset();
            int size = ds.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    RecordEntry en = ds.get(i);

                    // index by name
                    String name = RecordEntity.getName(en);
                    if (name.indexOf(keyword) < 0) {
                        continue;
                    }

                    RecordEntity r = new RecordEntity(context, en.getId(), en);
                    rs.add(r);
                }
            }
        }

        return rs;
    }

}
