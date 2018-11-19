package com.haiyunshan.whatsnote.article;

import androidx.recyclerview.widget.RecyclerView;
import com.haiyunshan.whatsnote.article.entity.Document;
import com.haiyunshan.whatsnote.article.entity.DocumentEntity;
import com.haiyunshan.whatsnote.article.entity.ParagraphEntity;

class BaseRemove<T extends ComposeViewHolder> {

    T holder;

    Document document;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    ComposeArticleFragment parent;

    BaseRemove(ComposeArticleFragment f, T holder) {
        this.holder = holder;

        this.parent = f;

        this.document = f.document;

        this.recyclerView = f.recyclerView;
        this.adapter = f.adapter;
    }

    void execute() {

    }
}

class ParagraphRemove extends BaseRemove<ParagraphViewHolder> {

    public ParagraphRemove(ComposeArticleFragment f, ParagraphViewHolder holder) {
        super(f, holder);
    }

    @Override
    void execute() {

        DocumentEntity entity = holder.getEntity();
        if (document.size() == 1 || document.indexOf(entity) == 0) {
            return;
        }

        ParagraphViewHolder previous = null;
        int index = recyclerView.indexOfChild(holder.itemView);
        if (index > 0) {
            RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(recyclerView.getChildAt(index - 1));
            if (h instanceof ParagraphViewHolder) {
                previous = (ParagraphViewHolder)h;
            }
        }

        if (previous != null) {
            index = document.remove(entity);
            if (index >= 0) {
                int position = previous.length();

                CharSequence s = holder.getText();
                if (s.length() != 0) {
                    s = previous.getText().append(s);
                    previous.setText(s);
                }

                previous.setSelection(position);
                previous.requestFocus();
            }

            if (index >= 0) {
                adapter.notifyItemRemoved(index);
            }

            if (index >= 0) {
                document.save();
            }
        }

    }
}

/**
 *
 */
class PictureRemove extends BaseRemove<PictureViewHolder> {

    public PictureRemove(ComposeArticleFragment f, PictureViewHolder holder) {
        super(f, holder);
    }

    @Override
    void execute() {
        DocumentEntity entity = holder.getEntity();

        int index = document.indexOf(entity);
        if (index >= 0) {
            int position = index;
            int count = 1;

            // next one
            index = position + 1;
            if (index < document.size()) {
                DocumentEntity e = document.get(index);
                if (e.getClass() == ParagraphEntity.class) {
                    ParagraphEntity en = (ParagraphEntity)e;
                    if (en.getText().length() == 0) {
                        count += 1;
                    }
                }
            }

            // if the last paragraph did't removed, we try the previous one
            index = position - 1;
            if ((count == 1) && (index > 0)) { // cannot remove first paragraph
                DocumentEntity e = document.get(index);
                if (e.getClass() == ParagraphEntity.class) {
                    ParagraphEntity en = (ParagraphEntity)e;
                    if (en.getText().length() == 0) {
                        count += 1;

                        position -= 1;
                    }
                }
            }

            // remove them
            for (int i = 0; i < count; i++) {
                document.remove(position);
            }

            // notify changed
            adapter.notifyItemRangeRemoved(position, count);
        }

        if (index >= 0) {
            document.save();
        }
    }
}
