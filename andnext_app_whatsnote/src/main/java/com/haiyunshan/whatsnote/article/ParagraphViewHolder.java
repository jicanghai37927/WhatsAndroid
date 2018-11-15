package com.haiyunshan.whatsnote.article;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import club.andnext.helper.ClearAssistMenuHelper;
import com.haiyunshan.whatsnote.article.entity.ParagraphEntity;
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

    CharSequence[] split() {
        if (!editText.isFocused()) {
            return null;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start != end) {
            return null;
        }

        int position = start;
        CharSequence text = editText.getText();

        CharSequence first;
        CharSequence second;
        if ((position > 0) && (text.charAt(position - 1) == '\n')) {

            first = text.subSequence(0, position - 1);
            second = text.subSequence(position, text.length());

        } else if ((position < text.length()) && (text.charAt(position) == '\n')) {

            first = text.subSequence(0, position);
            second = text.subSequence(position + 1, text.length());

        } else {

            first = text.subSequence(0, position);
            second = text.subSequence(position, text.length());

        }

        return new CharSequence[] { first, second };
    }

    void setText(CharSequence text) {
        entity.setText(text);

        editText.setText(text);
    }

    Editable getText() {
        return editText.getText();
    }

    int length() {
        return editText.length();
    }

    void setSelection(int index) {
        editText.setSelection(index);
    }

    int getSelectionStart() {
        return editText.getSelectionStart();
    }

    int getSelectionEnd() {
        return editText.getSelectionEnd();
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
                        int start = getSelectionStart();
                        int end = getSelectionEnd();
                        if ((start == end) && (start == 0)) {
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
