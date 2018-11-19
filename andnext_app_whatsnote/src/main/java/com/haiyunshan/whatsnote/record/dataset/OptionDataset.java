package com.haiyunshan.whatsnote.record.dataset;

import club.andnext.dataset.BaseDataset;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OptionDataset extends BaseDataset {

    @SerializedName("sort_id")
    String sortId;

    @SerializedName("sort_order")
    String sortOrder;

    @SerializedName("recent_sort_id")
    String recentSortId;

    @SerializedName("recent_sort_order")
    String recentSortOrder;

    @SerializedName("section_list")
    List<String> sectionList;

    public OptionDataset() {
        {
            this.sortId = "name";
            this.sortOrder = "asc";
        }

        {
            this.recentSortId = "modified";
            this.recentSortOrder = "desc";
        }

        {
            this.sectionList = new ArrayList<>();
            sectionList.add("entrance");
            sectionList.add("favorite");
            sectionList.add("tag");
        }

        {
            this.list = null;
        }
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public String getSortOrder() {
        return sortOrder == null? "": sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getRecentSortId() {
        return recentSortId;
    }

    public void setRecentSortId(String recentSortId) {
        this.recentSortId = recentSortId;
    }

    public String getRecentSortOrder() {
        return recentSortOrder == null? "": recentSortOrder;
    }

    public void setRecentSortOrder(String recentSortOrder) {
        this.recentSortOrder = recentSortOrder;
    }

    public List<String> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<String> sectionList) {
        this.sectionList = sectionList;
    }
}
