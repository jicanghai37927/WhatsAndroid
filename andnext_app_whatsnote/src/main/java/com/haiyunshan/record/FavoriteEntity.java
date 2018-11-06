package com.haiyunshan.record;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class FavoriteEntity {

    String id;
    FavoriteEntry entry;

    ArrayList<FavoriteEntity> childList;

    FavoriteEntity(String id, FavoriteEntry entry) {
        this.id = id;
        this.entry = entry;

        this.childList = null;
    }

    public String getId() {
        if (entry == null) {
            return id;
        }

        return entry.getId();
    }

    public String getName() {
        if (entry == null) {
            return "";
        }

        RecordEntry e = getManager().getRecordDataset().get(entry.getId());
        if (e == null) {
            return "";
        }

        if (!TextUtils.isEmpty(e.getName())) {
            return e.getName();
        }
        if (!TextUtils.isEmpty(e.getAlias())) {
            return e.getAlias();
        }

        return "";
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
            FavoriteEntity entity = new FavoriteEntity(entry.getId(), entry);
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
        return RecordManager.getInstance();
    }

    public static final FavoriteEntity obtain() {
        RecordManager mgr = RecordManager.getInstance();
        if (mgr.favoriteEntity != null) {
            return mgr.favoriteEntity;
        }

        mgr.favoriteEntity = create();
        return mgr.favoriteEntity;
    }

    static final FavoriteEntity create() {

        String id = "/";
        FavoriteEntry entry = null;
        RecordManager mgr = RecordManager.getInstance();

        FavoriteEntity entity = new FavoriteEntity(id, entry);
        FavoriteDataset ds = mgr.getFavoriteDataset();
        int size = ds.size();
        if (size != 0) {
            entity.childList = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                FavoriteEntry e = ds.get(i);

                FavoriteEntity en = new FavoriteEntity(e.getId(), e);
                entity.childList.add(en);
            }
        }

        return entity;
    }
}
