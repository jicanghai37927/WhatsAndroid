package com.haiyunshan.article;

public abstract class DocumentEntity<T extends ArticleEntry> {

    protected T entry;
    protected Document parent;

    DocumentEntity(Document d, T entry) {
        this.parent = d;
        this.entry = entry;
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
        return DocumentManager.getInstance();
    }
}
