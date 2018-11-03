package com.haiyunshan.dataset;

import club.andnext.dataset.BaseDataset;
import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class DemoDataset extends BaseDataset<DemoDataset.DemoEntry> {

    @SerializedName("name")
    String name;

    public String getName() {
        return name;
    }

    public static class DemoEntry extends BaseEntry {

        @SerializedName("name")
        String name;

        @SerializedName("desc")
        String desc;

        @SerializedName("help")
        String help;

        @SerializedName("target")
        String target;

        @SerializedName("activity")
        String activity;

        @SerializedName("fragment")
        String fragment;

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public String getHelp() {
            return help;
        }

        public String getTarget() {
            return target;
        }

        public String getActivity() {
            return activity;
        }

        public String getFragment() {
            return fragment;
        }
    }
}
