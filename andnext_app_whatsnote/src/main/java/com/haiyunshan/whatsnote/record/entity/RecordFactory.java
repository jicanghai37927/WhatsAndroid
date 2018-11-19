package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.record.dataset.RecordEntry;

import java.util.List;

import static com.haiyunshan.whatsnote.record.entity.RecordEntity.TYPE_ALL;
import static com.haiyunshan.whatsnote.record.entity.RecordManager.TYPE_EMPTY;

class RecordFactory {

    static final String[] RECORD_ARRAY  = new String[] { RecordManager.RECORD_FOLDER, RecordManager.RECORD_NOTE };
    static final int[] TYPE_ARRAY       = new int[] { RecordManager.TYPE_FOLDER, RecordManager.TYPE_NOTE };

    public static RecordEntity create(Context context, String id) {
        return create(context, id, TYPE_ALL);
    }

    public static RecordEntity create(Context context, String id, int childFlags) {

        RecordManager mgr = RecordManager.getInstance(context);
        RecordEntry entry = mgr.getRecordDataset().get(id);

        RecordEntity entity = new RecordEntity(context, id, entry);
        if (childFlags != TYPE_EMPTY) {

            List<RecordEntry> list = mgr.getList(id, childFlags, null);
            if (list != null && list.size() > 0) {
                for (RecordEntry e : list) {
                    entity.add(new RecordEntity(context, e.getId(), e));
                }
            }

        }

        return entity;
    }

    static final int getType(RecordEntry entry) {

        int index = -1;
        for (int i = 0, size = RECORD_ARRAY.length; i < size; i++) {
            if (RECORD_ARRAY[i].equals(entry.getType())) {
                index = i;
                break;
            }
        }

        return TYPE_ARRAY[index];
    }

    static final String getType(int type) {
        int index = -1;
        for (int i = 0, size = TYPE_ARRAY.length; i < size; i++) {
            if (TYPE_ARRAY[i] == type) {
                index = i;
                break;
            }
        }

        return RECORD_ARRAY[index];
    }
}
