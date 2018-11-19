package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.Nullable;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.record.dataset.FavoriteEntry;
import com.haiyunshan.whatsnote.record.dataset.RecordEntry;

import java.util.ArrayList;

public class FavoriteEntity extends BaseEntitySet<FavoriteEntity> {

    String name;

    FavoriteEntry entry;

    FavoriteEntity(Context context, FavoriteEntry entry) {
        super(context);

        this.entry = entry;
    }

    FavoriteEntity(FavoriteEntity entity) {
        this(entity.context, entity.entry);

        this.name = RecordEntity.getName(context, entity.getId());

        if (entity.childList != null) {
            this.childList = new ArrayList<>(entity.childList.size());
            for (int i = 0, size = entity.childList.size(); i < size; i++) {
                FavoriteEntity en = new FavoriteEntity(entity.childList.get(i));
                this.childList.add(en);
            }
        }
    }

    public String getId() {
        if (entry == null) {
            return "";
        }

        return entry.getId();
    }

    public String getName() {
        if (name != null) {
            return name;
        }

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof FavoriteEntity)) {
            return false;
        }

        FavoriteEntity another = (FavoriteEntity)obj;

        String name1 = this.getName();
        String name2 = another.getName();
        return name1.equals(name2);
    }

    public static final FavoriteEntity obtain() {
        return FavoriteFactory.obtain(WhatsApp.getContext());
    }

    public static final FavoriteEntity copy() {
        return new FavoriteEntity(obtain());
    }
}
