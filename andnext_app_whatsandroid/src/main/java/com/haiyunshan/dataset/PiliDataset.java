package com.haiyunshan.dataset;

import club.andnext.dataset.BaseDataset;
import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class PiliDataset extends BaseDataset<PiliDataset.PiliEntry> {

    /**
     *
     */
    public static class PiliEntry extends BaseEntry {

        @SerializedName("name")
        String name;

        @SerializedName("poem")
        String poem;

        transient Object object;

        public String getName() {
            return name;
        }

        public String getPoem() {
            return poem;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }
}
