package club.andnext.recyclerview.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewHolderBuilder<T, VH extends RecyclerView.ViewHolder> {

    ClazzAdapter adapter;

    protected ViewHolderBuilder() {

    }

    public Context getContext() {
        return adapter.getContext();
    }

    public ClazzAdapter getAdapter() {
        return this.adapter;
    }

    public final VH create(ClazzAdapter adapter, @NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.adapter = adapter;

        View view = onCreateView(adapter.getContext(), inflater, container);
        VH holder = onCreateViewHolder(view);

        return holder;
    }

    @NonNull
    public abstract View onCreateView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    @NonNull
    public abstract VH onCreateViewHolder(@NonNull View view);

    public abstract void onBindViewHolder(VH holder, T item, int position);
}
