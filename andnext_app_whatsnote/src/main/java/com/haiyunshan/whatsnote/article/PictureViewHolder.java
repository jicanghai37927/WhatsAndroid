package com.haiyunshan.whatsnote.article;

import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import club.andnext.helper.ClearAssistMenuHelper;
import club.andnext.widget.TargetSizeImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.haiyunshan.article.PictureEntity;
import com.haiyunshan.whatsnote.R;

/**
 *
 */
public class PictureViewHolder extends ComposeViewHolder<PictureEntity> implements View.OnClickListener {

    public static final int LAYOUT_RES_ID = R.layout.layout_compose_picture_list_item;

    View removeBtn;

    TargetSizeImageView pictureView;
    EditText editText;

    public PictureViewHolder(ComposeArticleFragment f, View itemView) {
        super(f, itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        this.removeBtn = view.findViewById(R.id.iv_remove);
        removeBtn.setOnClickListener(this);

        this.pictureView = view.findViewById(R.id.iv_picture);

        this.editText = view.findViewById(R.id.edit_text);
        ClearAssistMenuHelper.attach(editText);
    }

    @Override
    public void onBind(PictureEntity item, int position) {
        super.onBind(item, position);

        {
            editText.setText(item.getText());

        }

        {
            int maxWidth = this.getMaxWidth();
            int width = item.getWidth();
            int height = item.getHeight();
            if (width > maxWidth / 3) {
                width = maxWidth;
                height = width * item.getHeight() / item.getWidth();
            }

            pictureView.setTargetSize(width, height);
        }

        {
            RequestOptions options = createRequestOptions(item);
            Glide.with(parent)
                    .load(item.getUri())
                    .apply(options)
                    .into(pictureView);
        }
    }

    @Override
    void save() {
        entity.setText(editText.getText());
    }

    RequestOptions createRequestOptions(PictureEntity entity) {
        RequestOptions options = new RequestOptions();

        int maxWidth = this.getMaxWidth();
        int maxHeight = 3 * this.getMaxHeight();

        int width = maxWidth;
        int height = width * entity.getHeight() / entity.getWidth();
        if (height > maxHeight) {
            height = maxHeight;
            width = height * entity.getWidth() / entity.getHeight();

            width = (width > maxWidth)? maxWidth: width;
        }

        options.override(width, height);
        options.signature(new ObjectKey(entity.getSignature()));

        return options;
    }

    int getMaxWidth() {
        int width = parent.recyclerView.getWidth();
        if (width > 0) {
            width -= parent.recyclerView.getPaddingLeft();
            width -= parent.recyclerView.getPaddingRight();
        }

        if (width <= 0) {
            width = parent.getResources().getDisplayMetrics().widthPixels;
        }

        return width;
    }

    int getMaxHeight() {
        int width = parent.recyclerView.getHeight();

        if (width <= 0) {
            width = parent.getResources().getDisplayMetrics().widthPixels;
        }

        return width;
    }

    @Override
    public void onClick(View v) {
        if (v == removeBtn) {
            this.remove();
        }
    }
}
