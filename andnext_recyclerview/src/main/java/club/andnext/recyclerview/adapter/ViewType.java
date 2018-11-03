package club.andnext.recyclerview.adapter;

import java.util.ArrayList;
import java.util.Arrays;

public class ViewType {

    static final int INTERVAL = 1000;

    Class<?> clazz;
    int value;

    ArrayList<ViewHolderBuilder> list;

    ViewType(Class<?> clz, int value, ViewHolderBuilder... builders) {
        this.clazz = clz;
        this.value = value;
        this.list = new ArrayList<>(Arrays.asList(builders));
    }

    void add(ViewHolderBuilder... builders) {
        for (ViewHolderBuilder b : builders) {
            if (list.indexOf(b) >= 0) {
                continue;
            }

            list.add(b);
        }
    }

    ViewType accept(int value) {
        int a = this.value / INTERVAL;
        int b = value / INTERVAL;

        if (a == b) {
            return this;
        }

        return null;
    }

    int index(Object obj, BuilderFilter filter) {
        ViewHolderBuilder d = filter.accept(obj, this.list);
        if (d != null) {
            for (int i = 0, size = list.size(); i < size; i++) {
                if (d == list.get(i)) {
                    return this.value + i;
                }
            }
        }

        return -1;
    }

    ViewHolderBuilder get(int value) {
        int index = value - this.value;
        if (index < 0 || index >= list.size()) {
            return null;
        }

        return list.get(index);
    }

}
