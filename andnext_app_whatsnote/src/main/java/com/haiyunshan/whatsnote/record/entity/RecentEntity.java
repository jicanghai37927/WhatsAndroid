package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.record.dataset.RecentEntry;

public class RecentEntity extends BaseEntitySet<RecentEntity> {

    RecentEntry entry;

    RecentEntity(Context context, RecentEntry entry) {
        super(context);

        this.entry = entry;

    }

    @Override
    public void save() {

    }

    public static final boolean put(String id) {
        return RecentFactory.put(WhatsApp.getContext(), id);
    }
}
