package com.haiyunshan.whatsnote.record.dataset;

import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class TagEntry extends BaseEntry {

    @SerializedName("name")
    String name;

    @SerializedName("color")
    String color;

    public TagEntry(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name == null? "": name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color == null? "": color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
