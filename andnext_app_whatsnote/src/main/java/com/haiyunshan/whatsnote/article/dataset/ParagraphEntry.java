package com.haiyunshan.whatsnote.article.dataset;

import com.google.gson.annotations.SerializedName;

public class ParagraphEntry extends ArticleEntry {

    public static final String TYPE = "paragraph";

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
