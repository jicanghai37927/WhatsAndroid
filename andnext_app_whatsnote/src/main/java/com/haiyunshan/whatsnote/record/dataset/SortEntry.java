package com.haiyunshan.whatsnote.record.dataset;

import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class SortEntry extends BaseEntry {

    @SerializedName("name")
    String name;

    @SerializedName("visible")
    boolean visible;

    public SortEntry() {

    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }
}
