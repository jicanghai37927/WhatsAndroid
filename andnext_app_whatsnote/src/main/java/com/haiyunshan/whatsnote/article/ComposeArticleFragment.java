package com.haiyunshan.whatsnote.article;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.haiyunshan.whatsnote.record.entity.RecentFactory;
import com.haiyunshan.whatsnote.directory.DirectoryManager;
import com.haiyunshan.whatsnote.R;
import club.andnext.recyclerview.helper.EditTouchHelper;

import java.io.File;
import java.util.ArrayList;
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
            this.document = Document.create(getActivity(), id);
        }

        {
            RecentFactory.put(getActivity(), document.getId());
        }

        {
            this.composeTouchHelper = new ComposeTouchHelper();
            composeTouchHelper.attach(recyclerView);
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if ((resultCode == RESULT_OK) && (data != null)) {
                    if (pictureUri != null) {
                        ArrayList<Uri> list = new ArrayList<>();
                        list.add(pictureUri);

                        this.insertPhotos(list);
                    }

                }

                pictureUri = null;
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

    void insertPhotos(List<Uri> list) {
        if ((list == null) || list.isEmpty()) {
            return;
        }

        ParagraphEntity result = null;

        ParagraphViewHolder holder = null;

        // check current focus holder is ParagraphViewHolder or not?
        {
            View focus = getActivity().getCurrentFocus();
            if (focus != null) {
                RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(focus);
                if (h instanceof ParagraphViewHolder) {
                    holder = (ParagraphViewHolder) h;
                }
            }
        }

        // we may split Paragraph text and inert photos
        if (holder != null) {

            ParagraphEntity entity = holder.getEntity();
            int index = document.indexOf(entity);
            boolean isPreviousParagraph = (index == 0)? false: (document.get(index - 1).getClass() == ParagraphEntity.class);

            if (holder.length() == 0) {

                if (isPreviousParagraph) {
                    insertPhotos(index, null, list);
                    result = entity;
                } else {
                    result = insertPhotos(index + 1, "", list);
                }

            } else {

                int start = holder.getSelectionStart();
                int end = holder.getSelectionEnd();
                if (start == end) {

                    int position = start;
                    if (position == 0) {
                        if (isPreviousParagraph) {
                            insertPhotos(index, null, list);
                            result = entity;
                        }
                    } else if (position == holder.length()) {
                        boolean isNextParagraph = (index + 1 >= document.size())? false: (document.get(index + 1).getClass() == ParagraphEntity.class);
                        if (isNextParagraph) {
                            insertPhotos(index + 1, null, list);

                            result = (ParagraphEntity)(document.get(index + 1));
                        }
                    }
                }
            }

            if (result == null) {
                CharSequence[] array = holder.split();
                if (array != null && array.length == 2) {
                    int position = document.indexOf(holder.getEntity());
                    if (position >= 0) {
                        result = insertPhotos(position + 1, array[1], list);
                        if (result != null) {
                            holder.setText(array[0]);
                            holder.setSelection(holder.length());
                        }
                    }
                }
            }
        }

        // if current focus is not Paragraph, we just append at last
        if (holder == null) {
            result = insertPhotos(document.size(), "", list);
        }

        // move to new position
        if (result != null) {
            final int index = document.indexOf(result);
            if (index >= 0) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        int position = index;
                        recyclerView.smoothScrollToPosition(position);

                        RecyclerView.ViewHolder h = recyclerView.findViewHolderForAdapterPosition(position);
                        if (h != null && h instanceof ParagraphViewHolder) {
                            ParagraphViewHolder holder = (ParagraphViewHolder)h;
                            holder.setSelection(0);
                            holder.requestFocus();
                        }
                    }
                });
            }
        }

        // save document
        if (result != null) {
            document.save();
        }
    }

    ParagraphEntity insertPhotos(int position, CharSequence text, List<Uri> list) {
        if ((list == null) || list.isEmpty()) {
            return null;
        }

        ParagraphEntity entity = null;

        int index = position;
        for (int i = 0, size = list.size(); i < size; i++) {
            Uri uri = list.get(i);

            PictureEntity pic = PictureEntity.create(document, uri);
            if (pic != null) {
                document.add(index, pic);
                ++index;

                CharSequence s = ((i + 1) == size)? text: "";
                if (text != null) {
                    ParagraphEntity en = ParagraphEntity.create(document, s);

                    document.add(index, en);
                    ++index;

                    entity = en;
                }
            }
        }

        int count = (index - position);
        if (count > 0) {
            adapter.notifyItemRangeInserted(position, count);
        }

        return entity;
    }

    void remove(ComposeViewHolder holder) {

        if (document.indexOf(holder.getEntity()) <= 0) {
            return;
        }

        if (holder instanceof ParagraphViewHolder) {

            removeParagraph((ParagraphViewHolder)holder);

        } else if (holder instanceof PictureViewHolder) {

            removePicture((PictureViewHolder)holder);

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

    /**
     * when insert photos, we insert a paragraph after it.
     * so if we remove pictures, we should remove paragraph too.
     *
     * @param holder
     */
    void removePicture(PictureViewHolder holder) {
        DocumentEntity entity = holder.getEntity();

        int index = document.indexOf(entity);
        if (index >= 0) {
            int position = index;
            int count = 1;

            // next one
            index = position + 1;
            if (index < document.size()) {
                DocumentEntity e = document.get(index);
                if (e.getClass() == ParagraphEntity.class) {
                    ParagraphEntity en = (ParagraphEntity)e;
                    if (en.getText().length() == 0) {
                        count += 1;
                    }
                }
            }

            // if the last paragraph did't removed, we try the previous one
            index = position - 1;
            if ((count == 1) && (index > 0)) { // cannot remove first paragraph
                DocumentEntity e = document.get(index);
                if (e.getClass() == ParagraphEntity.class) {
                    ParagraphEntity en = (ParagraphEntity)e;
                    if (en.getText().length() == 0) {
                        count += 1;

                        position -= 1;
                    }
                }
            }

            // remove them
            for (int i = 0; i < count; i++) {
                document.remove(position);
            }

            // notify changed
            adapter.notifyItemRangeRemoved(position, count);
        }

        if (index >= 0) {
            document.save();
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
                int position = previous.length();

                CharSequence s = holder.getText();
                if (s.length() != 0) {
                    s = previous.getText().append(s);
                    previous.setText(s);
                }

                previous.setSelection(position);
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
    private class ComposeMenuItemListener implements Toolbar.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.menu_add_photo: {
                    requestCamera();

                    break;
                }
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
