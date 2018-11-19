package com.haiyunshan.whatsnote.article;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.haiyunshan.whatsnote.article.entity.Document;
import com.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import com.haiyunshan.whatsnote.article.entity.PictureEntity;

import java.util.List;

/**
 *
 */
abstract class BaseInsert {

    Document document;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    ComposeArticleFragment parent;

    boolean alwaysSplit;

    BaseInsert(ComposeArticleFragment f) {
        this.parent = f;

        this.document = f.document;

        this.recyclerView = f.recyclerView;
        this.adapter = f.adapter;

        this.alwaysSplit = false;
    }

    abstract ParagraphEntity insert(int position, CharSequence text);

    final Activity getActivity() {
        return parent.getActivity();
    }

    final ParagraphViewHolder getFocus() {
        ParagraphViewHolder holder = null;

        {
            View focus = getActivity().getCurrentFocus();
            if (focus != null) {
                RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(focus);
                if (h instanceof ParagraphViewHolder) {
                    holder = (ParagraphViewHolder) h;
                }
            }
        }

        return holder;
    }

    void execute() {

        ParagraphEntity result = null;

        ParagraphViewHolder focus = getFocus();
        ParagraphEntity current = (focus != null)? focus.getEntity(): null;

        // we may split Paragraph text and inert photos
        if (focus != null) {

            if (!alwaysSplit) {

                ParagraphEntity entity = focus.getEntity();
                int index = document.indexOf(entity);
                boolean isPreviousParagraph = (index == 0) ? false : (document.get(index - 1).getClass() == ParagraphEntity.class);

                if (focus.length() == 0) {

                    if (isPreviousParagraph) {
                        result = insert(index, null);
                        if (result == null) {
                            result = entity;
                        }
                    } else {
                        result = insert(index + 1, "");
                    }

                } else {

                    int start = focus.getSelectionStart();
                    int end = focus.getSelectionEnd();
                    if (start == end) {

                        int position = start;
                        if (position == 0) {
                            if (isPreviousParagraph) {
                                result = insert(index, null);
                                if (result == null) {
                                    result = entity;
                                }
                            }
                        } else if (position == focus.length()) {
                            boolean isNextParagraph = (index + 1 >= document.size()) ? false : (document.get(index + 1).getClass() == ParagraphEntity.class);
                            if (isNextParagraph) {
                                ParagraphEntity next = (ParagraphEntity) (document.get(index + 1));

                                result = insert(index + 1, null);
                                if (result == null) {
                                    result = next;
                                }
                            }
                        }
                    }
                }

            }

            if (result == null) {
                CharSequence[] array = focus.split();
                if (array != null && array.length == 2) {
                    int position = document.indexOf(focus.getEntity());
                    if (position >= 0) {
                        result = insert(position + 1, array[1]);
                        if (result != null) {
                            focus.setText(array[0]);
                            focus.setSelection(focus.length());
                        }
                    }
                }
            }
        }

        // if current focus is not Paragraph, we just append at last
        if (focus == null) {
            result = insert(document.size(), "");
        }

        // move to new position
        if ((result != null) && (result != current)) {
            final int index = document.indexOf(result);
            if (index >= 0) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        int position = index;
                        recyclerView.smoothScrollToPosition(position);

                        RecyclerView.ViewHolder h = recyclerView.findViewHolderForAdapterPosition(position);
                        if (h != null && h instanceof ParagraphViewHolder) {
                            ParagraphViewHolder holder = (ParagraphViewHolder)h;
                            holder.setSelection(0);
                            holder.requestFocus();
                        }
                    }
                });
            }
        }

        // save document
        if (result != null) {
            document.save();
        }
    }
}

class ParagraphInsert extends BaseInsert {

    ParagraphInsert(ComposeArticleFragment f) {
        super(f);

        this.alwaysSplit = true;
    }

    @Override
    ParagraphEntity insert(int position, CharSequence text) {
        ParagraphEntity entity;

        int index = position;
        {
            CharSequence s = (text == null)? "": text;
            entity = ParagraphEntity.create(document, s);

            document.add(index, entity);
            ++index;
        }

        int count = (index - position);
        if (count > 0) {
            adapter.notifyItemRangeInserted(position, count);
        }

        return entity;
    }
}

/**
 *
 */
class PictureInsert extends BaseInsert {

    List<Uri> list;

    PictureInsert(ComposeArticleFragment f, List<Uri> list) {
        super(f);

        this.list = list;
    }

    @Override
    ParagraphEntity insert(int position, CharSequence text) {
        ParagraphEntity entity = null;

        int index = position;
        for (int i = 0, size = list.size(); i < size; i++) {
            Uri uri = list.get(i);

            PictureEntity pic = PictureEntity.create(document, uri);
            if (pic != null) {
                document.add(index, pic);
                ++index;

                CharSequence s = ((i + 1) == size)? text: "";
                if (text != null) {
                    ParagraphEntity en = ParagraphEntity.create(document, s);

                    document.add(index, en);
                    ++index;

                    entity = en;
                }
            }
        }

        int count = (index - position);
        if (count > 0) {
            adapter.notifyItemRangeInserted(position, count);
        }

        return entity;
    }
}

class InsertFactory {

    static final ParagraphInsert createParagraph(ComposeArticleFragment f) {
        return new ParagraphInsert(f);
    }

    static final PictureInsert createPicture(ComposeArticleFragment f, List<Uri> list) {
        return new PictureInsert(f, list);
    }
}
