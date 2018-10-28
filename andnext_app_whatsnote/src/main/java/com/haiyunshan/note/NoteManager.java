package com.haiyunshan.note;

import androidx.annotation.NonNull;
import club.andnext.utils.UUIDUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoteManager {

    public static final int TYPE_FOLDER = RecordDataset.TYPE_FOLDER;
    public static final int TYPE_NOTE   = RecordDataset.TYPE_NOTE;
    public static final int TYPE_ALL    = TYPE_FOLDER | TYPE_NOTE;

    RecordDataset recordDs; // 记录集合
    File recordFile;

    FolderDataset folderDs; // 目录集合
    File folderFile;

    NoteDataset	noteDs; 	// 笔记集合
    File noteFile;

    List<RecordDataset.RecordEntity> recordList;
    List<FileEntity> fileList;

    static NoteManager instance;

    public static final NoteManager getInstance() {
        if (instance == null) {
            instance = new NoteManager();
        }

        return instance;
    }

    private NoteManager() {

    }

    /**
     *
     * @param parent
     * @param list
     * @return
     */
    public List<FileEntity> getList(String parent, int type, List<FileEntity> list) {

        {
            parent = (parent == null) ? "" : parent;

            list = (list == null) ? new ArrayList<FileEntity>() : list;
            list.clear();
        }

        {
            this.recordList = getRecordDataset().getChildren(parent, recordList);

            for (RecordDataset.RecordEntity e : recordList) {

                if ((type & TYPE_FOLDER) != 0) {
                    FileEntity en = obtain(e.getId(), type & TYPE_FOLDER);
                    if (en != null) {
                        list.add(en);
                    }
                }

                if ((type & TYPE_NOTE) != 0) {
                    FileEntity en = obtain(e.getId(), type & TYPE_NOTE);
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

    public FileEntity create(String parent, int type) {

        FileEntity en = null;

        boolean accept = (type == TYPE_FOLDER) || (type == TYPE_NOTE);
        if (!accept) {
            throw new IllegalArgumentException("type should be FOLDER or NOTE.");
        }

        RecordDataset.RecordEntity r = createRecord(parent, type);

        if (type == TYPE_FOLDER) {

            FolderDataset.FolderEntity entity = new FolderDataset.FolderEntity(r.getId());

            FolderDataset ds = getFolderDataset();
            ds.add(entity);

            en = entity;

        } else if (type == TYPE_NOTE) {

            NoteDataset.NoteEntity entity = new NoteDataset.NoteEntity(r.getId());

            NoteDataset ds = getNoteDataset();
            ds.add(entity);

            en = entity;
        }

        return en;
    }

    public String getName(@NonNull FileEntity entity, @NonNull  String name) {

        int type = (entity.isDirectory())? TYPE_FOLDER: TYPE_NOTE;
        String parent = getRecordDataset().obtain(entity.getId()).getParent();

        this.fileList = getList(parent, type, fileList);

        String r = getName(name, fileList);

        fileList.clear();

        return r;
    }

    FileEntity obtain(String id, int type) {

        if ((type & TYPE_FOLDER) != 0) {
            FolderDataset ds = getFolderDataset();
            FolderDataset.FolderEntity en = ds.obtain(id);

            if (en != null) {
                return en;
            }
        }

        if ((type & TYPE_NOTE) != 0) {
            NoteDataset ds = getNoteDataset();
            NoteDataset.NoteEntity en = ds.obtain(id);

            if (en != null) {
                return en;
            }
        }

        return null;
    }

    String getName(String name, List<FileEntity> list) {
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

    int indexOfName(String name, List<FileEntity> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            FileEntity e = list.get(i);
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

    FolderDataset getFolderDataset() {
        if (folderDs != null) {
            return folderDs;
        }

        folderDs = new FolderDataset();

        return folderDs;
    }

    NoteDataset getNoteDataset() {
        if (noteDs != null) {
            return noteDs;
        }

        noteDs = new NoteDataset();

        return noteDs;
    }
}
