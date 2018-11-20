package com.haiyunshan.whatsnote.article.entity;

import com.haiyunshan.whatsnote.article.dataset.Article;
import com.haiyunshan.whatsnote.article.dataset.ArticleEntry;

public abstract class DocumentEntity<T extends ArticleEntry> {

    protected T entry;
    protected Document parent;

    DocumentEntity(Document d, T entry) {
        this.parent = d;
        this.entry = entry;
    }

    public String getId() {
        return entry.getId();
    }

    abstract void save();

    T getEntry() {
        return entry;
    }

    Article getArticle() {
        return getDocument().getArticle();
    }

    Document getDocument() {
        return parent;
    }

    DocumentManager getManager() {
        return DocumentManager.getInstance(getDocument().context);
    }
}
