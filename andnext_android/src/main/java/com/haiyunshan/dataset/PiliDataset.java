package com.haiyunshan.dataset;

import club.andnext.dataset.BaseDataset;
import club.andnext.dataset.BaseEntity;
import com.google.gson.annotations.SerializedName;

public class PiliDataset extends BaseDataset<PiliDataset.PiliEntity> {

    /**
     *
     */
    public static class PiliEntity extends BaseEntity {

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
