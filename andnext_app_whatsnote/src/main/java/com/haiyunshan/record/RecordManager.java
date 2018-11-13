package com.haiyunshan.record;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import club.andnext.dataset.BaseDataset;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.storage.LocalStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class RecordManager {

    public static final int TYPE_FOLDER = RecordEntry.TYPE_FOLDER;
    public static final int TYPE_NOTE   = RecordEntry.TYPE_NOTE;
    public static final int TYPE_ALL    = TYPE_FOLDER | TYPE_NOTE;
    public static final int TYPE_EMPTY  = 0;

    public static final int DS_RECORD       = 0x01;
    public static final int DS_TAG          = 0x01 << 1;
    public static final int DS_FAVORITE     = 0x01 << 2;
    public static final int DS_RECENT       = 0x01 << 3;
    public static final int DS_ALL          = 0xFFFF;

    public static final String ROOT_NOTE    = ".note";
    public static final String ROOT_TRASH   = ".trash";
    public static final String ROOT_EXTRACT = ".extract";

    RecordDataset recordDataset; // 记录集合

    TagDataset tagDataset;       // 标签集合
    TagEntity tagEntity;

    FavoriteDataset favoriteDataset;
    FavoriteEntity favoriteEntity;

    RecentDataset recentDataset;

    HashMap<Class<? extends BaseDataset>, File> fileMap;

    static RecordManager instance;

    static final RecordManager getInstance() {
        if (instance == null) {
            instance = new RecordManager();
        }

        return instance;
    }

    private RecordManager() {
        this.fileMap = new HashMap<>();

        File dir = LocalStorage.getInstance().getDirectory(LocalStorage.PATH_NOTE_DIR);

        fileMap.put(RecordDataset.class, new File(dir, "record_ds.json"));
        fileMap.put(TagDataset.class, new File(dir, "tag_ds.json"));
        fileMap.put(FavoriteDataset.class, new File(dir, "favorite_ds.json"));
        fileMap.put(RecentDataset.class, new File(dir, "recent_ds.json"));

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

                if ((e.getType() & type) != 0) {
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
        RecordEntry entry = new RecordEntry(id, parent, type);
        ds.add(entry);

        return entry;

    }

    public String getName(@NonNull RecordEntry entry, @NonNull  String name) {

        int type = entry.getType();
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
            entry.setDeleted(System.currentTimeMillis());
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
        if (recordDataset != null) {
            return recordDataset;
        }

        recordDataset = createDataset(RecordDataset.class);

        return recordDataset;
    }

    TagDataset getTagDataset() {
        if (tagDataset != null) {
            return tagDataset;
        }

        tagDataset = createDataset(TagDataset.class);

        return tagDataset;
    }


    FavoriteDataset getFavoriteDataset() {
        if (favoriteDataset != null) {
            return favoriteDataset;
        }

        favoriteDataset = createDataset(FavoriteDataset.class);

        return favoriteDataset;
    }

    RecentDataset getRecentDataset() {
        if (recentDataset != null) {
            return recentDataset;
        }

        recentDataset = createDataset(RecentDataset.class);

        return recentDataset;
    }
    void save(int flags) {

        if (((flags & DS_RECORD) != 0)) {
            save(recordDataset);
        }

        if (((flags & DS_FAVORITE) != 0)) {
            save(favoriteDataset);
        }

        if (((flags & DS_TAG) != 0)) {
            save(tagDataset);
        }

        if (((flags & DS_RECENT) != 0)) {
            save(recentDataset);
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
