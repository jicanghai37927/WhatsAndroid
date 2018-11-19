package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.Nullable;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.record.dataset.SortEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SortEntity extends BaseEntitySet<SortEntity> {

    public static final String ID_NAME      = "name";
    public static final String ID_CREATED   = "created";
    public static final String ID_MODIFIED  = "modified";
    public static final String ID_SIZE      = "size";
    public static final String ID_TAG       = "tag";

    SortEntry entry;
    boolean reverse;
    Comparator<RecordEntity>[] array;

    SortEntity(Context context, SortEntry entry) {
        super(context);

        this.entry = entry;
        this.reverse = false;
        this.array = new Comparator[2];
    }

    SortEntity(SortEntity entity) {
        super(entity.context);

        this.entry = entity.entry;
        this.reverse = entity.reverse;
        this.array = Arrays.copyOf(entity.array, entity.array.length);
    }

    public String getId() {
        return entry.getId();
    }

    public String getName() {
        return entry.getName();
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public void setReverse(boolean value) {
        this.reverse = value;
    }

    public void toggle() {
        this.setReverse(!isReverse());
    }

    public Comparator<RecordEntity> getComparator() {
        int index = (reverse)? 1: 0;
        if (array[index] != null) {
            return array[index];
        }

        if (array[0] == null) {
            String id = getId();
            array[0] = SortManager.getInstance(this.context).create(id);
        }

        if (index == 1) {
            array[1] = Collections.reverseOrder(array[0]);
        }

        return array[index];
    }

    @Override
    public void save() {

    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SortEntity)) {
            return false;
        }

        SortEntity another = (SortEntity)obj;

        boolean a = this.getId().equals(another.getId());
        boolean b = !(this.isReverse() ^ another.isReverse());

        return (a && b);
    }

    public static final SortEntity all() {
        return SortFactory.all(WhatsApp.getContext());
    }

    public static final SortEntity create(String id) {
        return SortFactory.create(WhatsApp.getContext(), id);
    }
}
