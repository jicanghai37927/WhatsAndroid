package com.haiyunshan.whatsnote.article;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.utils.SoftInputUtils;
import com.haiyunshan.article.Document;
import com.haiyunshan.article.DocumentEntity;
import com.haiyunshan.article.ParagraphEntity;
import com.haiyunshan.article.PictureEntity;
import com.haiyunshan.record.RecentEntity;
import com.haiyunshan.whatsnote.R;
import club.andnext.recyclerview.helper.EditTouchHelper;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeArticleFragment extends Fragment {

    public static final String KEY_ID = "article.id";

    static final int REQUEST_PHOTO  = 1001;

    Toolbar toolbar;

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
            this.toolbar = view.findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.menu_compose_article);
            toolbar.setOnMenuItemClickListener(new ComposeMenuItemListener());
        }

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
            this.document = Document.create(id);
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
            adapter.bind(PictureEntity.class,
                    new BridgeBuilder(PictureViewHolder.class, PictureViewHolder.LAYOUT_RES_ID, this));
        }

        {
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PHOTO: {
                if ((resultCode == RESULT_OK) && (data != null)) {

                    ArrayList<Uri> list = new ArrayList<>();

                    {
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = item.getUri();
                                list.add(uri);
                            }
                        }

                        if (list.isEmpty()) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                list.add(uri);
                            }
                        }
                    }

                    this.insertPhotos(list);

                }

                break;
            }
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

    boolean requestPhoto() {
        SoftInputUtils.hide(getActivity());

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        try {
            this.startActivityForResult(intent, REQUEST_PHOTO);

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    void insertPhotos(List<Uri> list) {
        if ((list == null) || list.isEmpty()) {
            return;
        }

        int position = document.size();
        int count = 0;
        for (Uri uri : list) {
            PictureEntity pic = PictureEntity.create(document, uri);
            if (pic != null) {
                document.add(pic);
                ++count;

                ParagraphEntity en = ParagraphEntity.create(document, "");
                document.add(en);
                ++count;
            }
        }

        if (count > 0) {
            adapter.notifyItemRangeInserted(position, count);
        }

        if (count > 0) {
            document.save();
        }
    }

    void remove(ComposeViewHolder holder) {

        if (document.indexOf(holder.getEntity()) <= 0) {
            return;
        }

        if (holder instanceof ParagraphViewHolder) {

            removeParagraph((ParagraphViewHolder)holder);

        } else {
            DocumentEntity entity = holder.getEntity();

            int index = document.remove(entity);
            if (index >= 0) {
                adapter.notifyItemRemoved(index);
            }

            if (index >= 0) {
                document.save();
            }
        }


    }

    void removeParagraph(ParagraphViewHolder holder) {

        DocumentEntity entity = holder.getEntity();
        if (document.size() == 1 || document.indexOf(entity) == 0) {
            return;
        }

        ParagraphViewHolder previous = null;
        int index = recyclerView.indexOfChild(holder.itemView);
        if (index > 0) {
            RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(recyclerView.getChildAt(index - 1));
            if (h instanceof ParagraphViewHolder) {
                previous = (ParagraphViewHolder)h;
            }
        }

        if (previous != null) {
            index = document.remove(entity);
            if (index >= 0) {
                previous.setSelection(previous.length());
                previous.requestFocus();
            }

            if (index >= 0) {
                adapter.notifyItemRemoved(index);
            }

            if (index >= 0) {
                document.save();
            }
        }

    }

    /**
     *
     */
    private class ComposeMenuItemListener implements Toolbar.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.menu_insert_photo: {
                    requestPhoto();

                    break;
                }
            }

            return false;
        }
    }
    /**
     *
     */
    private class ComposeProvider implements BridgeAdapterProvider<DocumentEntity> {

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
