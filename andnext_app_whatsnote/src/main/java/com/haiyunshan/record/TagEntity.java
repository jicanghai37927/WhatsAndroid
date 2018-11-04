package com.haiyunshan.record;

import android.graphics.Color;
import club.andnext.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.List;

public class TagEntity {

    String id;
    TagEntry entry;

    ArrayList<TagEntity> childList;

    RecordManager recordManager;

    TagEntity(String id, TagEntry entry, RecordManager mgr) {
        this.id = id;
        this.entry = entry;

        this.childList = null;

        this.recordManager = mgr;
    }

    public String getId() {
        if (entry == null) {
            return id;
        }

        return entry.getId();
    }

    public int getColor() {
        if (entry == null) {
            return Color.BLACK;
        }

        return entry.getColor();
    }

    public String getName() {
        if (entry == null) {
            return "";
        }

        return entry.getName();
    }

    public List<TagEntity> getList() {
        return childList;
    }

    public TagEntity get(int index) {
        if (childList == null) {
            return null;
        }

        return childList.get(index);
    }

    public int size() {
        if (childList == null) {
            return 0;
        }

        return childList.size();
    }

    public TagEntity add(String name, int color) {
        TagEntry entry = new TagEntry(UUIDUtils.next(), name, color);

        {
            recordManager.getTagDataset().add(entry);
        }

        {
            TagEntity entity = new TagEntity(entry.getId(), entry, this.recordManager);
            if (childList == null) {
                childList = new ArrayList<>();
            }

            childList.add(entity);

            return entity;
        }
    }

    public int getOrder() {
        if (entry == null) {
            return Integer.MAX_VALUE;
        }

        List<String> list = recordManager.getTagDataset().getOrderList();
        if (list == null) {
            return Integer.MAX_VALUE;
        }

        int index = list.indexOf(entry.getId());
        if (index < 0) {
            return Integer.MAX_VALUE;
        }

        return index;
    }

    public void save() {
        recordManager.save(RecordManager.DS_TAG);
    }

    public static final boolean exist(String name) {
        RecordManager mgr = RecordManager.getInstance();
        TagDataset ds = mgr.getTagDataset();
        for (int i = 0, size = ds.size(); i < size; i++) {
            if (ds.get(i).getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static final TagEntity obtain() {
        RecordManager mgr = RecordManager.getInstance();
        if (mgr.tagEntity != null) {
            return mgr.tagEntity;
        }

        mgr.tagEntity = create();
        return mgr.tagEntity;
    }

    static final TagEntity create() {

        String id = "/";
        TagEntry entry = null;
        RecordManager mgr = RecordManager.getInstance();

        TagEntity entity = new TagEntity(id, entry, mgr);
        TagDataset ds = mgr.getTagDataset();
        int size = ds.size();
        if (size != 0) {
            entity.childList = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                TagEntry e = ds.get(i);

                TagEntity en = new TagEntity(e.getId(), e, mgr);
                entity.childList.add(en);
            }
        }

        return entity;
    }

    static final TagEntity create(String id) {
        RecordManager mgr = RecordManager.getInstance();
        TagDataset ds = mgr.getTagDataset();
        TagEntry entry = ds.get(id);
        if (entry == null) {
            return null;
        }

        TagEntity entity = new TagEntity(id, entry, mgr);
        return entity;
    }
}
