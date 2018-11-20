package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.record.dataset.SavedStateEntry;

public class SavedStateEntity extends BaseEntity {

    SavedStateEntry entry;

    SavedStateEntity(Context context, SavedStateEntry entry) {
        super(context);

        this.entry = entry;
    }

    @Override
    public String getId() {
        return entry.getId();
    }

    public void setOffset(String id, int top, int textOffset) {
        entry.setOffsetId(id);
        entry.setOffsetTop(top == 0? null: String.valueOf(top));
        entry.setTextOffset(textOffset == 0? null: String.valueOf(textOffset));
    }

    public void setFocus(String id, int start, int end) {
        entry.setFocusId(id);
        entry.setSelectionStart(start < 0? null: String.valueOf(start));
        entry.setSelectionEnd(end < 0? null: String.valueOf(end));
    }

    public String getOffsetId() {
        String id = entry.getOffsetId();
        return (id == null)? "": id;
    }

    public int getOffsetTop() {
        String value = entry.getOffsetTop();
        return (TextUtils.isEmpty(value))? 0: Integer.parseInt(value);
    }

    public int getTextOffset() {
        String value = entry.getTextOffset();
        return (TextUtils.isEmpty(value))? 0: Integer.parseInt(value);
    }

    public String getFocusId() {
        String id = entry.getFocusId();
        return (id == null)? "": id;
    }

    public int getSelectionStart() {
        String value = entry.getSelectionStart();
        return (TextUtils.isEmpty(value))? -1: Integer.parseInt(value);
    }

    public int getSelectionEnd() {
        String value = entry.getSelectionEnd();
        return (TextUtils.isEmpty(value))? -1: Integer.parseInt(value);
    }

    public void clear() {
        entry.setOffsetId(null);
        entry.setOffsetTop(null);
        entry.setTextOffset(null);

        entry.setFocusId(null);
        entry.setSelectionStart(null);
        entry.setSelectionEnd(null);
    }

    public static final SavedStateEntity create(String id) {
        return SavedStateFactory.create(WhatsApp.getContext(), id);
    }
}
