package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.Nullable;
import club.andnext.utils.ColorUtils;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.record.dataset.TagEntry;

import java.awt.*;
import java.util.ArrayList;

public class TagEntity extends BaseEntitySet<TagEntity> {

    Color color;

    String name;

    TagEntry entry;

    public static final TagEntity obtain() {
        return TagFactory.obtain(WhatsApp.getContext());
    }

    public static final TagEntity copy() {
        return new TagEntity(obtain());
    }

    TagEntity(Context context, TagEntry entry) {
        super(context);

        this.entry = entry;
    }

    TagEntity(TagEntity entity) {
        this(entity.context, entity.entry);

        this.color = entity.color;
        this.name = entity.getName();

        if (entity.childList != null && !entity.childList.isEmpty()) {
            this.childList = new ArrayList<>(entity.childList.size());
            for (int i = 0, size = entity.childList.size(); i < size; i++) {
                TagEntity en = new TagEntity(entity.childList.get(i));

                this.childList.add(en);
            }
        }
    }

    public String getId() {
        if (entry == null) {
            return "";
        }

        return entry.getId();
    }

    public int getColor() {
        if (entry == null) {
            return android.graphics.Color.TRANSPARENT;
        }

        if (color != null) {
            return color.getRGB();
        }

        String text = entry.getColor();
        this.color = new Color(ColorUtils.parse(text), true);
        return color.getRGB();
    }

    public String getName() {
        if (name != null) {
            return name;
        }


        if (entry == null) {
            return "";
        }

        return entry.getName();
    }

    public int indexOf(String id) {
        if (childList == null) {
            return -1;
        }

        for (int i = 0, size = childList.size(); i < size; i++) {
            if (childList.get(i).getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    public TagEntity add(String name, int color) {
        int index = 0;

        TagEntry entry = new TagEntry(UUIDUtils.next(), name, ColorUtils.format(color));

        {
            getManager().getTagDataset().add(index, entry);
        }

        {
            TagEntity entity = new TagEntity(this.context, entry);
            this.add(index, entity);

            return entity;
        }
    }

    public int getDisplayColor() {
        int color = this.getColor();

        return TagUtils.getDisplayColor(context, color);
    }

    @Override
    public void save() {
        getManager().save(RecordManager.DS_TAG);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof TagEntity)) {
            return false;
        }

        TagEntity another = (TagEntity)obj;

        boolean result = true;
        if (result) {
            String name1 = this.getName();
            String name2 = another.getName();

            result = name1.equals(name2);
        }

        if (result) {
            result = (this.getColor() == another.getColor());
        }

        return result;
    }

}
