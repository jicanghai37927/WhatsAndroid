package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.record.dataset.SortEntry;

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

        this.array = new Comparator[2];
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
}
