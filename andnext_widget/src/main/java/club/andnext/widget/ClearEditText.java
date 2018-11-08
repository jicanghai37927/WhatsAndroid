package club.andnext.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * 可清除编辑框
 *
 */
public class ClearEditText extends AppCompatEditText {

	private boolean mIsEmpty = true;
	
	private Drawable mDrawableLeft;
	private Drawable mDrawableRight;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /* 获取左右Drawable，并只显示left drawable */
        this.mDrawableLeft = getCompoundDrawables()[0];
        this.mDrawableRight = getCompoundDrawables()[2];
        if (mDrawableRight == null) {
            Drawable right = getResources().getDrawable(R.drawable.anc_ic_clear, null);
            right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());

            this.mDrawableRight = right;
        }

        if (this.mDrawableRight != null) {
            setCompoundDrawables(mDrawableLeft, null, null, null);
        }

    }

    @Override
	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
		
		if ((this.mDrawableRight != null) && (paramMotionEvent.getAction() == 1)) {
			
//			int paddingRight = getPaddingRight(); 
			int comPaddingRight = getCompoundPaddingRight(); 
//			int drawableWidth = mDrawableRight.getBounds().targetWidth();
			
			float f1 = paramMotionEvent.getX();
			float f2 = getWidth() - comPaddingRight;
			if (f1 > f2) { // 判断是否点击右drawable
				setText(null);
			}
		}
		
		return super.dispatchTouchEvent(paramMotionEvent);
	}

	@Override
	public boolean onPreDraw() {

		boolean isEmpty = TextUtils.isEmpty(getText());
		
		if (this.mIsEmpty != isEmpty) { // 防止重复设置
			this.mIsEmpty = isEmpty;
			
			if (this.mIsEmpty) {
				Drawable localDrawable1 = this.mDrawableLeft;
				setCompoundDrawables(localDrawable1, null, null, null);
			} else {
				setCompoundDrawables(this.mDrawableLeft, null, this.mDrawableRight, null);
			}
		}

		return super.onPreDraw(); 
	}
}
