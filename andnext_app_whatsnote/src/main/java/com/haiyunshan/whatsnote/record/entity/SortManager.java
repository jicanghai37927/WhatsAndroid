package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import club.andnext.utils.GsonUtils;
import com.haiyunshan.whatsnote.record.dataset.SortDataset;

import java.util.Comparator;

class SortManager {

    ComparatorFactory comparatorFactory;
    SortDataset sortDataset;

    Context context;

    private static SortManager instance;

    public static final SortManager getInstance(Context context) {
        if (instance == null) {
            instance = new SortManager(context);
        }

        return instance;
    }

    private SortManager(Context context) {
        this.context = context.getApplicationContext();
    }

    Comparator<RecordEntity> create(String id) {
        return getComparatorFactory().create(id);
    }

    SortDataset getDataset() {
        if (sortDataset != null) {
            return sortDataset;
        }

        SortDataset ds = GsonUtils.fromJson(context, "record/sort_ds.json", SortDataset.class);
        if (ds == null) {
            ds = new SortDataset();
        }

        sortDataset = ds;
        return sortDataset;
    }

    ComparatorFactory getComparatorFactory() {
        if (comparatorFactory != null) {
            return comparatorFactory;
        }

        comparatorFactory = new ComparatorFactory();

        comparatorFactory.register(SortEntity.ID_NAME,      ComparatorFactory.Name.class);
        comparatorFactory.register(SortEntity.ID_CREATED,   ComparatorFactory.Created.class);
        comparatorFactory.register(SortEntity.ID_MODIFIED,  ComparatorFactory.Modified.class);
        comparatorFactory.register(SortEntity.ID_SIZE,      ComparatorFactory.Size.class);
        comparatorFactory.register(SortEntity.ID_TAG,       ComparatorFactory.Tag.class);

        return comparatorFactory;
    }

}
