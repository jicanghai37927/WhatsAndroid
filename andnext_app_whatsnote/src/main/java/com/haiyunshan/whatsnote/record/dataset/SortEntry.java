package com.haiyunshan.whatsnote.record.dataset;

import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class SortEntry extends BaseEntry {

    @SerializedName("name")
    String name;

    public SortEntry() {

    }

    public String getName() {
        return name;
    }
}
