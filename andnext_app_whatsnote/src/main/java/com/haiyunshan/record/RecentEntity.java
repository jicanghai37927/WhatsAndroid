package com.haiyunshan.record;

import android.text.TextUtils;

import java.util.ArrayList;

public class RecentEntity {

    TagEntity tagEntity;

    ArrayList<RecordEntity> recordList;

    RecentEntity(TagEntity tag) {
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

    public static final boolean put(String id) {

        RecentDataset ds = RecordManager.getInstance().getRecentDataset();

        {
            RecentEntry entry = ds.get(id);
            if (entry == null) {
                entry = new RecentEntry(id);
                ds.add(entry);
            }

            entry.setModified(System.currentTimeMillis());
        }

        {
            RecordManager.getInstance().save(ds);
        }

        return true;
    }

    public static final RecentEntity obtain(String tag) {
        TagEntity tagEntity = null;
        if (!TextUtils.isEmpty(tag)) {
            tagEntity = TagEntity.create(tag);
        }

        RecentEntity entity = create(tagEntity);

        return entity;
    }

    static final RecentEntity create(TagEntity tag) {
        RecordManager mgr = RecordManager.getInstance();

        RecentEntity entity = new RecentEntity(tag);

        RecentDataset ds = mgr.getRecentDataset();
        int size = ds.size();
        if (size > 0) {
            entity.recordList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                RecentEntry e = ds.get(i);
                RecordEntry en = mgr.getRecordDataset().get(e.getId());
                if (en == null) {
                    continue;
                }

                if (tag != null) {
                    if (en.indexOfTag(tag.getId()) < 0) {
                        continue;
                    }
                }

                RecordEntity r = new RecordEntity(en.getId(), en);
                if (r.isTrash()) {
                    continue;
                }

                entity.recordList.add(r);
            }
        }

        return entity;
    }
}
