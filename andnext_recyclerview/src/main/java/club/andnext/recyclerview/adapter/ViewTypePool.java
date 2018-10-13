package club.andnext.recyclerview.adapter;

import java.util.HashMap;
import java.util.Iterator;

public class ViewTypePool {

    HashMap<Class<?>, ViewType> clazzMap;
    HashMap<Class<?>, BuilderFilter> filterMap;

    int type;

    ViewTypePool() {
        this.clazzMap = new HashMap<>();
        this.filterMap = new HashMap<>();

        this.type = 0;
    }

    void bind(Class<?> clazz, ViewHolderBuilder... builders) {
        if (clazzMap.get(clazz) != null) {
            ViewType type = clazzMap.get(clazz);

            clazzMap.put(clazz, new ViewType(clazz, type.value, builders));

        } else {

            clazzMap.put(clazz, new ViewType(clazz, type, builders));

            type += ViewType.INTERVAL;

        }
    }

    void bind(Class<?> clazz, BuilderFilter filter) {
        filterMap.put(clazz, filter);
    }

    int getViewType(Object obj) {
        int viewType = -1;

        Class<?> clazz = obj.getClass();
        ViewType type = clazzMap.get(clazz);
        if (type == null) {
            return viewType;
        }

        viewType = type.value;
        if (type.array.length > 1) {
            BuilderFilter filter = filterMap.get(clazz);
            if (filter != null) {
                viewType = type.index(obj, filter);
            }
        }

        return viewType;
    }

    ViewHolderBuilder getDelegate(int viewType) {
        if (viewType < 0) {
            return null;
        }

        Iterator<ViewType> iterator = clazzMap.values().iterator();
        while (iterator.hasNext()) {
            ViewType type = iterator.next();
            type = type.accept(viewType);
            if (type != null) {
                return type.get(viewType);
            }
        }

        return null;
    }

    boolean isBind(Class clz) {
        return clazzMap.get(clz) != null;
    }

}
