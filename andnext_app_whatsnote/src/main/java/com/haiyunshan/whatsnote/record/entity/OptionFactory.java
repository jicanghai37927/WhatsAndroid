package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.record.dataset.OptionDataset;

class OptionFactory {

    public static final OptionEntity obtain(Context context) {
        RecordManager mgr = RecordManager.getInstance(context);
        if (mgr.optionEntity != null) {
            return mgr.optionEntity;
        }

        mgr.optionEntity = create(context);
        return mgr.optionEntity;
    }

    static final OptionEntity create(Context context) {
        RecordManager mgr = RecordManager.getInstance(context);
        OptionDataset ds = mgr.createDataset(OptionDataset.class);

        return new OptionEntity(context, ds);
    }
}
