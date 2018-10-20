package com.haiyunshan.preview;

import android.text.TextUtils;
import club.andnext.dataset.BaseDataset;
import club.andnext.dataset.BaseEntity;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class ExtensionDataset extends BaseDataset<ExtensionDataset.ExtensionEntity> {

    public ExtensionEntity accept(String ext) {
        for (ExtensionEntity e : list) {
            ExtensionEntity entity = e.accept(ext);
            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    /**
     *
     */
    public static class ExtensionEntity extends BaseEntity {

        @SerializedName("name")
        String name;

        @SerializedName("extensions")
        String[] extensions;

        @SerializedName("fragment")
        String fragment;

        public String getFragment() {
            return fragment;
        }

        ExtensionEntity accept(String ext) {
            if (extensions == null || extensions.length == 0) {
                return this;
            }

            if (TextUtils.isEmpty(ext)) {
                return null;
            }

            for (String s : extensions) {
                if (s.equalsIgnoreCase(ext)) {
                    return this;
                }
            }

            return null;
        }
    }
}
