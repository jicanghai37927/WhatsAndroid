package com.haiyunshan.article;

import android.text.TextUtils;
import android.util.Pair;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.storage.LocalStorage;

import java.io.File;

class DocumentManager {

    static final String TYPE_ARTICLE = "";
    static final String TYPE_PICTURE = "picture";

    static final String URI_ARTICLE = "article.json";

    EntryDeserializer entryDeserializer;
    EntityFactory entityFactory;
    StuffWorker stuffWorker;

    static DocumentManager instance;

    public static final DocumentManager getInstance() {
        if (instance == null) {
            instance = new DocumentManager();
        }

        return instance;
    }

    private DocumentManager() {

    }

    Article create(String id, String content, long created) {
        Article ds = null;

        File file = getPath(id, URI_ARTICLE, TYPE_ARTICLE);
        if (file.exists()) {
            ds = GsonUtils.fromJson(file, Article.class, new Pair(ArticleEntry.class, this.getDeserializer()));
        }

        if (ds == null) {
            ds = new Article();
        }

        if (ds.size() == 0) {

            ParagraphEntry entry = new ParagraphEntry(UUIDUtils.next());
            entry.setCreated(created);
            entry.setText(content);

            ds.add(entry); // 默认一个段落
        }

        return ds;
    }

    void save(String id, Article article) {

        File file = getPath(id, URI_ARTICLE, TYPE_ARTICLE);
        file.getParentFile().mkdirs();

        GsonUtils.toJson(article, file);

    }

    File getFile(DocumentEntity entity) {
        File file = null;

        ArticleEntry e = entity.getEntry();

        String uri = "";
        String type = "";

        if (e instanceof PictureEntry) {
            PictureEntry entry = (PictureEntry)e;

            uri = e.getId();

            String ext = entry.getExtension();
            if (!TextUtils.isEmpty(ext)) {
                uri = uri + "." + entry.getExtension();
            }

            uri = uri  + ".pic";

            type = TYPE_PICTURE;
        }

        if (!TextUtils.isEmpty(uri)) {
            file = getPath(entity.getDocument().getId(), uri, type);
        }

        return file;
    }

    StuffWorker getStuffWorker() {
        if (stuffWorker != null) {
            return stuffWorker;
        }

        stuffWorker = new StuffWorker();
        return stuffWorker;
    }

    EntityFactory getFactory() {
        if (entityFactory != null) {
            return entityFactory;
        }

        entityFactory = new EntityFactory();
        {
            entityFactory.put(ParagraphEntry.class, ParagraphEntity.class);
            entityFactory.put(PictureEntry.class, PictureEntity.class);
        }

        return entityFactory;
    }

    EntryDeserializer getDeserializer() {
        if (entryDeserializer != null) {
            return entryDeserializer;
        }

        EntryDeserializer typeAdapter = new EntryDeserializer();

        {
            typeAdapter.put(ParagraphEntry.TYPE, ParagraphEntry.class);
            typeAdapter.put(PictureEntry.TYPE, PictureEntry.class);
        }

        this.entryDeserializer = typeAdapter;
        return entryDeserializer;
    }

    File getDir(String id) {
        File dir = LocalStorage.getInstance().getDirectory(LocalStorage.PATH_NOTE_DIR);
        dir = new File(dir, "files");
        dir = new File(dir, id + ".note");

        return dir;
    }

    File getPath(String id, String uri, String type) {
        File file;

        File dir = getDir(id);
        if (TextUtils.isEmpty(type)) {
            file = new File(dir, uri);
        } else {
            dir = new File(dir, type);
            file = new File(dir, uri);
        }

        return file;
    }

}
