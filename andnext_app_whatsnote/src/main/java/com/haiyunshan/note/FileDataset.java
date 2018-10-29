package com.haiyunshan.note;

import android.text.TextUtils;
import club.andnext.dataset.BaseDataset;
import club.andnext.dataset.BaseEntity;
import com.google.gson.annotations.SerializedName;

public class FileDataset<T extends FileDataset.FileEntity> extends BaseDataset<T> {

    /**
     *
     */
    public static abstract class FileEntity extends BaseEntity {

        @SerializedName("name")
        String name; // real name from user

        @SerializedName("desc")
        String desc; // entity description

        @SerializedName("alias")
        private String alias; // inner use, alias from app, name first

        public FileEntity(String id) {
            this.id = id;
        }

        public abstract boolean isDirectory();

        public String getDisplayName() {
            if (!TextUtils.isEmpty(name)) {
                return name;
            }

            if (!TextUtils.isEmpty(alias)) {
                return alias;
            }

            return "";
        }

        public String getName() {
            return name == null? "": name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc == null? "": name;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getAlias() {
            return alias == null? "": name;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }

}
