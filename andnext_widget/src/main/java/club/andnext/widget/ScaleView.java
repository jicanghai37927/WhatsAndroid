package club.andnext.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 对子控件进行Scale
 *
 */
public class ScaleView extends FrameLayout {

    public ScaleView(@NonNull Context context) {
        this(context, null);
    }

    public ScaleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ScaleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        {

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() != 1) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            return;
        }

        View child = this.getChildAt(0);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)(child.getLayoutParams());
        params.gravity = Gravity.CENTER;

        int childWidth = getDisplayWidth(getContext()); // 子控件高度为全屏
        int childHeight = 0;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (this.getLayoutParams().height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            if (height > 0 && width > 0) { // 确定父控件的高度，则设置子控件的最小高度
                childHeight = (int) (1.f * height * childWidth / width);
            }
        }

        // 计算子控件尺寸
        child.setMinimumHeight(childHeight);
        child.measure(
                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        childWidth = child.getMeasuredWidth();
        childHeight = child.getMeasuredHeight();

        // 设置缩放
        float scale = 1.f * width / childWidth;
        child.setScaleX(scale);
        child.setScaleY(scale);

        // 设置大小
        double value = 1.0f * childHeight * width / childWidth;
        height = (int)(Math.ceil(value));
        this.setMeasuredDimension(width, height);
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScaleLayout can host only one direct child");
        }

        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScaleLayout can host only one direct child");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScaleLayout can host only one direct child");
        }

        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScaleLayout can host only one direct child");
        }

        super.addView(child, index, params);
    }

    private static final int getDisplayWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }
}
