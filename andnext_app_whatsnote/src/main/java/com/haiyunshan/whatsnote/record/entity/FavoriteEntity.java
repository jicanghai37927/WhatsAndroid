package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.record.dataset.FavoriteEntry;
import com.haiyunshan.whatsnote.record.dataset.RecordEntry;

import java.util.ArrayList;
import java.util.List;

public class FavoriteEntity {

    FavoriteEntry entry;

    ArrayList<FavoriteEntity> childList;

    Context context;

    FavoriteEntity(Context context, FavoriteEntry entry) {
        this.context = context.getApplicationContext();

        this.entry = entry;

        this.childList = null;
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
        return RecordFactory.getName(e);
    }

    public List<FavoriteEntity> getList() {
        return childList;
    }

    public FavoriteEntity get(int index) {
        if (childList == null) {
            return null;
        }

        return childList.get(index);
    }

    public int size() {
        if (childList == null) {
            return 0;
        }

        return childList.size();
    }

    public FavoriteEntity add(String id) {

        int index = 0;

        FavoriteEntry entry = new FavoriteEntry(id);

        {
            getManager().getFavoriteDataset().add(index, entry);
        }

        {
            FavoriteEntity entity = new FavoriteEntity(this.context, entry);
            if (childList == null) {
                childList = new ArrayList<>();
            }

            childList.add(index, entity);

            return entity;
        }
    }

    public void save() {
        getManager().save(RecordManager.DS_FAVORITE);
    }

    RecordManager getManager() {
        return RecordManager.getInstance(context);
    }

}
