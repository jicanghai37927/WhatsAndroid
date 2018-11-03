package com.haiyunshan.article;

import android.text.TextUtils;
import android.util.Pair;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.storage.LocalStorage;

import java.io.File;

class DocumentManager {

    static final String TYPE_ARTICLE = "";

    static final String URI_ARTICLE = "article.json";

    EntityFactory entityFactory;

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
            EntityDeserializer typeAdapter = new EntityDeserializer();
            ds = GsonUtils.fromJson(file, Article.class, new Pair(ArticleEntry.class, typeAdapter));
        }

        if (ds == null) {
            ds = new Article();

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

    EntityFactory getFactory() {
        if (entityFactory == null) {
            entityFactory = new EntityFactory();
        }

        return entityFactory;
    }

    File getDir(String id) {
        File dir = LocalStorage.getInstance().getDirectory(LocalStorage.PATH_NOTE_DIR);
        dir = new File(dir, "files");
        dir = new File(dir, id + ".note");

        return dir;
    }

    File getPath(String id, String uri, String type) {
        File file = null;

        File dir = getDir(id);
        if (TextUtils.isEmpty(type)) {
            file = new File(dir, uri);
        }

        return file;
    }

}
