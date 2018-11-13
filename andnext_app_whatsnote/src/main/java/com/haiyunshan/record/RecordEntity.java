package com.haiyunshan.record;

import android.text.TextUtils;

import javax.swing.tree.TreeNode;
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
    String name;
    RecordEntry entry;

    ArrayList<RecordEntity> childList;

    RecordEntity(String id, RecordEntry entry) {
        this.id = id;
        this.entry = entry;

        this.childList = null;
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
        if (entry != null) {
            return getName(this.entry);
        }

        return (this.name == null)? "": name;
    }

    public void setName(String name) {
        if (entry != null) {
            entry.setName(name);
        }

        this.name = name;
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

    public void moveTo(String parent) {
        if (entry == null) {
            return;
        }

        entry.setParent(parent);
    }

    public int remove(String id, boolean delete) {
        RecordEntity entity = this.get(id);
        return remove(entity, delete);
    }

    public int remove(RecordEntity entity, boolean delete) {
        int index = this.indexOf(entity);
        if (index < 0) {
            return index;
        }

        childList.remove(entity);

        if (delete) {
            getManager().remove(entity.entry);
        }

        return index;
    }

    public boolean isDescendantOf(String id) {
        RecordEntry ancestor = this.entry;
        if (ancestor == null) {
            return false;
        }

        do {
            if (ancestor.getId().equals(id)) {
                return true;
            }
        } while((ancestor = getManager().getParent(ancestor)) != null);

        return false;

    }

    public int indexOf(RecordEntity entity) {
        if (childList == null) {
            return -1;
        }

        return childList.indexOf(entity);
    }

    public RecordEntity add(int type, String name) {
        RecordEntry entry = getManager().create(this.id, type);
        name = getManager().getName(entry, name);
        entry.setAlias(name);

        RecordEntity entity = new RecordEntity(entry.getId(), entry);
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
            return false;
        }

        return (entry.getType() == TYPE_FOLDER);
    }

    public boolean isTrash() {
        String root = getManager().getRoot(id);
        boolean result = root.equals(ROOT_TRASH);
        return result;
    }

    public boolean isExtract() {
        String root = getManager().getRoot(id);
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
        getManager().save(RecordManager.DS_ALL);
    }

    RecordManager getManager() {
        return RecordManager.getInstance();
    }

    static String getName(RecordEntry entry) {
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

    public static RecordEntity create(String id) {
        return create(id, TYPE_ALL);
    }

    public static RecordEntity create(String id, int childFlags) {

        RecordManager mgr = RecordManager.getInstance();
        RecordEntry entry = mgr.getRecordDataset().get(id);

        RecordEntity entity = new RecordEntity(id, entry);
        if (childFlags != TYPE_EMPTY) {

            List<RecordEntry> list = mgr.getList(id, childFlags, null);
            if (list != null && list.size() > 0) {
                entity.childList = new ArrayList<>(list.size());

                for (RecordEntry e : list) {
                    entity.childList.add(new RecordEntity(e.getId(), e));
                }
            }
            
        }

        return entity;
    }

}
