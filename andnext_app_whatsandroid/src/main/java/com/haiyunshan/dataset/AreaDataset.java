package com.haiyunshan.dataset;

import club.andnext.dataset.BaseDataset;
import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AreaDataset extends BaseDataset<AreaDataset.AreaEntry> {

    public List<AreaEntry> getChildren(String parent) {
        List<AreaEntry> list = new ArrayList<>();
        list = getChildren(parent, list);

        return list;
    }

    public List<AreaEntry> getChildren(String parent, List<AreaEntry> list) {
        list = (list == null)? new ArrayList<AreaEntry>(): list;
        list.clear();

        for (AreaEntry e: this.list) {
            if (e.getParent().equalsIgnoreCase(parent)) {
                list.add(e);
            }
        }

        return list;
    }

    public static class AreaEntry extends BaseEntry {

        @SerializedName("parent")
        String parent;

        @SerializedName("name")
        String name;

        public AreaEntry(String id, String parent, String name) {
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
