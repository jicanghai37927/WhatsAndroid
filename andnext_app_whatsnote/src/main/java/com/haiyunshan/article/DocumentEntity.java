package com.haiyunshan.article;

public abstract class DocumentEntity<T extends ArticleEntry> {

    protected T entry;

    DocumentEntity(T entry) {
        this.entry = entry;
    }

    abstract void save();
}
