package com.haiyunshan.whatsnote.article.entity;

import android.content.Context;
import android.text.TextUtils;
import com.haiyunshan.whatsnote.article.dataset.Article;
import com.haiyunshan.whatsnote.article.dataset.ArticleEntry;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;
import com.haiyunshan.whatsnote.record.entity.RecordFactory;
import org.joda.time.DateTime;

import java.util.ArrayList;

public class Document {

    ArrayList<DocumentEntity> list;

    Article article;
    RecordEntity record;

    Context context;

    private Document(Context context, Article article, RecordEntity record) {
        this.context = context.getApplicationContext();

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

    public Context getContext() {
        return context;
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

    public int remove(int index) {
        if (index < 0 || index >= list.size()) {
            return -1;
        }

        DocumentEntity entity = list.get(index);

        list.remove(index);

        // remove entity stuff
        this.getStuff().remove(entity);

        return index;
    }
    public int indexOf(DocumentEntity entity) {
        return list.indexOf(entity);
    }

    public void save() {
        String title = this.getTitle(56);
        if (!TextUtils.isEmpty(title)) {
            record.setAlias(title);
        }

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

    String getTitle(int max) {
        DocumentEntity en = this.get(0);
        if (en.getClass() != ParagraphEntity.class) {
            return "";
        }

        ParagraphEntity entity = (ParagraphEntity)en;
        CharSequence s = entity.getText();
        if (s.length() > max) {
            s = s.subSequence(0, max);
        }
        String text = s.toString().trim();
        int pos = text.indexOf('\n');
        if (pos > 0) {
            return text.substring(0, pos);
        }

        return text;
    }

    StuffWorker getStuff() {
        return getManager().getStuffWorker();
    }

    Article getArticle() {
        return article;
    }

    DocumentManager getManager() {
        return DocumentManager.getInstance(context);
    }

    public static final Document create(Context context, String id) {
        DocumentManager mgr = DocumentManager.getInstance(context);

        Article article = mgr.create(id, "", DateTime.now());
        RecordEntity record = RecordFactory.create(context, id, RecordEntity.TYPE_EMPTY);
        Document doc = new Document(context, article, record);

        return doc;
    }

    public static final boolean create(Context context, RecordEntity record, String content) {

        DocumentManager mgr = DocumentManager.getInstance(context);

        Article article = mgr.create(record.getId(), content, record.getCreated());
        mgr.save(record.getId(), article);

        return true;
    }
}
