package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.record.dataset.SortDataset;
import com.haiyunshan.whatsnote.record.dataset.SortEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortEntity {

    SortEntry entry;
    boolean reverse;
    Comparator<RecordEntity>[] array;

    ArrayList<SortEntity> childList;

    Context context;

    SortEntity(Context context, SortEntry entry) {
        this.context = context.getApplicationContext();

        this.entry = entry;

        this.array = new Comparator[2];
        this.childList = null;
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

    public int size() {
        if (childList == null) {
            return 0;
        }

        return childList.size();
    }

    public SortEntity get(int index) {
        return childList.get(index);
    }

}
