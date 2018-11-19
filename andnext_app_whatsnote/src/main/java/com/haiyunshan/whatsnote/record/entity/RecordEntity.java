package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.article.entity.Document;
import com.haiyunshan.whatsnote.record.dataset.RecordDataset;
import com.haiyunshan.whatsnote.record.dataset.RecordEntry;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.Collections;
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

    Long size;

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

    public long getSize() {
        if (this.size != null) {
            return size;
        }

        long value = 0;
        if (this.isDirectory()) {
            RecordDataset ds = getManager().getRecordDataset();
            for (int i = 0, size = ds.size(); i < size; i++) {
                RecordEntry e = ds.get(i);
                if (e.getParent().equals(this.getId())) {
                    ++value;
                }
            }
        } else {
            value = Document.size(context, this.getId());
        }

        size = Long.valueOf(value);
        return size;
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

    public List<String> getTagList() {
        List<String> list = null;
        if (entry != null) {
            list = entry.getTagList();
        }

        if (list == null) {
            list = Collections.emptyList();
        }

        return list;
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

        if (result) {
            result = this.getSize() == another.getSize();
        }

        if (result) {
            result = this.getCreated().equals(another.getCreated());
        }

        if (result) {
            result = (Minutes.minutesBetween(this.getModified(), another.getModified()).getMinutes() == 0);
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
        getManager().save();
    }

    public static final CharSequence format(Context context, DateTime time) {
        StringBuilder sb = new StringBuilder();

        int days = Days.daysBetween(time.toLocalDate(), LocalDate.now()).getDays();
        if (days == 0) {

            sb.append(String.format("%02d", time.getHourOfDay()));
            sb.append(':');
            sb.append(String.format("%02d", time.getMinuteOfHour()));

        } else if (days == 1) {
            sb.append("昨天");
        } else if (days == 2) {
            sb.append("前天");
        } else {
            sb.append(time.getYear());
            sb.append('/');
            sb.append(time.getMonthOfYear());
            sb.append('/');
            sb.append(time.getDayOfMonth());
        }

        return sb;
    }

    static final String getName(Context context, String id) {
        RecordEntry e = RecordManager.getInstance(context).getRecordDataset().get(id);
        return RecordEntity.getName(e);
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

    public static RecordEntity create(String id) {
        return create(id, TYPE_ALL);
    }

    public static RecordEntity create(String id, int childFlags) {
        return RecordFactory.create(WhatsApp.getContext(), id, childFlags);
    }
}
