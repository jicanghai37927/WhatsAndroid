package club.andnext.recyclerview.adapter;

public class ViewType {

    static final int INTERVAL = 1000;

    Class<?> clazz;
    int value;

    ViewHolderBuilder[] array;

    ViewType(Class<?> clz, int value, ViewHolderBuilder... builders) {
        this.clazz = clz;
        this.value = value;
        this.array = builders;
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
        ViewHolderBuilder d = filter.accept(obj, this.array);
        if (d != null) {
            for (int i = 0; i < array.length; i++) {
                if (d == array[i]) {
                    return this.value + i;
                }
            }
        }

        return -1;
    }

    ViewHolderBuilder get(int value) {
        int index = value - this.value;
        if (index < 0 || index >= array.length) {
            return null;
        }

        return array[index];
    }

}
