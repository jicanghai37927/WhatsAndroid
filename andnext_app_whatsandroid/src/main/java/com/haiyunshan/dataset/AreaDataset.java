package com.haiyunshan.dataset;

import club.andnext.dataset.BaseDataset;
import club.andnext.dataset.BaseEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AreaDataset extends BaseDataset<AreaDataset.AreaEntity> {

    public List<AreaEntity> getChildren(String parent) {
        List<AreaEntity> list = new ArrayList<>();
        list = getChildren(parent, list);

        return list;
    }

    public List<AreaEntity> getChildren(String parent, List<AreaEntity> list) {
        list = (list == null)? new ArrayList<AreaEntity>(): list;
        list.clear();

        for (AreaEntity e: this.list) {
            if (e.getParent().equalsIgnoreCase(parent)) {
                list.add(e);
            }
        }

        return list;
    }

    public static class AreaEntity extends BaseEntity {

        @SerializedName("parent")
        String parent;

        @SerializedName("name")
        String name;

        public AreaEntity(String id, String parent, String name) {
            this.id = id;
            this.parent = parent;
            this.name = name;
        }

        public String getParent() {
            return parent == null? "": parent;
        }

        public String getName() {
            return name;
        }
    }
}
