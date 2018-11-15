package com.haiyunshan.whatsnote.preview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import club.andnext.markdown.MarkdownWebView;
import com.haiyunshan.whatsnote.preview.entity.PreviewMessage;
import com.haiyunshan.whatsnote.R;

public class MarkdownPreviewFragment extends BasePreviewFragment {

    MarkdownWebView markdownView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_markdown_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        {
            this.markdownView = view.findViewById(R.id.wv_markdown);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreviewMessage entity = this.getMessage();
        String text = entity.getText(getActivity());
        markdownView.setText(text);

    }
}
