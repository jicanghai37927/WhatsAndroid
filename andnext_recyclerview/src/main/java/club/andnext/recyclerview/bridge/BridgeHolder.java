package club.andnext.recyclerview.bridge;

import android.content.Context;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import club.andnext.recyclerview.adapter.ClazzAdapter;

import java.util.List;

public abstract class BridgeHolder<T> extends RecyclerView.ViewHolder {

    public BridgeHolder(View itemView) {
        super(itemView);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public T getItem() {
        int position = this.getAdapterPosition();
        if (position < 0) {
            return null;
        }

        ViewParent parent = itemView.getParent();
        if (parent instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView)parent;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();

            if (adapter instanceof ClazzAdapter) {
                ClazzAdapter clazzAdapter = (ClazzAdapter)adapter;
                T obj = (T)(clazzAdapter.get(position));

                return obj;
            }
        }

        return null;
    }

    public abstract int getLayoutResourceId();

    public abstract void onViewCreated(@NonNull View view);

    public abstract void onBind(T item, int position);

    public void onBind(T item, int position, @NonNull List payloads) {
        this.onBind(item, position);
    }

    public void onViewAttachedToWindow() {

    }

    public void onViewDetachedFromWindow() {

    }
}
