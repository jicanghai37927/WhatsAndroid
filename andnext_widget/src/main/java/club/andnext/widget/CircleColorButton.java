package club.andnext.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CircleColorButton extends FrameLayout implements Checkable {

    ImageView colorView;
    ImageView checkView;

    int replaceColor;   // color for transparent
    int color;

    public CircleColorButton(@NonNull Context context) {
        this(context, null);
    }

    public CircleColorButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleColorButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CircleColorButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        {
            int resource = R.layout.anc_merge_circle_color_button;
            LayoutInflater.from(context).inflate(resource, this, true);
        }

        {
            this.replaceColor = getResources().getColor(R.color.anc_replace_circle_color);
            this.setReplaceColor(replaceColor);
        }

        {
            this.colorView = findViewById(R.id.anc_iv_color);
            this.checkView = findViewById(R.id.anc_iv_check);
        }

        {
            checkView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = colorView.getMeasuredWidth();
        int height = colorView.getMeasuredHeight();
        if (width > 0 && height > 0 && width != height) {
            int size = (width > height)? height: width;
            width = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            height = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);

            colorView.measure(width, height);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        checkView.setVisibility(checked? View.VISIBLE: View.INVISIBLE);
    }

    @Override
    public boolean isChecked() {
        return (checkView.getVisibility() == View.VISIBLE);
    }

    @Override
    public void toggle() {
        this.setChecked(!isChecked());
    }

    public void setReplaceColor(int color) {
        this.replaceColor = color;
    }

    public int getReplaceColor() {
        return this.replaceColor;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;

        if (color == Color.TRANSPARENT) {

            int c = this.getReplaceColor();

            int resId = R.drawable.anc_ic_circle_color_stroke;
            colorView.setImageResource(resId);
            colorView.setImageTintList(ColorStateList.valueOf(c));

            checkView.setImageTintList(ColorStateList.valueOf(c));

        } else {

            int c = color;

            int resId = R.drawable.anc_ic_circle_color_solid;
            colorView.setImageResource(resId);
            colorView.setImageTintList(ColorStateList.valueOf(c));

            checkView.setImageTintList(null);
        }

    }

}
