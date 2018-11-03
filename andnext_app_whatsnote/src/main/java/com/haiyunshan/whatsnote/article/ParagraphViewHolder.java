package com.haiyunshan.whatsnote.article;

import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import club.andnext.helper.ClearAssistMenuHelper;
import com.haiyunshan.article.ParagraphEntity;
import com.haiyunshan.whatsnote.R;

public class ParagraphViewHolder extends ComposeViewHolder<ParagraphEntity> {

    public static final int LAYOUT_RES_ID = R.layout.layout_compose_paragraph_list_item;

    EditText editText;

    public ParagraphViewHolder(ComposeArticleFragment f, View itemView) {
        super(f, itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        this.editText = view.findViewById(R.id.edit_paragraph);
        ClearAssistMenuHelper.attach(editText);
    }

    @Override
    public void onBind(ParagraphEntity item, int position) {
        super.onBind(item, position);

        editText.setText(item.getText());
    }

    @Override
    void save() {
        entity.setText(editText.getText());
    }

}
