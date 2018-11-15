package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.haiyunshan.whatsnote.record.dataset.RecordEntry;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class RecordEntity extends BaseEntitySet<RecordEntity> {

    public static final int TYPE_FOLDER = RecordManager.TYPE_FOLDER;
    public static final int TYPE_NOTE   = RecordManager.TYPE_NOTE;
    public static final int TYPE_ALL    = RecordManager.TYPE_ALL;
    public static final int TYPE_EMPTY  = RecordManager.TYPE_EMPTY;

    public static final String ROOT_NOTE    = RecordManager.ROOT_NOTE;
    public static final String ROOT_EXTRACT   = RecordManager.ROOT_EXTRACT;
    public static final String ROOT_TRASH   = RecordManager.ROOT_TRASH;

    String id;
    String name;

    DateTime created;
    DateTime modified;
    DateTime deleted;

    RecordEntry entry;

    RecordEntity(Context context, String id, RecordEntry entry) {
        super(context);

        this.id = id;
        this.name = RecordEntity.getName(entry);
        this.entry = entry;
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
        return this.name;
    }

    public void setName(String name) {
        this.name = (name == null)? "": name;

        if (entry != null) {
            entry.setName(name);
        }
    }

    public void setAlias(String name) {
        if (entry != null) {
            entry.setAlias(name);
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

    public void moveTo(String parent) {
        if (entry == null) {
            return;
        }

        entry.setParent(parent);
    }

    public int remove(String id, boolean delete) {
        RecordEntity entity = this.get(id);

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

    public RecordEntity add(int type, String name) {
        RecordEntry entry = getManager().create(this.id, type);
        name = getManager().getName(entry, name);
        entry.setAlias(name);

        RecordEntity entity = new RecordEntity(this.context, entry.getId(), entry);
        this.add(entity);

        return entity;
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

        return (RecordFactory.getType(entry) == TYPE_FOLDER);
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof RecordEntity)) {
            return false;
        }

        boolean result = true;
        RecordEntity another = (RecordEntity)obj;

        if (result) {
            result = this.getName().equals(another.getName());
        }

        return result;
    }

    public boolean isEditable() {
        return !(isTrash() || isExtract());
    }

    public DateTime getCreated() {
        if (created != null) {
            return created;
        }

        if (entry == null) {
            created = DateTime.now();
            return created;
        }

        String time = entry.getCreated();
        if (TextUtils.isEmpty(time)) {
            time = DateTime.now().toString();

            entry.setCreated(time);
            entry.setModified(time);
        }

        if (time.indexOf('T') > 0) {
            created = DateTime.parse(time);
        } else {
            created = new DateTime(Long.parseLong(time));
            entry.setCreated(created.toString());
        }

        return created;
    }

    public void setCreated(long time) {
        if (entry == null) {
            return;
        }

        this.created = new DateTime(time);
        entry.setCreated(created.toString());
    }

    public DateTime getModified() {

        if (modified != null) {
            return modified;
        }

        if (entry == null) {
            modified = DateTime.now();

            return modified;
        }

        String time = entry.getModified();
        if (TextUtils.isEmpty(time)) {
            time = DateTime.now().toString();

            entry.setModified(time);
        }

        if (time.indexOf('T') > 0) {
            modified = DateTime.parse(time);
        } else {
            modified = new DateTime(Long.parseLong(time));
            entry.setModified(modified.toString());
        }

        return modified;
    }

    public void setModified(long time) {
        if (entry == null) {
            return;
        }

        this.modified = new DateTime(time);
        entry.setModified(modified.toString());
    }

    @Override
    public void save() {
        getManager().save(RecordManager.DS_ALL);
    }

    static final String getName(RecordEntry e) {
        if (e == null) {
            return "";
        }

        if (!TextUtils.isEmpty(e.getName())) {
            return e.getName();
        }

        if (!TextUtils.isEmpty(e.getAlias())) {
            return e.getAlias();
        }

        return "";
    }

}
