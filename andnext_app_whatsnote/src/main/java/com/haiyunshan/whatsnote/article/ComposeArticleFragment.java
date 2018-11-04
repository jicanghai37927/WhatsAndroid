package com.haiyunshan.whatsnote.article;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.navigation.NavigationHelper;
import club.andnext.navigation.NavigationLayout;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import com.haiyunshan.article.Document;
import com.haiyunshan.article.DocumentEntity;
import com.haiyunshan.article.ParagraphEntity;
import com.haiyunshan.record.RecentEntity;
import com.haiyunshan.whatsnote.R;
import club.andnext.recyclerview.helper.EditTouchHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeArticleFragment extends Fragment {

    public static final String KEY_ID = "article.id";

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    EditTouchHelper editTouchHelper;

    Document document;

    public ComposeArticleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            String id = args.getString(KEY_ID, "demo");
            this.document = Document.obtain(id);
        }

        {
            RecentEntity.put(document.getId());
        }

        {
            this.editTouchHelper = new EditTouchHelper();
            editTouchHelper.attach(recyclerView);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new ComposeProvider());
            adapter.bind(ParagraphEntity.class,
                    new BridgeBuilder(ParagraphViewHolder.class, ParagraphViewHolder.LAYOUT_RES_ID, this));
        }

        {
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        this.save();
    }

    void save() {

        int count = recyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            ComposeViewHolder holder = getViewHolder(i);
            if (holder != null) {
                holder.save();
            }
        }

        {
            document.save();
        }

    }

    ComposeViewHolder getViewHolder(int index) {
        View child = recyclerView.getChildAt(index);
        RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(child);
        if (h != null && h instanceof ComposeViewHolder) {
            return (ComposeViewHolder)h;
        }

        return null;
    }

    /**
     *
     */
    private class ComposeProvider extends BridgeAdapterProvider<DocumentEntity> {

        @Override
        public DocumentEntity get(int position) {
            return document.get(position);
        }

        @Override
        public int size() {
            return document.size();
        }
    }
}
