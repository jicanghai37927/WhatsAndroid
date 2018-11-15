package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import club.andnext.utils.ColorUtils;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.whatsnote.record.dataset.TagEntry;

import java.awt.*;

public class TagEntity extends BaseEntitySet<TagEntity> {

    Color color;

    TagEntry entry;

    TagEntity(Context context, TagEntry entry) {
        super(context);

        this.entry = entry;
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
        if (entry == null) {
            return "";
        }

        return entry.getName();
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

}
