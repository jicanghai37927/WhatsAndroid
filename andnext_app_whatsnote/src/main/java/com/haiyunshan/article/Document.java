package com.haiyunshan.article;

import com.haiyunshan.record.RecordEntity;

import java.util.ArrayList;

public class Document {

    ArrayList<DocumentEntity> entityList;

    Article article;
    RecordEntity record;

    DocumentManager documentManager;

    private Document(Article article, RecordEntity record, DocumentManager mgr) {
        this.article = article;
        this.record = record;

        this.documentManager = mgr;

        this.entityList = new ArrayList<>(article.size());

        {
            EntityFactory factory = mgr.getFactory();

            for (int i = 0, size = article.size(); i < size; i++) {
                ArticleEntry e = article.get(i);
                DocumentEntity entity = factory.create(e);
                if (entity != null) {
                    entityList.add(entity);
                }
            }
        }
    }

    public String getId() {
        return record.getId();
    }

    public DocumentEntity get(int index) {
        return entityList.get(index);
    }

    public int size() {
        return entityList.size();
    }

    public void save() {
        for (DocumentEntity e : entityList) {
            e.save();
        }

        documentManager.save(this.getId(), article);
    }

    public static final Document obtain(String id) {
        DocumentManager mgr = DocumentManager.getInstance();

        Article article = mgr.create(id, "", System.currentTimeMillis());
        RecordEntity record = RecordEntity.obtain(id, RecordEntity.TYPE_EMPTY);
        Document doc = new Document(article, record, mgr);

        return doc;
    }

    public static final boolean create(RecordEntity record, String content) {

        DocumentManager mgr = DocumentManager.getInstance();

        Article article = mgr.create(record.getId(), content, record.getCreated());
        mgr.save(record.getId(), article);

        return true;
    }
}
