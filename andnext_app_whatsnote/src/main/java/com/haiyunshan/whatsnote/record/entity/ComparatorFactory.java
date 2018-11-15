package com.haiyunshan.whatsnote.record.entity;

import org.joda.time.DateTime;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;

class ComparatorFactory {

    HashMap<String, Class<? extends Comparator<RecordEntity>>> map;

    ComparatorFactory() {
        this.map = new HashMap<>();
    }

    public void register(String id, Class<? extends Comparator<RecordEntity>> clz) {
        map.put(id, clz);
    }

    Comparator<RecordEntity> create(String id) {
        Class<? extends Comparator<RecordEntity>> clz = map.get(id);
        if (clz == null) {
            clz = Name.class;
        }

        Comparator<RecordEntity> target = null;

        try {
            target = clz.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        if (target == null) {
            target = new Name();
        }

        return target;
    }

    /**
     * sort by name
     *
     */
    public static class Name implements Comparator<RecordEntity> {

        Collator collator;

        public Name() {
            collator = Collator.getInstance();
        }

        @Override
        public int compare(RecordEntity o1, RecordEntity o2) {
            String name1 = o1.getName();
            String name2 = o2.getName();

            return collator.compare(name1, name2);
        }
    }

    /**
     * sort by created time
     *
     */
    public static class Created implements Comparator<RecordEntity> {

        public Created() {

        }

        @Override
        public int compare(RecordEntity o1, RecordEntity o2) {
            DateTime c1 = o1.getCreated();
            DateTime c2 = o2.getCreated();

            int result = 0;
            if (!c1.isEqual(c2)) {
                result = (c1.isBefore(c2))? -1: 1;
            }

            return result;
        }
    }

    /**
     *
     */
    public static class Size implements Comparator<RecordEntity> {

        public Size() {

        }

        @Override
        public int compare(RecordEntity o1, RecordEntity o2) {
            return 0;
        }
    }

    /**
     *
     */
    public static class Tag implements Comparator<RecordEntity> {

        public Tag() {

        }

        @Override
        public int compare(RecordEntity o1, RecordEntity o2) {
            return 0;
        }
    }
}
