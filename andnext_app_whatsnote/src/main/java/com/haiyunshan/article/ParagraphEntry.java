package com.haiyunshan.article;

import com.google.gson.annotations.SerializedName;

class ParagraphEntry extends ArticleEntry {

    static final String TYPE = "paragraph";

    @SerializedName("text")
    String text;

    public ParagraphEntry(String id) {
        super(id, TYPE);
    }

    public String getText() {
        return (text == null)? "": text;
    }

    public ParagraphEntry setText(String text) {
        this.text = text;

        return this;
    }
}
