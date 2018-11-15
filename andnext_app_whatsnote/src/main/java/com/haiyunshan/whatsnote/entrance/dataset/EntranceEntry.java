package com.haiyunshan.whatsnote.entrance.dataset;

import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class EntranceEntry extends BaseEntry {

    public static final String ID_NOTE     = "note";
    public static final String ID_EXTRACT  = "extract";
    public static final String ID_RECENT   = "recent";
    public static final String ID_PREVIEW  = "preview";
    public static final String ID_TRASH    = "trash";

    @SerializedName("name")
    String name;

    @SerializedName("visible")
    boolean visible;

    @SerializedName("editable")
    boolean editable;

    @SerializedName("movable")
    boolean movable;

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isMovable() {
        return movable;
    }

}
