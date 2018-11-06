package com.haiyunshan.record;

import android.graphics.Color;
import club.andnext.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.List;

public class TagEntity {

    String id;
    TagEntry entry;

    ArrayList<TagEntity> childList;

    TagEntity(String id, TagEntry entry) {
        this.id = id;
        this.entry = entry;

        this.childList = null;
    }

    public String getId() {
        if (entry == null) {
            return id;
        }

        return entry.getId();
    }

    public int getColor() {
        if (entry == null) {
            return Color.TRANSPARENT;
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

    public int indexOf(TagEntity entity) {
        return childList.indexOf(entity);
    }

    public TagEntity add(String name, int color) {
        int index = 0;

        TagEntry entry = new TagEntry(UUIDUtils.next(), name, color);

        {
            getManager().getTagDataset().add(index, entry);
        }

        {
            TagEntity entity = new TagEntity(entry.getId(), entry);
            if (childList == null) {
                childList = new ArrayList<>();
            }

            childList.add(index, entity);

            return entity;
        }
    }

    public int getDrawable() {
        int color = this.getColor();

        return TagUtils.getDrawable(color);
    }

    public int getDisplayColor() {
        int color = this.getColor();

        return TagUtils.getDisplayColor(color);
    }

    public void save() {
        getManager().save(RecordManager.DS_TAG);
    }

    RecordManager getManager() {
        return RecordManager.getInstance();
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

        TagEntity entity = new TagEntity(id, entry);
        TagDataset ds = mgr.getTagDataset();
        int size = ds.size();
        if (size != 0) {
            entity.childList = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                TagEntry e = ds.get(i);

                TagEntity en = new TagEntity(e.getId(), e);
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

        TagEntity entity = new TagEntity(id, entry);
        return entity;
    }
}
