package club.andnext.recyclerview.bridge;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.List;

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

    public void onBind(T item, int position, @NonNull List payloads) {
        this.onBind(item, position);
    }

    public void onViewAttachedToWindow() {

    }

    public void onViewDetachedFromWindow() {

    }
}
