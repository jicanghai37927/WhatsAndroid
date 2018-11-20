package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.record.dataset.SavedStateDataset;
import com.haiyunshan.whatsnote.record.dataset.SavedStateEntry;

class SavedStateFactory {

    static final SavedStateEntity create(Context context, String id) {
        RecordManager mgr = RecordManager.getInstance(context);

        SavedStateDataset ds = mgr.getSavedStateDataset();
        SavedStateEntry entry = ds.get(id);
        if (entry == null) {
            entry = new SavedStateEntry(id);
            ds.add(entry);
        }

        return new SavedStateEntity(context, entry);
    }
}
