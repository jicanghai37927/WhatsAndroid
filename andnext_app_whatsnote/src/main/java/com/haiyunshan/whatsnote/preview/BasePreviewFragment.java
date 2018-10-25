package com.haiyunshan.whatsnote.preview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.haiyunshan.preview.PreviewEntity;

public abstract class BasePreviewFragment extends Fragment {

    PreviewEntity entity;

    /**
     *
     * @param entity
     */
    public void setArguments(PreviewEntity entity) {
        Bundle args = entity.toBundle();
        this.setArguments(args);
    }

    /**
     *
     * @return
     */
    public boolean canPreview(Context context) {
        return true;
    }

    /**
     *
     * @return
     */
    PreviewEntity getEntity() {
        if (entity == null) {
            entity = PreviewEntity.create(this.getArguments());
        }

        return entity;
    }

    @Nullable
    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public abstract void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);

}
