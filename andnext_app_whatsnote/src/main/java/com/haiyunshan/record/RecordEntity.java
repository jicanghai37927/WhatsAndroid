package com.haiyunshan.record;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class RecordEntity {

    public static final int TYPE_FOLDER = RecordManager.TYPE_FOLDER;
    public static final int TYPE_NOTE   = RecordManager.TYPE_NOTE;
    public static final int TYPE_ALL    = RecordManager.TYPE_ALL;
    public static final int TYPE_EMPTY  = RecordManager.TYPE_EMPTY;

    public static final String ROOT_NOTE    = RecordManager.ROOT_NOTE;
    public static final String ROOT_EXTRACT   = RecordManager.ROOT_EXTRACT;
    public static final String ROOT_TRASH   = RecordManager.ROOT_TRASH;

    String id;
    RecordEntry entry;

    ArrayList<RecordEntity> childList;

    RecordManager recordManager;

    RecordEntity(String id, RecordEntry entry, RecordManager mgr) {
        this.id = id;
        this.entry = entry;

        this.childList = null;

        this.recordManager = mgr;
    }

    public String getId() {
        return this.id;
    }

    public String getParent() {
        if (entry != null) {
            return entry.getParent();
        }

        return "";
    }

    public String getName() {
        if (entry == null) {
            return "";
        }

        if (!TextUtils.isEmpty(entry.getName())) {
            return entry.getName();
        }

        if (!TextUtils.isEmpty(entry.getAlias())) {
            return entry.getAlias();
        }

        return "";
    }

    public void setName(String name) {
        if (entry != null) {
            entry.setName(name);
        }
    }

    public RecordEntity get(String id) {
        if (childList == null) {
            return null;
        }

        for (RecordEntity e : childList) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }

    public RecordEntity get(int index) {
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

    public int remove(RecordEntity entity) {
        int index = this.indexOf(entity);
        if (index < 0) {
            return index;
        }

        recordManager.remove(entity.entry);
        childList.remove(entity);

        return index;
    }

    public int indexOf(RecordEntity entity) {
        if (childList == null) {
            return -1;
        }

        return childList.indexOf(entity);
    }

    public RecordEntity add(int type, String name) {
        RecordEntry entry = recordManager.create(this.id, type);
        name = recordManager.getName(entry, name);
        entry.setAlias(name);

        RecordEntity entity = new RecordEntity(entry.getId(), entry, this.recordManager);
        this.add(entity);

        return entity;
    }

    public long getCreated() {
        if (entry == null) {
            return 0;
        }

        return entry.getCreated();
    }

    public void setCreated(long created) {
        if (entry == null) {
            return;
        }

        entry.setCreated(created);
    }

    public void addTag(String tag) {
        if (entry == null) {
            return;
        }

        int index = indexOfTag(tag);
        if (index < 0) {
            List<String> list = entry.getTagList();
            if (list == null) {
                list = new ArrayList<>();
                entry.setTagList(list);
            }

            list.add(tag);
        }
    }

    public int removeTag(String tag) {
        if (entry == null) {
            return -1;
        }

        int index = indexOfTag(tag);
        if (index < 0) {
            return index;
        }

        entry.getTagList().remove(index);
        return index;
    }

    public int indexOfTag(String tag) {
        if (entry == null) {
            return -1;
        }

        List<String> list = entry.getTagList();
        if (list == null) {
            return -1;
        }

        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i).equals(tag)) {
                return i;
            }
        }

        return -1;
    }

    public boolean isDirectory() {
        if (entry == null) {
            return true;
        }

        return (entry.getType() == TYPE_FOLDER);
    }

    public boolean isTrash() {
        String root = recordManager.getRoot(id);
        boolean result = root.equals(ROOT_TRASH);
        return result;
    }

    public boolean isExtract() {
        String root = recordManager.getRoot(id);
        boolean result = root.equals(ROOT_EXTRACT);
        return result;
    }

    public boolean isEditable() {
        return !(isTrash() || isExtract());
    }

    void add(RecordEntity entity) {
        if (childList == null) {
            childList = new ArrayList<>();
        }

        childList.add(entity);
    }

    public void save() {
        recordManager.save(RecordManager.DS_ALL);
    }

    public static RecordEntity obtain(String id) {
        return create(id, TYPE_ALL);
    }

    public static RecordEntity obtain(String id, int childFlags) {
        return create(id, childFlags);
    }

    static RecordEntity create(String id, int childFlags) {

        RecordManager mgr = RecordManager.getInstance();
        RecordEntry entry = mgr.getRecordDataset().get(id);

        RecordEntity entity = new RecordEntity(id, entry, mgr);
        if (childFlags != TYPE_EMPTY) {

            List<RecordEntry> list = mgr.getList(id, childFlags, null);
            if (list != null && list.size() > 0) {
                entity.childList = new ArrayList<>(list.size());

                for (RecordEntry e : list) {
                    entity.childList.add(new RecordEntity(e.getId(), e, mgr));
                }
            }
            
        }

        return entity;
    }

}
