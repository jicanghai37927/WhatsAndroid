package club.andnext.recyclerview.adapter;

/**
 *
 * @param <T>
 */
public abstract class BuilderFilter<T> {

    public ViewHolderBuilder accept(T obj, ViewHolderBuilder[] array) {
        Class<? extends ViewHolderBuilder> clz = this.getBuilder(obj);
        if (clz == null) {
            return null;
        }

        for (ViewHolderBuilder d : array) {
            if (d.getClass() == clz) {
                return d;
            }
        }

        return null;
    }

    public abstract Class<? extends ViewHolderBuilder> getBuilder(T obj);

}
