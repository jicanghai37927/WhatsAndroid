package club.andnext.recyclerview.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ClazzAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ViewTypePool pool;
    ClazzAdapterProvider provider;

    NotFoundBuilder notFoundDelegate;

    LayoutInflater inflater;
    Context context;

    public ClazzAdapter(Context context, ClazzAdapterProvider provider) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        this.pool = new ViewTypePool();
        this.provider = provider;
    }

    public Context getContext() {
        return this.context;
    }

    public ClazzAdapter bind(Class<?> clazz, ViewHolderBuilder... builders) {
        pool.bind(clazz, builders);

        return this;
    }

    public ClazzAdapter bind(Class<?> clazz, BuilderFilter filter) {
        pool.bind(clazz, filter);

        return this;
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = provider.get(position);
        int type = pool.getViewType(obj);

        return type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolderBuilder delegate = pool.getDelegate(viewType);
        if (delegate == null) {
            if (this.notFoundDelegate == null) {
                this.notFoundDelegate = new NotFoundBuilder(this.pool);
            }

            delegate = this.notFoundDelegate;
        }

        RecyclerView.ViewHolder holder = delegate.create(this, inflater, parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderBuilder delegate = pool.getDelegate(holder.getItemViewType());
        if (delegate == null) {
            delegate = this.notFoundDelegate;
        }

        Object obj = provider.get(position);
        delegate.onBindViewHolder(holder, obj, position);

    }

    @Override
    public int getItemCount() {
        return provider.size();
    }
}
