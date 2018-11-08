package club.andnext.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * auto adjust view size by target size.
 * keep with and scale height
 *
 */
public class TargetSizeImageView extends AppCompatImageView {

    static final String TAG = TargetSizeImageView.class.getSimpleName();

    int targetWidth;    // bitmap width
    int targetHeight;   // bitmap height

    public TargetSizeImageView(Context context) {
        super(context, null);
    }

    public TargetSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public TargetSizeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (targetWidth <= 0 || targetHeight <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            return;
        }

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (measureWidth <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        Log.v(TAG, "onMeasure width = " + measureWidth + ", height = " + measureHeight);

        int minWidth = this.getSuggestedMinimumWidth();
        int minHeight = this.getSuggestedMinimumHeight();

        int width = measureWidth;
        width = (width > targetWidth)? targetWidth : width; // with must not large then targetWidth
        width = (width < minWidth)? minWidth: width; // and not smaller then minWidth

        int height = width * targetHeight / targetWidth; // calculate height from width
        if (height < minHeight) { // recalculate width
            height = minHeight;
            width = height * targetWidth / targetHeight;

            width = (width > measureWidth)? measureWidth: width;
            width = (width > targetWidth)? targetWidth : width; // with must not large then targetWidth
            width = (width < minWidth)? minWidth: width; // and not smaller then minWidth
        }

        Log.v(TAG, "width = " + width + ", height = " + height);
        this.setMeasuredDimension(width, height);
    }

    public void setTargetSize(int width, int height) {
        if ((this.targetWidth == width) && (this.targetHeight == height)) {
            return;
        }

        this.targetWidth = width;
        this.targetHeight = height;

        this.requestLayout();
    }
}
