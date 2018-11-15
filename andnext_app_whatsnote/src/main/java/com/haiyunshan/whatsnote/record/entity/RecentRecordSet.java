package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;

import java.util.ArrayList;

public class RecentRecordSet {

    TagEntity tagEntity;

    ArrayList<RecordEntity> recordList;

    Context context;

    RecentRecordSet(Context context, TagEntity tag) {
        this.context = context.getApplicationContext();

        this.tagEntity = tag;
    }

    public TagEntity getTag() {
        return tagEntity;
    }

    public RecordEntity get(String id) {
        if (recordList == null || recordList.isEmpty()) {
            return null;
        }

        for (RecordEntity e : recordList) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }

    public RecordEntity get(int index) {
        if (recordList == null) {
            return null;
        }

        return recordList.get(index);
    }

    public int size() {
        if (recordList == null) {
            return 0;
        }

        return recordList.size();
    }

    public int remove(RecordEntity entity) {
        int index = this.indexOf(entity);
        if (index < 0) {
            return index;
        }

        getManager().remove(entity.entry);
        recordList.remove(entity);

        return index;
    }

    public int indexOf(RecordEntity entity) {
        if (recordList == null) {
            return -1;
        }

        return recordList.indexOf(entity);
    }

    public void save() {
        getManager().save(RecordManager.DS_RECENT);
    }

    RecordManager getManager() {
        return RecordManager.getInstance(context);
    }

}
