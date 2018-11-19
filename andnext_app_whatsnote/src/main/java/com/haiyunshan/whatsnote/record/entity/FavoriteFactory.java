package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.record.dataset.FavoriteDataset;
import com.haiyunshan.whatsnote.record.dataset.FavoriteEntry;

import java.util.ArrayList;

class FavoriteFactory {

    public static final FavoriteEntity obtain(Context context) {
        RecordManager mgr = RecordManager.getInstance(context);
        if (mgr.favoriteEntity != null) {
            return mgr.favoriteEntity;
        }

        mgr.favoriteEntity = create(context);
        return mgr.favoriteEntity;
    }

    static final FavoriteEntity create(Context context) {

        FavoriteEntry entry = null;
        RecordManager mgr = RecordManager.getInstance(context);

        FavoriteEntity entity = new FavoriteEntity(context, entry);
        FavoriteDataset ds = mgr.getFavoriteDataset();
        int size = ds.size();
        if (size != 0) {
            entity.childList = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                FavoriteEntry e = ds.get(i);

                FavoriteEntity en = new FavoriteEntity(context, e);
                entity.childList.add(en);
            }
        }

        return entity;
    }
}
