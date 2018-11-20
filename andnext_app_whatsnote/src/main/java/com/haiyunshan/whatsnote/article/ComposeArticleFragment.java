package com.haiyunshan.whatsnote.article;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
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
import club.andnext.utils.UriUtils;
import com.haiyunshan.whatsnote.article.entity.Document;
import com.haiyunshan.whatsnote.article.entity.DocumentEntity;
import com.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import com.haiyunshan.whatsnote.article.entity.PictureEntity;
import com.haiyunshan.whatsnote.directory.DirectoryManager;
import com.haiyunshan.whatsnote.R;
import club.andnext.recyclerview.helper.EditTouchHelper;
import com.haiyunshan.whatsnote.record.entity.RecentEntity;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;
import com.haiyunshan.whatsnote.record.entity.SavedStateEntity;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeArticleFragment extends Fragment {

    public static final String KEY_ID = "article.id";

    static final int REQUEST_CAMERA  = 1001;
    static final int REQUEST_PHOTO  = 1002;

    Toolbar toolbar;

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    ComposeTouchHelper composeTouchHelper;

    ComposeCallback composeCallback;

    Uri pictureUri;

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
            toolbar.setOnMenuItemClickListener(new ComposeMenuItemListener(this));
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

        {
            ComposeScrollListener scrollListener = new ComposeScrollListener();
            recyclerView.addOnScrollListener(scrollListener);
        }

        {
//            recyclerView.getItemAnimator().setChangeDuration(0);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.composeCallback = new ComposeCallback(this);
        }

        {
            Bundle args = this.getArguments();
            String id = args.getString(KEY_ID, "demo");
            this.document = Document.create(getActivity(), id);
        }

        {
            RecentEntity.put(document.getId());
        }

        {
            this.composeTouchHelper = new ComposeTouchHelper();
            composeTouchHelper.attach(recyclerView);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new ComposeProvider());
            adapter.bind(ParagraphEntity.class,
                    new BridgeBuilder(ParagraphViewHolder.class, ParagraphViewHolder.LAYOUT_RES_ID, composeCallback)
                        .setParameterTypes(ComposeViewHolder.Callback.class));
            adapter.bind(PictureEntity.class,
                    new BridgeBuilder(PictureViewHolder.class, PictureViewHolder.LAYOUT_RES_ID, composeCallback)
                        .setParameterTypes(ComposeViewHolder.Callback.class));
        }

        {
            recyclerView.setAdapter(adapter);
        }

        {
            boolean showSoftInput = false;

            if (document.size() == 1) {
                DocumentEntity e = document.get(0);
                if (e.getClass() == ParagraphEntity.class) {
                    ParagraphEntity entity = (ParagraphEntity) e;
                    if (entity.getText().length() == 0) {
                        showSoftInput = true;
                    }
                }
            }

            if (showSoftInput) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        EditText edit = findEditText(recyclerView);
                        if (edit != null) {
                            SoftInputUtils.show(getActivity(), edit);
                        }
                    }
                });
            }
        }

        {
            final SavedStateEntity params = document.getRecord().getSavedState();

            final String id = params.getOffsetId();
            int index = (!TextUtils.isEmpty(id))? (document.indexOf(id)): -1;
            if (index > 0) {
                recyclerView.scrollToPosition(index);
            }

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    {
                        int vertical = params.getOffsetTop();
                        int textOffset = params.getTextOffset();

                        int y = vertical;

                        if (textOffset > 0) {
                            ParagraphViewHolder holder = findParagraphViewHolder(id);
                            if (holder != null) {
                                y = holder.getVertical(textOffset);
                            }
                        }

                        recyclerView.scrollBy(0, y);
                    }

                    boolean enable = false;
                    if (enable) {
                        String id = params.getFocusId();
                        int start = params.getSelectionStart();
                        int end = params.getSelectionEnd();
                        if (!(TextUtils.isEmpty(id)) && start >= 0 && end >= 0) {
                            ParagraphViewHolder holder = findParagraphViewHolder(id);
                            if (holder != null) {
                                holder.setSelection(start, end);
                                holder.requestFocus();
                            }
                        }
                    }
                }
            });


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA: {

                if ((resultCode == RESULT_OK) && (data != null)) {

                    ArrayList<Uri> list = null;

                    if (pictureUri != null) {
                        list = new ArrayList<>();
                        list.add(pictureUri);
                    }

                    if (list != null && !list.isEmpty()) {
                        InsertFactory.createPicture(this, list).execute();
                    }

                }

                {
                    pictureUri = null;
                }

                break;
            }

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

                    {
                        InsertFactory.createPicture(this, list).execute();
                    }
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

        {
            int count = recyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                ComposeViewHolder holder = getViewHolder(i);
                if (holder != null) {
                    holder.save();
                }
            }
        }

        {
            this.saveState(document.getRecord().getSavedState());
        }

        {
            document.save();
        }

    }

    EditText findEditText(View view) {
        if (view instanceof EditText) {
            return (EditText)view;
        }

        if (view instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup)view;
            for (int i = 0, size = layout.getChildCount(); i < size; i++) {
                EditText edit = findEditText(layout.getChildAt(i));
                if (edit != null) {
                    return edit;
                }
            }
        }

        return null;
    }

    ComposeViewHolder getViewHolder(int index) {
        View child = recyclerView.getChildAt(index);
        RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(child);
        if (h != null && h instanceof ComposeViewHolder) {
            return (ComposeViewHolder)h;
        }

        return null;
    }

    boolean requestCamera() {
        SoftInputUtils.hide(getActivity());

        Uri imageUri;
        {
            File file = DirectoryManager.getInstance().getDirectory(getActivity(), DirectoryManager.DIR_CAMERA_PHOTO);
            file = new File(file, "IMG_" + System.currentTimeMillis() + ".jpg");
            imageUri = UriUtils.fromFile(getActivity(), file);
        }

        Intent intent = new Intent();
        {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }

        try {

            this.startActivityForResult(intent, REQUEST_CAMERA);
            this.pictureUri = imageUri;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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

    void saveState(SavedStateEntity savedState) {
        if (recyclerView.getChildCount() == 0) {
            savedState.clear();

            return;
        }

        // offset
        {
            View child = recyclerView.getChildAt(0);
            ComposeViewHolder h = (ComposeViewHolder) (recyclerView.findContainingViewHolder(child));
            DocumentEntity entity = h.getEntity();
            int index = document.indexOf(entity);

            String id = entity.getId();
            int top = child.getTop();
            int textOffset = 0;

            if (h instanceof ParagraphViewHolder) {
                ParagraphViewHolder holder = (ParagraphViewHolder) h;
                textOffset = holder.getOffset();
            }

            if (index == 0 && top >= 0) {
                id = null;
            }

            savedState.setOffset(id, Math.abs(top), textOffset);
        }

        // focus
        {
            View view = getActivity().getCurrentFocus();
            if (view != null) {

                String id = null;
                int start = -1;
                int end = -1;

                RecyclerView.ViewHolder h = (recyclerView.findContainingViewHolder(view));
                if (h != null && h instanceof ParagraphViewHolder) {
                    ParagraphViewHolder holder = (ParagraphViewHolder)h;

                    id = (holder.getEntity().getId());
                    start = ((holder.getSelectionStart()));
                    end = ((holder.getSelectionEnd()));
                }

                savedState.setFocus(id, start, end);
            }
        }

    }

    ParagraphViewHolder findParagraphViewHolder(String id) {
        for (int i = 0, size = recyclerView.getChildCount(); i < size; i++) {
            View child = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(child);
            if (h instanceof ParagraphViewHolder) {
                ParagraphViewHolder holder = (ParagraphViewHolder)h;
                if (holder.getEntity().getId().equals(id)) {
                    return holder;
                }
            }
        }

        return null;
    }

    /**
     *
     */
    private class ComposeScrollListener extends RecyclerView.OnScrollListener {

        int scrollState = RecyclerView.SCROLL_STATE_IDLE;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if ((scrollState != RecyclerView.SCROLL_STATE_IDLE)
                && (newState == RecyclerView.SCROLL_STATE_IDLE)) {

            }

            this.scrollState = newState;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    /**
     *
     */
    private class ComposeTouchHelper extends EditTouchHelper {

        @Override
        protected TextView findTextView(View child) {
            RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(child);

            // only handle touch for ParagraphViewHolder
            if ((h != null) && (!(h instanceof ParagraphViewHolder))) {
                return null;
            }

            return super.findTextView(child);
        }
    }

    /**
     *
     */
    private static class ComposeMenuItemListener implements Toolbar.OnMenuItemClickListener {

        ComposeArticleFragment parent;

        ComposeMenuItemListener(ComposeArticleFragment f) {
            this.parent = f;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.menu_insert_paragraph: {

                    InsertFactory.createParagraph(parent).execute();

                    break;
                }

                case R.id.menu_add_photo: {
                    parent.requestCamera();

                    break;
                }
                case R.id.menu_insert_photo: {
                    parent.requestPhoto();

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

    /**
     *
     */
    private static class ComposeCallback extends ComposeViewHolder.Callback {

        HashMap<Class<? extends ComposeViewHolder>, Class<? extends BaseRemove>> removeMap;

        ComposeArticleFragment parent;

        ComposeCallback(ComposeArticleFragment f) {
            this.parent = f;

            {
                this.removeMap = new HashMap<>();
                removeMap.put(ParagraphViewHolder.class, ParagraphRemove.class);
                removeMap.put(PictureViewHolder.class, PictureRemove.class);
            }

        }

        @Override
        public Activity getContext() {
            return parent.getActivity();
        }

        @Override
        public void remove(ComposeViewHolder holder) {
            Document document = parent.document;
            RecyclerView.Adapter adapter = parent.adapter;

            DocumentEntity entity = holder.getEntity();

            if (document.indexOf(entity) <= 0) {
                return;
            }

            Class<? extends BaseRemove> clz = removeMap.get(holder.getClass());
            if (clz == null) {

                int index = document.remove(entity);
                if (index >= 0) {
                    adapter.notifyItemRemoved(index);
                }

                if (index >= 0) {
                    document.save();
                }

            } else {

                try {
                    Constructor<? extends BaseRemove> c = clz.getConstructor(ComposeArticleFragment.class, holder.getClass());
                    BaseRemove obj = c.newInstance(parent, holder);
                    obj.execute();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getMaxWidth() {
            int width = parent.recyclerView.getWidth();
            if (width > 0) {
                width -= parent.recyclerView.getPaddingLeft();
                width -= parent.recyclerView.getPaddingRight();
            }

            if (width <= 0) {
                width = super.getMaxWidth();
            }

            return width;
        }

        @Override
        public int getMaxHeight() {
            int width = parent.recyclerView.getHeight();

            if (width <= 0) {
                width = super.getMaxHeight();
            }

            return width;
        }
    }
}
