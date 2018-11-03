package com.haiyunshan.article;

import android.text.SpannableStringBuilder;

public class ParagraphEntity extends DocumentEntity<ParagraphEntry> {

    SpannableStringBuilder text;

    public ParagraphEntity(ParagraphEntry entry) {
        super(entry);

        this.setText(entry.getText());
    }

    @Override
    void save() {
        entry.setText(text.toString());
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        if (text instanceof SpannableStringBuilder) {
            this.text = (SpannableStringBuilder)text;
        }

        this.text = new SpannableStringBuilder(text);
    }
}
