package com.haiyunshan.whatsnote.record.dataset;

import club.andnext.dataset.BaseEntry;
import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

import java.util.List;

public class RecordEntry extends BaseEntry {

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

    @SerializedName("created")
    String created;

    @SerializedName("modified")
    String modified;

    @SerializedName("deleted")
    String deleted;

    public RecordEntry(String id, String parent, String type) {
        this.id = id;
        this.parent = parent;
        this.type = type;

        this.created = DateTime.now().toString();
        this.modified = this.created;
    }

    public String getType() {
        return type;
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

    public int indexOfTag(String tag) {
        if (tagList == null || tagList.isEmpty()) {
            return -1;
        }

        return tagList.indexOf(tag);
    }

    public String getCreated() {
        return created == null? "": created;
    }

    public void setCreated(String utc) {
        this.created = utc;
    }

    public String getModified() {
        return modified == null? "": modified;
    }

    public void setModified(String utc) {
        this.modified = utc;
    }

    public String getDeleted() {
        return deleted == null? "": deleted;
    }

    public void setDeleted(String utc) {
        this.deleted = utc;
    }
}