package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.graphics.Color;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.whatsnote.record.dataset.TagDataset;
import com.haiyunshan.whatsnote.record.dataset.TagEntry;

import java.util.ArrayList;
import java.util.List;

public class TagEntity {

    String id;
    TagEntry entry;

    ArrayList<TagEntity> childList;

    Context context;

    TagEntity(Context context, String id, TagEntry entry) {
        this.context = context.getApplicationContext();

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
            TagEntity entity = new TagEntity(this.context, entry.getId(), entry);
            if (childList == null) {
                childList = new ArrayList<>();
            }

            childList.add(index, entity);

            return entity;
        }
    }

    public int getDisplayColor() {
        int color = this.getColor();

        return TagUtils.getDisplayColor(context, color);
    }

    public void save() {
        getManager().save(RecordManager.DS_TAG);
    }

    RecordManager getManager() {
        return RecordManager.getInstance(context);
    }

}
