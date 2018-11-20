package com.haiyunshan.whatsnote.record.dataset;

import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class SavedStateEntry extends BaseEntry {

    @SerializedName("position")
    String offsetId;

    @SerializedName("top")
    String offsetTop;

    @SerializedName("offset")
    String textOffset;

    @SerializedName("focus")
    String focusId;

    @SerializedName("start")
    String selectionStart;

    @SerializedName("end")
    String selectionEnd;

    public SavedStateEntry(String id) {
        this.id = id;
    }

    public String getOffsetId() {
        return offsetId;
    }

    public void setOffsetId(String offsetId) {
        this.offsetId = offsetId;
    }

    public String getOffsetTop() {
        return offsetTop;
    }

    public void setOffsetTop(String offsetTop) {
        this.offsetTop = offsetTop;
    }

    public String getTextOffset() {
        return textOffset;
    }

    public void setTextOffset(String textOffset) {
        this.textOffset = textOffset;
    }

    public String getFocusId() {
        return focusId;
    }

    public void setFocusId(String focusId) {
        this.focusId = focusId;
    }

    public String getSelectionStart() {
        return selectionStart;
    }

    public void setSelectionStart(String selectionStart) {
        this.selectionStart = selectionStart;
    }

    public String getSelectionEnd() {
        return selectionEnd;
    }

    public void setSelectionEnd(String selectionEnd) {
        this.selectionEnd = selectionEnd;
    }
}
