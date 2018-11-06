package com.haiyunshan.record;

import android.text.TextUtils;

import java.util.ArrayList;

public class TagRecordSet {

    TagEntity tagEntity;

    ArrayList<RecordEntity> recordList;

    TagRecordSet(TagEntity tag) {
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
        RecordManager.getInstance().save(RecordManager.DS_RECENT);
    }

    RecordManager getManager() {
        return RecordManager.getInstance();
    }

    public static final TagRecordSet create(String tag) {
        TagEntity tagEntity = null;
        if (!TextUtils.isEmpty(tag)) {
            tagEntity = TagEntity.create(tag);
        }

        TagRecordSet rs = create(tagEntity);

        return rs;
    }

    static final TagRecordSet create(TagEntity tag) {
        RecordManager mgr = RecordManager.getInstance();

        TagRecordSet rs = new TagRecordSet(tag);

        RecordDataset ds = mgr.getRecordDataset();
        int size = ds.size();
        if (size > 0) {
            rs.recordList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                RecordEntry en = ds.get(i);

                if (tag != null) {
                    if (en.indexOfTag(tag.getId()) < 0) {
                        continue;
                    }
                }

                RecordEntity r = new RecordEntity(en.getId(), en);
                if (r.isTrash()) {
                    continue;
                }

                rs.recordList.add(r);
            }
        }

        return rs;
    }
}
