package com.haiyunshan.note;

import club.andnext.dataset.BaseDataset;
import club.andnext.dataset.BaseEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RecordDataset extends BaseDataset<RecordDataset.RecordEntity> {

    static final int TYPE_FOLDER = 0x01;
    static final int TYPE_NOTE   = 0x02;

    private static final String KEY_FOLDER  = "folder";
    private static final String KEY_NOTE    = "note";

    public List<RecordEntity> getChildren(String parent, List<RecordEntity> list) {
        list = (list == null)? new ArrayList<RecordEntity>(): list;
        list.clear();

        for (RecordEntity e: this.list) {
            if (e.getParent().equalsIgnoreCase(parent)) {
                list.add(e);
            }
        }

        return list;
    }

    /**
     *
     */
    public static class RecordEntity extends BaseEntity {

        @SerializedName("parent")
        String parent;

        @SerializedName("type")
        String type;

        private int typeValue; // i prefer to save string value and use int value. :)

        public RecordEntity(String id, String parent, int type) {
            this.id = id;
            this.parent = parent;
            this.type = (type == TYPE_FOLDER)? KEY_FOLDER: KEY_NOTE;

            this.typeValue = type;
        }

        public String getParent() {
            return parent == null? "": parent;
        }

        public int getType() {
            if (typeValue != 0) {
                return typeValue;
            }

            typeValue = (type.equals(KEY_FOLDER))? TYPE_FOLDER: TYPE_NOTE;
            return typeValue;
        }
    }
}
