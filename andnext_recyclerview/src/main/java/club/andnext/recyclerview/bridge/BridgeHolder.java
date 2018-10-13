package club.andnext.recyclerview.bridge;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BridgeHolder<T> extends RecyclerView.ViewHolder {

    public BridgeHolder(View itemView) {
        super(itemView);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public abstract int getLayoutResourceId();

    public abstract void onViewCreated(@NonNull View view);

    public abstract void onBind(T item, int position);
}
