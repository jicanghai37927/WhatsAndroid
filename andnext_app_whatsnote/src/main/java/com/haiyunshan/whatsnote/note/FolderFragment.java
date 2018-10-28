package com.haiyunshan.whatsnote.note;


import android.os.Bundle;
import android.view.MenuItem;
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
import club.andnext.recyclerview.bridge.BridgeHolder;
import com.haiyunshan.note.FileEntity;
import com.haiyunshan.note.FolderDataset;
import com.haiyunshan.note.NoteDataset;
import com.haiyunshan.note.NoteManager;
import com.haiyunshan.whatsnote.FolderActivity;
import com.haiyunshan.whatsnote.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderFragment extends Fragment {

    Toolbar toolbar;

    RecyclerView recyclerView;
    BridgeAdapter adapter;

    String parentFolder;
    List<FileEntity> fileList;

    public FolderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.toolbar = view.findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.menu_folder);
            toolbar.setOnMenuItemClickListener(new FolderMenuItemListener(this));
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

        {
            view.findViewById(R.id.btn_create_folder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createFolder();
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.parentFolder = getArguments().getString("parent", "");
            this.fileList = NoteManager.getInstance().getList(parentFolder, NoteManager.TYPE_ALL, fileList);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new FileProvider());

            adapter.bind(NoteDataset.NoteEntity.class,
                    new BridgeBuilder(NoteViewHolder.class, NoteViewHolder.LAYOUT_RES_ID, this));
            adapter.bind(FolderDataset.FolderEntity.class,
                    new BridgeBuilder(FolderViewHolder.class, FolderViewHolder.LAYOUT_RES_ID, this));

        }

        {
            recyclerView.setAdapter(adapter);
        }
    }

    void createFolder() {
        FileEntity entity = NoteManager.getInstance().create(parentFolder, NoteManager.TYPE_FOLDER);

        String name = "新建文件夹";
        name = NoteManager.getInstance().getName(entity, name);
        entity.setAlias(name);

        fileList.add(entity);

        adapter.notifyItemInserted(fileList.size() - 1);
    }

    void createNote() {
        FileEntity entity = NoteManager.getInstance().create(parentFolder, NoteManager.TYPE_NOTE);

        String name = "空白笔记";
        name = NoteManager.getInstance().getName(entity, name);
        entity.setAlias(name);

        fileList.add(entity);

        adapter.notifyItemInserted(fileList.size() - 1);
    }

    /**
     *
     */
    private class FileProvider extends BridgeAdapterProvider {

        @Override
        public Object get(int position) {
            return fileList.get(position);
        }

        @Override
        public int size() {
            return fileList.size();
        }
    }

    /**
     *
     */
    private static class FolderViewHolder extends BridgeHolder<FolderDataset.FolderEntity> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        FolderFragment parent;

        public FolderViewHolder(FolderFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this);
        }

        @Override
        public void onBind(FolderDataset.FolderEntity item, int position) {
            ((TextView)itemView).setText(item.getDisplayName());
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                this.click();
            }
        }

        void click() {
            FolderActivity.start(parent.getActivity(), getEntity().getId());
        }

        FolderDataset.FolderEntity getEntity() {
            int position = this.getAdapterPosition();
            FileEntity en = parent.fileList.get(position);

            return (FolderDataset.FolderEntity)en;
        }
    }

    /**
     *
     */
    public static class NoteViewHolder extends BridgeHolder<NoteDataset.NoteEntity> {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        public NoteViewHolder(FolderFragment f, View itemView) {
            super(itemView);
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {

        }

        @Override
        public void onBind(NoteDataset.NoteEntity item, int position) {
            ((TextView)itemView).setText(item.getDisplayName());
        }
    }

    /**
     *
     */
    private static class FolderMenuItemListener implements Toolbar.OnMenuItemClickListener {

        final FolderFragment parent;

        public FolderMenuItemListener(FolderFragment f) {
            this.parent = f;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.btn_create_note: {

                    parent.createNote();

                    break;
                }
            }

            return false;
        }
    }
}
