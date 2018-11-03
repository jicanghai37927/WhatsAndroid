package com.haiyunshan.record;

import android.graphics.Color;
import android.text.TextUtils;
import club.andnext.dataset.BaseEntry;
import club.andnext.utils.ColorUtils;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class TagEntry extends BaseEntry {

    @SerializedName("name")
    String name;

    @SerializedName("color")
    String color;

    transient Integer colorValue;

    public TagEntry(String id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = ColorUtils.format(color);

        this.colorValue = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        if (colorValue == null) {
            if (TextUtils.isEmpty(color)) {
                colorValue = Color.TRANSPARENT;
            } else {
                colorValue = ColorUtils.parse(color);
            }
        }

        return colorValue;
    }

    public void setColor(int color) {
        if (colorValue == null || colorValue != color) {
            this.color = ColorUtils.format(color);
            colorValue = color;
        }
    }
}
