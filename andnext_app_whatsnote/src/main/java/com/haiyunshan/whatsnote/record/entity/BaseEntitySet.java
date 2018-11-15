package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class BaseEntitySet<T> {

    Context context;

    ArrayList<T> childList;

    BaseEntitySet(Context context) {
        this.context = context.getApplicationContext();

        this.childList = null;
    }

    public T get(int index) {
        if (childList == null) {
            return null;
        }

        return childList.get(index);
    }

    public int indexOf(T entity) {
        if (childList == null) {
            return -1;
        }

        return childList.indexOf(entity);
    }

    public int size() {
        if (childList == null) {
            return 0;
        }

        return childList.size();
    }

    public Collection<T> getCollection() {
        if (childList != null) {
            return childList;
        }

        return (Collection<T>)(Collections.emptyList());
    }

    public abstract void save();

    void add(int index, T entity) {
        if (childList == null) {
            childList = new ArrayList<>(1024);
        }

        childList.add(index, entity);
    }

    void add(T entity) {
        if (childList == null) {
            childList = new ArrayList<>(1024);
        }

        childList.add(entity);
    }

    RecordManager getManager() {
        return RecordManager.getInstance(context);
    }

}
