package com.haiyunshan.whatsnote.article;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import club.andnext.helper.ClearAssistMenuHelper;
import club.andnext.widget.TargetSizeImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.haiyunshan.whatsnote.article.entity.PictureEntity;
import com.haiyunshan.whatsnote.R;

/**
 *
 */
public class PictureViewHolder extends ComposeViewHolder<PictureEntity> implements View.OnClickListener {

    public static final int LAYOUT_RES_ID = R.layout.layout_compose_picture_list_item;

    static final String TAG = PictureViewHolder.class.getSimpleName();

    View removeBtn;

    TargetSizeImageView pictureView;
    EditText editText;

    PictureListener pictureListener;

    public PictureViewHolder(Callback callback, View itemView) {
        super(callback, itemView);

        this.pictureListener = new PictureListener();
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
            // set break strategy to request layout
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editText.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
            }

            editText.setText(item.getText());
        }

        {
            int maxWidth = callback.getMaxWidth();
            int width = item.getWidth();
            int height = item.getHeight();
            if (width > maxWidth / 2) {
                width = maxWidth;
                height = width * item.getHeight() / item.getWidth();
            }

            pictureView.setTargetSize(width, height);
        }

        {
            RequestOptions options = createRequestOptions(item);

            Glide.with(callback.getContext())
                    .load(item.getUri())
                    .apply(options)
                    .listener(pictureListener)
                    .into(pictureView);
        }

        {
            Log.v(TAG, "uri = " + item.getUri() + ", width = " + item.getWidth() + ", height = " + item.getHeight());
        }
    }

    @Override
    void save() {
        entity.setText(editText.getText());
    }

    @Override
    public void onClick(View v) {
        if (v == removeBtn) {
            this.remove();
        }
    }

    RequestOptions createRequestOptions(PictureEntity entity) {
        RequestOptions options = new RequestOptions();

        // picture size
        int width = entity.getWidth();
        int height = entity.getHeight();

        // sample it for 1, 2, 4, 8, ...
        {
            int maxWidth = callback.getMaxWidth();
            int maxHeight = callback.getMaxHeight();

            int widthSampleFactor = getSampleFactor(width, maxWidth);
            int heightSampleFactor = getSampleFactor(height, 6 * maxHeight);

            int sampleFactor = Math.max(widthSampleFactor, heightSampleFactor);
            sampleFactor = Math.max(1, Integer.highestOneBit(sampleFactor));

            if (sampleFactor > 1) {
                width /= sampleFactor;
                height /= sampleFactor;
            }
        }

        {
            options.dontTransform();
            options.override(width, height);
            options.downsample(DownsampleStrategy.FIT_CENTER);
            options.signature(new ObjectKey(entity.getSignature()));
        }

        return options;
    }

    int getSampleFactor(int source, int target) {
        int scale = 1;

        while ((source / scale) > target) {
            scale *= 2;
        }

        return scale;
    }

    /**
     *
     */
    private class PictureListener implements RequestListener<Drawable> {

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            if (resource != null && resource instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                if (bitmap != null) {
                    Log.v(TAG, "bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight());
                }
            }

            return false;
        }
    }
}
