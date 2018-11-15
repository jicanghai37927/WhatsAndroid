package com.haiyunshan.whatsnote.preview.dataset;

import android.text.TextUtils;
import club.andnext.dataset.BaseDataset;
import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class ExtensionDataset extends BaseDataset<ExtensionDataset.ExtensionEntry> {

    public ExtensionEntry accept(String ext) {
        for (ExtensionEntry e : list) {
            ExtensionEntry entity = e.accept(ext);
            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    /**
     *
     */
    public static class ExtensionEntry extends BaseEntry {

        @SerializedName("name")
        String name;

        @SerializedName("fragment")
        String fragment;

        @SerializedName("extensions")
        String[] extensions;

        public String getFragment() {
            return fragment;
        }

        ExtensionEntry accept(String ext) {
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
