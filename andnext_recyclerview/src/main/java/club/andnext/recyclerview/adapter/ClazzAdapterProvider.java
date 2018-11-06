package club.andnext.recyclerview.adapter;

public interface ClazzAdapterProvider<T> {

    T get(int position);

    int size();
}
