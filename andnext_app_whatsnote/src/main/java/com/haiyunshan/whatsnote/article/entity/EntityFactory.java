package com.haiyunshan.whatsnote.article.entity;

import com.haiyunshan.whatsnote.article.dataset.ArticleEntry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

class EntityFactory {

    HashMap<Class<? extends ArticleEntry>, Class<? extends DocumentEntity>> map;

    EntityFactory() {

        this.map = new HashMap<>();

    }

    void put(Class<? extends ArticleEntry> entry, Class<? extends DocumentEntity> entity) {
        map.put(entry, entity);
    }

    DocumentEntity create(Document d, ArticleEntry entry) {
        Class<? extends DocumentEntity> entityClass = map.get(entry.getClass());
        if (entityClass == null) {
            throw new NotFoundException("Did't found entity for " + entry.getClass());
        }

        DocumentEntity entity = null;

        try {
            Constructor c = entityClass.getConstructor(d.getClass(), entry.getClass());
            c.setAccessible(true);

            entity = (DocumentEntity)c.newInstance(d, entry);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (entity == null) {
            throw new IllegalArgumentException("Cannot create instance for " + entityClass);
        }

        return entity;
    }

    private static class NotFoundException extends RuntimeException {

        public NotFoundException(String message) {
            super(message);
        }
    }

}
