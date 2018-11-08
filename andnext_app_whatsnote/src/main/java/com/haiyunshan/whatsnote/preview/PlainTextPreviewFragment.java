package com.haiyunshan.whatsnote.preview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.haiyunshan.preview.PreviewMessage;
import com.haiyunshan.whatsnote.R;

public class PlainTextPreviewFragment extends BasePreviewFragment {

    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plaintext_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        {
            this.textView = view.findViewById(R.id.tv_content);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreviewMessage entity = this.getMessage();
        String text = entity.getText(getActivity());

        textView.setText(text);

    }
}
