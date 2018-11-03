package com.haiyunshan.article;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

class EntityFactory {

    HashMap<Class<? extends ArticleEntry>, Class<? extends DocumentEntity>> map;

    EntityFactory() {

        this.map = new HashMap<>();

        map.put(ParagraphEntry.class, ParagraphEntity.class);
    }

    DocumentEntity create(ArticleEntry entry) {
        Class<? extends DocumentEntity> entityClass = map.get(entry.getClass());
        if (entityClass == null) {
            throw new NotFoundException("Did't found entity for " + entry.getClass());
        }

        DocumentEntity entity = null;

        try {
            Constructor c = entityClass.getConstructor(entry.getClass());
            c.setAccessible(true);

            entity = (DocumentEntity)c.newInstance(entry);

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
