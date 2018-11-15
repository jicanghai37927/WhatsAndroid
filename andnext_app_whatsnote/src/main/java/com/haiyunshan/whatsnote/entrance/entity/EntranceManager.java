package com.haiyunshan.whatsnote.entrance.entity;

import android.content.Context;
import club.andnext.utils.GsonUtils;
import com.haiyunshan.whatsnote.entrance.dataset.EntranceDataset;
import com.haiyunshan.whatsnote.entrance.dataset.EntranceEntry;
import com.haiyunshan.whatsnote.directory.DirectoryManager;

import java.io.File;

class EntranceManager {

    EntranceDataset entranceDataset;
    File entranceFile;

    Context context;

    static EntranceManager instance;

    public static final EntranceManager getInstance(Context context) {
        if (instance == null) {
            instance = new EntranceManager(context);
        }

        return instance;
    }

    private EntranceManager(Context context) {
        this.context = context.getApplicationContext();

        File dir = DirectoryManager.getInstance().getDirectory(context, DirectoryManager.DIR_ENTRANCE);
        this.entranceFile = new File(dir, "entrance_ds.json");
    }

    void save() {
        if (entranceDataset == null) {
            return;
        }

        GsonUtils.toJson(entranceDataset, entranceFile);
    }

    EntranceDataset getDataset() {
        if (entranceDataset != null) {
            return entranceDataset;
        }

        if (entranceFile.exists()) {
            this.entranceDataset = GsonUtils.fromJson(entranceFile, EntranceDataset.class);
        }

        entranceDataset = merge(entranceDataset);
        return entranceDataset;
    }

    EntranceDataset merge(EntranceDataset ds) {
        EntranceDataset asset = GsonUtils.fromJson(context, "entrance/entrance_ds.json", EntranceDataset.class);
        if (asset == null) {
            asset = new EntranceDataset();
        }

        if (ds == null) {
            return asset;
        }

        for (int i = 0, size = asset.size(); i < size; i++) {
            EntranceEntry f = asset.get(i);
            EntranceEntry s = ds.get(f.getId());
            if (s == null) {
                ds.add(1, f);
            } else {

            }
        }

        return ds;
    }
}
