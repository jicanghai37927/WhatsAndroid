package com.haiyunshan.entrance;

import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

class EntranceEntry extends BaseEntry {

    static final String ID_NOTE     = "note";
    static final String ID_EXTRACT  = "extract";
    static final String ID_RECENT   = "recent";
    static final String ID_PREVIEW  = "preview";
    static final String ID_TRASH    = "trash";

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
