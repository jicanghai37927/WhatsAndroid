package com.haiyunshan.article;

import com.haiyunshan.record.RecordEntity;

import java.util.ArrayList;

public class Document {

    ArrayList<DocumentEntity> list;

    Article article;
    RecordEntity record;

    private Document(Article article, RecordEntity record) {
        this.article = article;
        this.record = record;

        this.list = new ArrayList<>(article.size());

        {
            EntityFactory factory = getManager().getFactory();

            for (int i = 0, size = article.size(); i < size; i++) {
                ArticleEntry e = article.get(i);
                DocumentEntity entity = factory.create(this, e);
                if (entity != null) {
                    list.add(entity);
                }
            }
        }
    }

    public String getId() {
        return record.getId();
    }

    public DocumentEntity get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public void add(DocumentEntity entity) {
        this.add(size(), entity);
    }

    public void add(int index, DocumentEntity entity) {
        list.add(index, entity);

        // add entity stuff
        this.getStuff().add(entity);
    }

    public int remove(DocumentEntity entity) {
        int index = list.indexOf(entity);
        if (index < 0) {
            return index;
        }

        list.remove(index);

        // remove entity stuff
        this.getStuff().remove(entity);

        return index;
    }

    public void save() {
        for (DocumentEntity e : list) {
            e.save();
        }

        {
            article.clear();
            for (DocumentEntity entity : list) {
                article.add(entity.getEntry());
            }
        }

        {
            getManager().save(this.getId(), article);
        }
    }


    StuffWorker getStuff() {
        return getManager().getStuffWorker();
    }

    Article getArticle() {
        return article;
    }

    DocumentManager getManager() {
        return DocumentManager.getInstance();
    }

    public static final Document create(String id) {
        DocumentManager mgr = DocumentManager.getInstance();

        Article article = mgr.create(id, "", System.currentTimeMillis());
        RecordEntity record = RecordEntity.create(id, RecordEntity.TYPE_EMPTY);
        Document doc = new Document(article, record);

        return doc;
    }

    public static final boolean create(RecordEntity record, String content) {

        DocumentManager mgr = DocumentManager.getInstance();

        Article article = mgr.create(record.getId(), content, record.getCreated());
        mgr.save(record.getId(), article);

        return true;
    }
}
