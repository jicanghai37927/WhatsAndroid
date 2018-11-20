package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class BaseEntitySet<T extends BaseEntity> extends BaseEntity {

    static List<BaseEntity> EMPTY_LIST;

    Context context;

    ArrayList<T> childList;

    {
        EMPTY_LIST = Collections.emptyList();
    }

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

    public int remove(T entity) {
        if (childList == null) {
            return -1;
        }

        int index = childList.indexOf(entity);
        if (index < 0) {
            return index;
        }

        childList.remove(index);
        return index;
    }

    public Collection<T> getCollection() {
        if (childList != null) {
            return childList;
        }

        return (Collection<T>)EMPTY_LIST;
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
