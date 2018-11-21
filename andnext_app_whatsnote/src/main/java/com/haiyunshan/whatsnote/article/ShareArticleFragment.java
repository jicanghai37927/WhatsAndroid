package com.haiyunshan.whatsnote.article;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.snapshot.Snapshot;
import club.andnext.utils.CharsetUtils;
import club.andnext.utils.PackageUtils;
import club.andnext.utils.UriUtils;
import com.google.android.material.snackbar.Snackbar;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.article.entity.Document;
import com.haiyunshan.whatsnote.article.entity.DocumentEntity;
import com.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import com.haiyunshan.whatsnote.article.entity.PictureEntity;
import com.haiyunshan.whatsnote.author.AuthorViewHolder;
import com.haiyunshan.whatsnote.author.entity.AuthorEntity;
import com.haiyunshan.whatsnote.directory.DirectoryManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareArticleFragment extends Fragment implements View.OnClickListener {

    public static final String KEY_ID = "article.id";

    View shareBtn;

    RecyclerView recyclerView;
    BridgeAdapter adapter;

    ItemTouchHelper itemTouchHelper;

    Document document;
    ComposeProvider provider;

    ComposeCallback composeCallback;

    public ShareArticleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_share_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.shareBtn = view.findViewById(R.id.btn_share);
            shareBtn.setOnClickListener(this);
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
            this.composeCallback = new ComposeCallback(this);
        }

        {
            Bundle args = this.getArguments();
            String id = args.getString(KEY_ID, "demo");
            this.document = Document.create(getActivity(), id);

            this.provider = new ComposeProvider(document);

            AuthorEntity authorEntity = AuthorEntity.create("");
            provider.add(authorEntity);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), provider);
            adapter.bind(ParagraphEntity.class,
                    new BridgeBuilder(ParagraphViewHolder.class, ParagraphViewHolder.LAYOUT_RES_ID, composeCallback)
                            .setParameterTypes(ComposeViewHolder.Callback.class));
            adapter.bind(PictureEntity.class,
                    new BridgeBuilder(PictureViewHolder.class, PictureViewHolder.LAYOUT_RES_ID, composeCallback)
                            .setParameterTypes(ComposeViewHolder.Callback.class));
        }

        {
            adapter.bind(AuthorEntity.class,
                    new BridgeBuilder(AuthorViewHolder.class, AuthorViewHolder.LAYOUT_RES_ID));
        }

        {
            ItemTouchCallback callback = new ItemTouchCallback(this);
            this.itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        {
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == shareBtn) {
            requestShare();
        }
    }

    void requestShare() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] items = new CharSequence[] {
                "保存为图片",
                "保存为纯文本文件",
                "保存为Markdown文件",
                "分享为长图片"
        };

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        Bitmap bitmap = saveAsBitmap(true);
                        if (bitmap != null) {
                            bitmap.recycle();
                        }

                        break;
                    }
                    case 1: {
                        saveAsPlainText();
                        break;
                    }
                    case 2: {
                        saveAsMarkdown();
                        break;
                    }
                    case 3: {
                        share();
                        break;
                    }
                }
            }
        });
        builder.setPositiveButton(android.R.string.ok, null);

        builder.show();
    }

    Bitmap saveAsBitmap(boolean notify) {
        String name = document.getRecord().getName();
        name = (TextUtils.isEmpty(name))? "note": name;

        File file = DirectoryManager.getInstance().getDirectory(getActivity(), DirectoryManager.DIR_EXPORT_PICTURE);
        file = getFile(file, name, ".jpg");

        Bitmap bitmap = Snapshot.capture(recyclerView);
        try {
            boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG, 40, new FileOutputStream(file));
            if (!result) {
                file = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (!notify) {
            return bitmap;
        }

        if (file != null) {
            final File target = file;

            Snackbar snackbar = Snackbar.make(recyclerView, "已保存", Snackbar.LENGTH_LONG);
            snackbar.setAction("查看", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view(getActivity(), UriUtils.fromFile(getActivity(), target), "image/*");
                }
            });
            snackbar.getView().setElevation(100);
            snackbar.show();
        }

        return bitmap;

    }

    void saveAsPlainText() {

    }

    void saveAsMarkdown() {

    }

    void share() {
        Bitmap bitmap = saveAsBitmap(false);
        if (bitmap == null) {
            return;
        }

        String url = insertImageToSystem(getActivity(), bitmap);
        if (TextUtils.isEmpty(url)) {
            bitmap.recycle();
            return;
        }

        send(getActivity(), Uri.parse(url), "image/*");
        bitmap.recycle();
    }

    private static String insertImageToSystem(Context context, Bitmap bitmap) {

        String url = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Hello", "Hello");

        return url;
    }


    public static final boolean send(Context context, Uri uri, String type) {

        Intent intent = new Intent();

        // mime-type
        {
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        // picture
        {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND)) {

                intent.removeExtra(Intent.EXTRA_STREAM);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }

            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND_MULTIPLE)) {

                ArrayList<Uri> imageUris = new ArrayList();
                imageUris.add(uri); // Add your image URIs here

                intent.removeExtra(Intent.EXTRA_STREAM);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }
        }

        // start
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {

        }

        return false;
    }

    private void shareImage(String kdescription, ArrayList<Uri> paths) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND_MULTIPLE");
        intent.setType("image/*");
        intent.putExtra("Kdescription", kdescription);
        intent.putExtra(Intent.EXTRA_STREAM, paths);
        getActivity().startActivity(intent);


    }

    public static final boolean view(Context context, Uri uri, String type) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setDataAndType(uri, type);

        try {
            context.startActivity(intent);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    File getFile(File dir, String name, String suffix) {
        String target = name;

        {
            File file = new File(dir, target + suffix);
            if (!file.exists()) {
                return file;
            }
        }

        int index = 2;
        while (true) {
            target = name + "(" + index + ")";

            File file = new File(dir, target + suffix);
            if (!file.exists()) {
                return file;
            }

            ++index;
        }
    }

    /**
     *
     */
    private class ComposeProvider implements BridgeAdapterProvider<Object> {

        ArrayList<Object> list;

        public ComposeProvider(Document document) {
            this.list = new ArrayList<>();
            list.addAll(document.getCollection());
        }

        @Override
        public Object get(int position) {
            return list.get(position);
        }

        @Override
        public int size() {
            return list.size();
        }

        public Object remove(int index) {
            return list.remove(index);
        }

        public void add(int index, Object obj) {
            list.add(index, obj);
        }

        public void add(Object obj) {
            list.add(obj);
        }
    }

    /**
     *
     */
    private static class ComposeCallback extends ComposeViewHolder.Callback {

        ComposeCallback(ShareArticleFragment f) {
            super(f.getActivity());

            this.enable = false;
        }

        @Override
        public void remove(ComposeViewHolder holder) {

        }

    }

    /**
     *
     */
    private static class ItemTouchCallback extends ItemTouchHelper.Callback {

        ShareArticleFragment parent;

        ItemTouchCallback(ShareArticleFragment f) {
            this.parent = f;
        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            float defaultValue = super.getSwipeThreshold(viewHolder);
            float value = 1.f;
            value = (value > defaultValue)? value: defaultValue;

            return value;
        }

        @Override
        public float getSwipeVelocityThreshold(float defaultValue) {
            float value = 1600;
            value = (value > defaultValue)? value: defaultValue;

            return value;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {

            int dragDirs = 0;
            int swipeDirs = 0;

            int position = viewHolder.getAdapterPosition();
            Object obj = parent.provider.get(position);
            if ((obj instanceof DocumentEntity)) {
                swipeDirs = ItemTouchHelper.LEFT;
            }

            return makeMovementFlags(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            final Object obj = parent.provider.remove(position);
            parent.adapter.notifyItemRemoved(position);

            Snackbar snackbar = Snackbar.make(parent.recyclerView, "文章内容已经移除。", Snackbar.LENGTH_LONG);
            snackbar.setAction("撤销", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.provider.add(position, obj);
                    parent.adapter.notifyItemInserted(position);

                    parent.recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            parent.recyclerView.smoothScrollToPosition(position);
                        }
                    }, 100);

                }
            });
            snackbar.getView().setElevation(100);
            snackbar.show();
        }
    }
}
