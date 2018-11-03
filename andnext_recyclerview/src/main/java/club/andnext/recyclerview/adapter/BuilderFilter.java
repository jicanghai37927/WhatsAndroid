package club.andnext.recyclerview.adapter;

import java.util.List;

/**
 *
 * @param <T>
 */
public abstract class BuilderFilter<T> {

    public ViewHolderBuilder accept(T obj, List<ViewHolderBuilder> list) {
        Class<? extends ViewHolderBuilder> clz = this.getBuilder(obj);
        if (clz == null) {
            return null;
        }

        for (ViewHolderBuilder d : list) {
            if (d.getClass() == clz) {
                return d;
            }
        }

        return null;
    }

    public abstract Class<? extends ViewHolderBuilder> getBuilder(T obj);

}
