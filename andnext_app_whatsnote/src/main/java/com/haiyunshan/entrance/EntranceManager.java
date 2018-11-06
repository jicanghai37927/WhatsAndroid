package com.haiyunshan.entrance;

import club.andnext.utils.GsonUtils;
import com.haiyunshan.storage.LocalStorage;
import com.haiyunshan.whatsnote.WhatsApp;

import java.io.File;

class EntranceManager {

    EntranceDataset entranceDataset;
    File entranceFile;

    static EntranceManager instance;

    public static final EntranceManager getInstance() {
        if (instance == null) {
            instance = new EntranceManager();
        }

        return instance;
    }

    private EntranceManager() {
        File dir = LocalStorage.getInstance().getDirectory(LocalStorage.PATH_ENTRANCE_DIR);
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
        EntranceDataset asset = GsonUtils.fromJson(WhatsApp.getContext(), "entrance/entrance_ds.json", EntranceDataset.class);
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
