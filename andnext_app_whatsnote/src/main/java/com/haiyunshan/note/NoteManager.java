package com.haiyunshan.note;

import android.util.SparseArray;
import androidx.annotation.NonNull;
import club.andnext.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.List;

public class NoteManager {

    public static final int TYPE_FOLDER = RecordDataset.TYPE_FOLDER;
    public static final int TYPE_NOTE   = RecordDataset.TYPE_NOTE;
    public static final int TYPE_ALL    = TYPE_FOLDER | TYPE_NOTE;

    RecordDataset recordDs; // 记录集合
    SparseArray<FileDataset<? extends FileDataset.FileEntity>> noteDsArray; // folder and note dataset

    List<RecordDataset.RecordEntity> recordList;
    List<FileDataset.FileEntity> fileList;

    static NoteManager instance;

    public static final NoteManager getInstance() {
        if (instance == null) {
            instance = new NoteManager();
        }

        return instance;
    }

    private NoteManager() {
        this.noteDsArray = new SparseArray<>();
    }

    /**
     *
     * @param parent
     * @param list
     * @return
     */
    public List<FileDataset.FileEntity> getList(String parent, int type, List<FileDataset.FileEntity> list) {

        {
            parent = (parent == null) ? "" : parent;

            list = (list == null) ? new ArrayList<FileDataset.FileEntity>() : list;
            list.clear();
        }

        {
            this.recordList = getRecordDataset().getChildren(parent, recordList);

            for (RecordDataset.RecordEntity e : recordList) {

                if ((type & TYPE_FOLDER) != 0) {
                    FileDataset.FileEntity en = obtain(e.getId(), type & TYPE_FOLDER);
                    if (en != null) {
                        list.add(en);
                    }
                }

                if ((type & TYPE_NOTE) != 0) {
                    FileDataset.FileEntity en = obtain(e.getId(), type & TYPE_NOTE);
                    if (en != null) {
                        list.add(en);
                    }
                }
            }
        }

        {
            recordList.clear();
        }

        return list;
    }

    public FileDataset.FileEntity create(String parent, int type) {

        FileDataset.FileEntity entity = null;

        boolean accept = (type == TYPE_FOLDER) || (type == TYPE_NOTE);
        if (!accept) {
            throw new IllegalArgumentException("type should be FOLDER or NOTE.");
        }

        RecordDataset.RecordEntity r = createRecord(parent, type);

        if (type == TYPE_FOLDER) {
            entity = new FolderDataset.FolderEntity(r.getId());
        } else if (type == TYPE_NOTE) {
            entity = new NoteDataset.NoteEntity(r.getId());
        }

        if (entity != null) {
            FileDataset ds = getDataset(type);
            ds.add(entity);
        }

        return entity;
    }

    public String getName(@NonNull FileDataset.FileEntity entity, @NonNull  String name) {

        int type = (entity.isDirectory())? TYPE_FOLDER: TYPE_NOTE;
        String parent = getRecordDataset().obtain(entity.getId()).getParent();

        this.fileList = getList(parent, type, fileList);

        String r = getName(name, fileList);

        fileList.clear();

        return r;
    }

    FileDataset.FileEntity obtain(String id, int type) {

        FileDataset.FileEntity entity = null;

        while (true) {

            if ((type & TYPE_FOLDER) != 0) {
                FileDataset<? extends FileDataset.FileEntity> ds = getDataset(type & TYPE_FOLDER);
                entity = ds.obtain(id);

                if (entity != null) {
                    break;
                }
            }

            if ((type & TYPE_NOTE) != 0) {
                FileDataset<? extends FileDataset.FileEntity> ds = getDataset(type & TYPE_NOTE);
                entity = ds.obtain(id);

                if (entity != null) {
                    break;
                }
            }

            break;
        }

        return entity;
    }

    String getName(String name, List<FileDataset.FileEntity> list) {
        if (indexOfName(name, list) < 0) {
            return name;
        }

        int index = 1;
        while (true) {
            String text = name + " " + index;
            if (indexOfName(text, list) < 0) {
                return text;
            }

            ++index;
        }
    }

    int indexOfName(String name, List<FileDataset.FileEntity> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            FileDataset.FileEntity e = list.get(i);
            if (e.getDisplayName().equals(name)) {
                return i;
            }
        }

        return -1;
    }

    RecordDataset.RecordEntity createRecord(String parent, int type) {
        String id = UUIDUtils.next();

        RecordDataset ds = getRecordDataset();
        RecordDataset.RecordEntity p = ds.obtain(parent);
        parent = (p == null)? "": parent;

        RecordDataset.RecordEntity en = new RecordDataset.RecordEntity(id, parent, type);
        ds.add(en);

        return en;
    }

    RecordDataset getRecordDataset() {
        if (recordDs != null) {
            return recordDs;
        }

        recordDs = new RecordDataset();

        return recordDs;
    }

    FileDataset<? extends FileDataset.FileEntity> getDataset(int type) {
        FileDataset ds = noteDsArray.get(type);
        if (ds != null) {
            return ds;
        }

        if (type == TYPE_FOLDER) {
            ds = new FolderDataset();
        } else {
            ds = new NoteDataset();
        }

        noteDsArray.put(type, ds);

        return ds;
    }
}
