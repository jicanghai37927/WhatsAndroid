package com.haiyunshan.record;

import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class RecordEntry extends BaseEntry {

    static final int TYPE_FOLDER = 0x01;
    static final int TYPE_NOTE   = 0x02;

    private static final String KEY_FOLDER  = "folder";
    private static final String KEY_NOTE    = "note";

    @SerializedName("type")
    String type;

    @SerializedName("parent")
    String parent;

    @SerializedName("ancestor")
    String ancestor; // when trash, set ancestor for recover

    @SerializedName("name")
    String name; // real name from user

    @SerializedName("desc")
    String desc; // entity description

    @SerializedName("alias")
    String alias; // inner use, alias from app, name first

    @SerializedName("tag_list")
    List<String> tagList;

    private transient Integer typeValue; // I prefer to save string value and use int value. :)

    public RecordEntry(String id, String parent, int type) {
        this.id = id;

        this.type = (type == TYPE_FOLDER)? KEY_FOLDER: KEY_NOTE;
        this.parent = parent;

        this.typeValue = type;
    }

    public int getType() {
        if (typeValue != null) {
            return typeValue;
        }

        typeValue = (type.equals(KEY_FOLDER))? TYPE_FOLDER: TYPE_NOTE;
        return typeValue;
    }

    public String getParent() {
        return parent == null? "": parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getAncestor() {
        return ancestor;
    }

    public void setAncestor(String ancestor) {
        this.ancestor = ancestor;
    }

    public String getName() {
        return name == null? "": name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc == null? "": desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAlias() {
        return alias == null? "": alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}