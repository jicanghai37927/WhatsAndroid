package com.haiyunshan.record;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class FavoriteEntity {

    String id;
    FavoriteEntry entry;

    ArrayList<FavoriteEntity> childList;

    RecordManager recordManager;

    FavoriteEntity(String id, FavoriteEntry entry, RecordManager mgr) {
        this.id = id;
        this.entry = entry;

        this.childList = null;

        this.recordManager = mgr;
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

        RecordEntry e = recordManager.getRecordDataset().get(entry.getId());
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
        FavoriteEntry entry = new FavoriteEntry(id);

        {
            recordManager.getFavoriteDataset().add(entry);
        }

        {
            FavoriteEntity entity = new FavoriteEntity(entry.getId(), entry, this.recordManager);
            if (childList == null) {
                childList = new ArrayList<>();
            }

            childList.add(entity);

            return entity;
        }
    }

    public int getOrder() {
        if (entry == null) {
            return Integer.MAX_VALUE;
        }

        List<String> list = recordManager.getFavoriteDataset().getOrderList();
        if (list == null) {
            return Integer.MAX_VALUE;
        }

        int index = list.indexOf(entry.getId());
        if (index < 0) {
            return Integer.MAX_VALUE;
        }

        return index;
    }

    public void save() {
        recordManager.save(RecordManager.DS_FAVORITE);
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

        FavoriteEntity entity = new FavoriteEntity(id, entry, mgr);
        FavoriteDataset ds = mgr.getFavoriteDataset();
        int size = ds.size();
        if (size != 0) {
            entity.childList = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                FavoriteEntry e = ds.get(i);

                FavoriteEntity en = new FavoriteEntity(e.getId(), e, mgr);
                entity.childList.add(en);
            }
        }

        return entity;
    }
}
