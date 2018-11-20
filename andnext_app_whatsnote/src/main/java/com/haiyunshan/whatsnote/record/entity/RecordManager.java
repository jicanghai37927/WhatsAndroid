package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import club.andnext.dataset.BaseDataset;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.whatsnote.record.dataset.*;
import com.haiyunshan.whatsnote.directory.DirectoryManager;
import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class RecordManager {

    public static final String RECORD_FOLDER  = "folder";
    public static final String RECORD_NOTE    = "note";

    public static final int TYPE_FOLDER = 1;
    public static final int TYPE_NOTE   = 1 << 2;
    public static final int TYPE_ALL    = TYPE_FOLDER | TYPE_NOTE;
    public static final int TYPE_EMPTY  = 0;

    public static final String ROOT_NOTE    = ".note";
    public static final String ROOT_TRASH   = ".trash";
    public static final String ROOT_EXTRACT = ".extract";

    OptionEntity optionEntity;  //
    TagEntity tagEntity;
    FavoriteEntity favoriteEntity;

    HashMap<Class<? extends BaseDataset>, File> fileMap;
    HashMap<Class<? extends BaseDataset>, BaseDataset> datasetMap;

    static RecordManager instance;

    static final RecordManager getInstance(Context context) {
        if (instance == null) {
            instance = new RecordManager(context);
        }

        return instance;
    }

    private RecordManager(Context context) {
        {
            this.fileMap = new HashMap<>();

            File dir = DirectoryManager.getInstance().getDirectory(context, DirectoryManager.DIR_NOTE);

            fileMap.put(RecordDataset.class, new File(dir, "record_ds.json"));

            fileMap.put(TagDataset.class, new File(dir, "tag_ds.json"));
            fileMap.put(FavoriteDataset.class, new File(dir, "favorite_ds.json"));
            fileMap.put(RecentDataset.class, new File(dir, "recent_ds.json"));
            fileMap.put(SearchDataset.class, new File(dir, "search_ds.json"));
            fileMap.put(SavedStateDataset.class, new File(dir, "saved_state_ds.json"));

            fileMap.put(OptionDataset.class, new File(dir, "option_ds.json"));
        }

        {
            this.datasetMap = new HashMap<>();
        }
    }

    /**
     *
     * @param parent
     * @param list
     * @return
     */
    public List<RecordEntry> getList(String parent, int type, List<RecordEntry> list) {
        list = (list == null)? new ArrayList<RecordEntry>(): list;
        list.clear();

        if (parent == null) {
            return list;
        }

        RecordDataset ds = getRecordDataset();
        for (int i = 0, size = ds.size(); i < size; i++) {
            RecordEntry e = ds.get(i);
            if (e.getParent().equals(parent)) {

                if ((RecordFactory.getType(e) & type) != 0) {
                    list.add(e);
                }

            }
        }

        return list;
    }

    public RecordEntry create(String parent, int type) {

        boolean accept = (type == TYPE_FOLDER) || (type == TYPE_NOTE);
        if (!accept) {
            throw new IllegalArgumentException("type should be FOLDER or NOTE.");
        }

        String id = UUIDUtils.next();

        RecordDataset ds = getRecordDataset();
        RecordEntry entry = new RecordEntry(id, parent, RecordFactory.getType(type));
        ds.add(entry);

        return entry;

    }

    public String getName(@NonNull RecordEntry entry, @NonNull  String name) {

        int type = RecordFactory.getType(entry);
        String parent = getRecordDataset().get(entry.getId()).getParent();

        List<RecordEntry> list = getList(parent, type, null);
        String r = getName(name, list);

        return r;
    }

    public RecordEntry getParent(RecordEntry entry) {
        if (entry == null) {
            return null;
        }

        String parentId = entry.getParent();
        entry = getRecordDataset().get(parentId);
        return entry;
    }

    public String getRoot(String id) {
        RecordEntry entry = getRecordDataset().get(id);
        if (entry == null) {
            return id;
        }

        String parent = entry.getParent();
        while (true) {
            if (TextUtils.isEmpty(parent)) {
                parent = entry.getId();
                break;
            }

            entry = getRecordDataset().get(parent);
            if (entry == null) {
                break;
            }

            parent = entry.getParent();
        }

        return parent;
    }

    public boolean isTrash(String id) {
        String root = this.getRoot(id);
        boolean result = root.equals(ROOT_TRASH);
        return result;
    }

    public void remove(RecordEntry entry) {
        if (entry == null) {
            return;
        }

        if (isTrash(entry.getId())) {

            RecordDataset recordDs = getRecordDataset();

            List<RecordEntry> list = this.getList(entry.getId(), TYPE_ALL, null);
            for (RecordEntry e : list) {
                this.remove(e);
            }

            {
                recordDs.remove(entry);
            }

        } else {

            entry.setAncestor(entry.getParent());

            entry.setParent(ROOT_TRASH);
            entry.setDeleted(DateTime.now().toString());
        }

    }

    String getName(String name, List<RecordEntry> list) {
        if (indexOfName(name, list) < 0) {
            return name;
        }

        int index = 2; // from 2
        while (true) {
            String text = name + " " + index;
            if (indexOfName(text, list) < 0) {
                return text;
            }

            ++index;
        }
    }

    int indexOfName(String name, List<RecordEntry> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            RecordEntry e = list.get(i);
            if (RecordEntity.getName(e).equals(name)) {
                return i;
            }
        }

        return -1;
    }

    RecordDataset getRecordDataset() {
        if (datasetMap.get(RecordDataset.class) != null) {
            RecordDataset ds = getDataset(RecordDataset.class);

            return ds;
        }

        RecordDataset ds = getDataset(RecordDataset.class);

        for (int i = 0, size = ds.size(); i < size; i++) {
            RecordEntry entry = ds.get(i);
            {
                String time = entry.getCreated();
                if (!TextUtils.isEmpty(time)) {
                    if (time.indexOf('T') < 0) {
                        DateTime dateTime = new DateTime(Long.parseLong(time));
                        entry.setCreated(dateTime.toString());
                    }
                }
            }

            {
                String time = entry.getModified();
                if (!TextUtils.isEmpty(time)) {
                    if (time.indexOf('T') < 0) {
                        DateTime dateTime = new DateTime(Long.parseLong(time));
                        entry.setModified(dateTime.toString());
                    }
                }
            }

            {
                String time = entry.getDeleted();
                if (!TextUtils.isEmpty(time)) {
                    if (time.indexOf('T') < 0) {
                        DateTime dateTime = new DateTime(Long.parseLong(time));
                        entry.setDeleted(dateTime.toString());
                    }
                }
            }
        }

        return ds;
    }

    TagDataset getTagDataset() {
        TagDataset ds = getDataset(TagDataset.class);
        return ds;
    }


    FavoriteDataset getFavoriteDataset() {
        FavoriteDataset ds = getDataset(FavoriteDataset.class);
        return ds;
    }

    RecentDataset getRecentDataset() {
        RecentDataset ds = getDataset(RecentDataset.class);
        return ds;
    }

    SearchDataset getSearchDataset() {
        SearchDataset ds = getDataset(SearchDataset.class);
        return ds;
    }

    SavedStateDataset getSavedStateDataset() {
        SavedStateDataset ds = getDataset(SavedStateDataset.class);
        return ds;
    }

    void save() {
        Iterator<BaseDataset> iter = datasetMap.values().iterator();
        while (iter.hasNext()) {
            BaseDataset ds = iter.next();
            this.save(ds);
        }
    }

    void save(Class<? extends BaseDataset> kind) {
        BaseDataset ds = datasetMap.get(kind);
        if (ds != null) {
            save(ds);
        }
    }

    void save(BaseDataset dataset) {
        if (dataset == null) {
            return;
        }

        Class clz = dataset.getClass();
        File file = fileMap.get(clz);
        if (file == null) {
            throw new IllegalArgumentException("cannot find file path for " + clz);
        }

        GsonUtils.toJson(dataset, file);
    }

    final <T extends BaseDataset> T getDataset(Class<T> kind) {
        T ds = (T)datasetMap.get(kind);
        if (ds != null) {
            return ds;
        }

        ds = createDataset(kind);
        datasetMap.put(kind, ds);

        return ds;
    }

    <T> T createDataset(Class<T> type) {
        File file = fileMap.get(type);
        if (file == null) {
            throw new IllegalArgumentException("cannot find file path for " + type);
        }

        T obj = null;
        if (file.exists()) {
            obj = GsonUtils.fromJson(file, type);
        }

        if (obj == null) {
            try {
                obj = type.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

}
