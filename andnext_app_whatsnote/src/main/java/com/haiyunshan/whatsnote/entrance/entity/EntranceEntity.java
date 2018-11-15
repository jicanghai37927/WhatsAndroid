package com.haiyunshan.whatsnote.entrance.entity;


import android.content.Context;
import com.haiyunshan.whatsnote.entrance.dataset.EntranceDataset;
import com.haiyunshan.whatsnote.entrance.dataset.EntranceEntry;

import java.util.ArrayList;

public class EntranceEntity {

    EntranceEntry entry;

    ArrayList<EntranceEntity> childList;

    Context context;

    private EntranceEntity(Context context, EntranceEntry entry) {
        this.context = context.getApplicationContext();

        this.entry = entry;

        this.childList = null;
    }

    public EntranceEntity get(int index) {
        if (childList == null) {
            return null;
        }

        return childList.get(index);
    }

    public int size() {
        if (childList == null) {
            return 0;
        }

        return childList.size();
    }

    public String getId() {
        return entry.getId();
    }

    public String getName() {
        return entry.getName();
    }

    public boolean isVisible() {
        return entry.isVisible();
    }

    public void setVisible(boolean value) {
        entry.setVisible(value);
    }

    public boolean isEditable() {
        return entry.isEditable();
    }

    public boolean isMovable() {
        return entry.isMovable();
    }

    public void save() {
        getManager().save();
    }

    EntranceManager getManager() {
        return EntranceManager.getInstance(context);
    }

    public static final EntranceEntity obtain(Context context) {
        EntranceEntity entity = new EntranceEntity(context, null);

        EntranceManager mgr = EntranceManager.getInstance(context);
        EntranceDataset ds = mgr.getDataset();
        entity.childList = new ArrayList<>(ds.size() + 1);
        for (int i = 0, size = ds.size(); i < size; i++) {
            EntranceEntry e = ds.get(i);

            EntranceEntity en = new EntranceEntity(context, e);
            entity.childList.add(en);
        }

        return entity;
    }
}
