package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.record.dataset.RecordEntry;
import com.haiyunshan.whatsnote.record.dataset.SearchEntry;

public class SearchEntity extends BaseEntitySet<SearchEntity> {

    SearchEntry entry;

    SearchEntity(Context context, SearchEntry entry) {
        super(context);

        this.entry = entry;
    }

    @Override
    public String getId() {
        if (entry == null) {
            return "";
        }

        return entry.getId();
    }

    public String getName() {
        if (entry == null) {
            return "";
        }

        RecordEntry e = getManager().getRecordDataset().get(entry.getId());
        return RecordEntity.getName(e);
    }

    @Override
    public void save() {

    }

    public static final SearchEntity create() {
        return SearchFactory.create(WhatsApp.getContext());
    }

    public static final void add(RecordEntity entity) {
        String id = entity.getId();
        if (TextUtils.isEmpty(id)) {
            return;
        }
        
        SearchFactory.add(WhatsApp.getContext(), id);
    }
}
