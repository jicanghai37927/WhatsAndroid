package com.haiyunshan.whatsnote.preview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.haiyunshan.whatsnote.preview.entity.PreviewMessage;
import com.haiyunshan.whatsnote.R;

public class PicturePreviewFragment extends BasePreviewFragment {

    SubsamplingScaleImageView pictureView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        {
            this.pictureView = view.findViewById(R.id.iv_picture);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreviewMessage msg = this.getMessage();
        pictureView.setImage(ImageSource.uri(msg.getUri()));
    }
}
