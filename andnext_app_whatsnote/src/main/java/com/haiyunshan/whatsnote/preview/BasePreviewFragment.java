package com.haiyunshan.whatsnote.preview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.haiyunshan.whatsnote.preview.entity.PreviewMessage;

public abstract class BasePreviewFragment extends Fragment {

    PreviewMessage message;

    /**
     *
     * @param entity
     */
    public void setArguments(PreviewMessage entity) {
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
    PreviewMessage getMessage() {
        if (message == null) {
            message = PreviewMessage.create(this.getArguments());
        }

        return message;
    }

    @Nullable
    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public abstract void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);

}
