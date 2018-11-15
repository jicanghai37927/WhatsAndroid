package com.haiyunshan.whatsnote.article.entity;

import android.text.SpannableStringBuilder;
import androidx.annotation.CallSuper;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.whatsnote.article.dataset.ParagraphEntry;

public class ParagraphEntity<T extends ParagraphEntry> extends DocumentEntity<T> {

    SpannableStringBuilder text;

    public ParagraphEntity(Document d, T entry) {
        super(d, entry);
    }

    @Override
    @CallSuper
    void save() {
        if (text != null) {
            entry.setText(text.toString());
        }
    }

    public CharSequence getText() {
        if (text == null) {
            this.setText(entry.getText());
        }

        return text;
    }

    public void setText(CharSequence text) {
        text = (text == null)? "": text;

        if (text instanceof SpannableStringBuilder) {
            this.text = (SpannableStringBuilder)text;
        } else {
            this.text = new SpannableStringBuilder(text);
        }
    }

    public static final ParagraphEntity create(Document document, CharSequence text) {
        ParagraphEntry entry = new ParagraphEntry(UUIDUtils.next());

        ParagraphEntity entity = new ParagraphEntity(document, entry);
        entity.setText(text);

        return entity;
    }
}
