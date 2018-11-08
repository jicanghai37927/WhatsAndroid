package com.haiyunshan.whatsnote.article;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import club.andnext.helper.ClearAssistMenuHelper;
import com.haiyunshan.article.ParagraphEntity;
import com.haiyunshan.whatsnote.R;

public class ParagraphViewHolder extends ComposeViewHolder<ParagraphEntity> {

    public static final int LAYOUT_RES_ID = R.layout.layout_compose_paragraph_list_item;

    EditText editText;

    ParagraphKeyListener keyListener;

    public ParagraphViewHolder(ComposeArticleFragment f, View itemView) {
        super(f, itemView);

        this.keyListener = new ParagraphKeyListener();
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

        {
            editText.setOnKeyListener(keyListener);
        }

        {
            editText.setText(item.getText());
        }
    }

    @Override
    void save() {
        entity.setText(editText.getText());
    }

    int length() {
        return editText.length();
    }

    void setSelection(int index) {
        editText.setSelection(index);
    }

    void requestFocus() {
        editText.requestFocus();
    }

    /**
     *
     */
    private class ParagraphKeyListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int action = event.getAction();
            switch (action) {
                case KeyEvent.ACTION_DOWN: {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (editText.length() == 0) {
                            remove();
                        }
                    }

                    break;
                }
            }

            return false;
        }

    }
}
