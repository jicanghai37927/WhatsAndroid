package com.haiyunshan.whatsnote.record.entity;

import org.joda.time.DateTime;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
    public static class Modified implements Comparator<RecordEntity> {

        public Modified() {

        }

        @Override
        public int compare(RecordEntity o1, RecordEntity o2) {
            DateTime c1 = o1.getModified();
            DateTime c2 = o2.getModified();

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
            long s1 = o1.getSize();
            long s2 = o2.getSize();

            int result = 0;
            result = (s1 > s2)? 1: result;
            result = (s1 < s2)? -1: result;

            return result;
        }
    }

    /**
     *
     */
    public static class Tag implements Comparator<RecordEntity> {

        TagEntity tagEntity;

        public Tag() {
            tagEntity = TagEntity.obtain();
        }

        @Override
        public int compare(RecordEntity o1, RecordEntity o2) {
            List<String> list1 = o1.getTagList();
            List<String> list2 = o2.getTagList();

            int result = 0;

            if (list1.isEmpty() && list2.isEmpty()) {

            } else if (list1.isEmpty() && !list2.isEmpty()) {
                result = -1;
            } else if (!list1.isEmpty() && list2.isEmpty()) {
                result = 1;
            } else {
                int size1 = list1.size();
                int size2 = list2.size();

                int size = Math.min(size1, size2);
                for (int i = 0; i < size; i++) {
                    String tag1 = list1.get(i);
                    String tag2 = list2.get(i);

                    int a1 = tagEntity.indexOf(tag1);
                    a1 = (a1 < 0)? Integer.MAX_VALUE: a1;
                    int a2 = tagEntity.indexOf(tag2);
                    a2 = (a2 < 0)? Integer.MAX_VALUE: a2;

                    if (a1 != a2) {
                        result = (a1 > a2)? 1: -1;
                        break;
                    }
                }

                if (result == 0) {
                    if (size1 != size2) {
                        result = (size1 > size2)? 1: -1;
                    }
                }
            }

            return result;
        }
    }
}
