package club.andnext.recyclerview.adapter;

public abstract class ClazzAdapterProvider<T> {

    public abstract T get(int position);

    public abstract int size();
}
