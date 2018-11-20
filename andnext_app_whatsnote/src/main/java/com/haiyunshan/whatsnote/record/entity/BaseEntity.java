package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;

public abstract class BaseEntity {

    Context context;

    BaseEntity(Context context) {
        this.context = context.getApplicationContext();
    }

    public abstract String getId();

    RecordManager getManager() {
        return RecordManager.getInstance(context);
    }

}
