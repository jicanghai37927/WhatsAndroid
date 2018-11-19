package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.record.dataset.FavoriteEntry;
import com.haiyunshan.whatsnote.record.dataset.RecordEntry;

public class FavoriteEntity extends BaseEntitySet<FavoriteEntity> {

    FavoriteEntry entry;

    FavoriteEntity(Context context, FavoriteEntry entry) {
        super(context);

        this.entry = entry;
    }

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

    public FavoriteEntity add(String id) {

        int index = 0;

        FavoriteEntry entry = new FavoriteEntry(id);

        {
            getManager().getFavoriteDataset().add(index, entry);
        }

        {
            FavoriteEntity entity = new FavoriteEntity(this.context, entry);
            this.add(index, entity);

            return entity;
        }
    }

    @Override
    public void save() {
        getManager().save(RecordManager.DS_FAVORITE);
    }

    public static final FavoriteEntity obtain() {
        return FavoriteFactory.create(WhatsApp.getContext());
    }

}
