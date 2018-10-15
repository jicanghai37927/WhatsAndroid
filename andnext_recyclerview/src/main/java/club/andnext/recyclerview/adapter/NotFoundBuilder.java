package club.andnext.recyclerview.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotFoundBuilder extends ViewHolderBuilder {

    ViewTypePool pool;

    NotFoundBuilder(ViewTypePool pool) {
        this.pool = pool;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull View view) {
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Object item, int position) {
        String text;
        if (pool.isBind(item.getClass())) {
            text = "can't get suitable delegate by filter, please check filter for \"" + item.getClass().getName() + "\" at position " + position;
        } else {
            text = "can't find \"" + item.getClass().getName() + "\" registry at position " + position;
        }

        ((TextView)(holder.itemView)).setText(text);
    }
}
