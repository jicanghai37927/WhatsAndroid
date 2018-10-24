package club.andnext.recyclerview.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class MarginDividerDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{ android.R.attr.listDivider };

    Drawable background;
    Drawable divider;

    Rect bounds;
    int margin;

    boolean drawOver;
    boolean drawTop;
    boolean drawBottom;

    RecyclerView.ViewHolder dragViewHolder;

    public MarginDividerDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        this.divider = a.getDrawable(0);
        a.recycle();

        this.bounds = new Rect();
        this.margin = 0;

        this.drawOver = true;
        this.drawTop = true;
        this.drawBottom = true;

    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public void setDivider(Drawable d) {
        this.divider = d;
    }

    public void setMargin(int value) {
        this.margin = value;
    }

    public void setDrawOver(boolean value) {
        this.drawOver = value;
    }

    public void setDrawTop(boolean value) {
        this.drawTop = value;
    }

    public void setDrawBottom(boolean value) {
        this.drawBottom = value;
    }

    public void setDragViewHolder(RecyclerView.ViewHolder holder) {
        this.dragViewHolder = holder;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        if (!drawOver) {
            drawVertical(c, parent, state);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (drawOver) {
            drawVertical(c, parent, state);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (divider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        if (drawOver) {
            outRect.set(0, 0, 0, 0);
        } else {
            if (parent.findContainingViewHolder(view).getAdapterPosition() == 0 && this.drawTop) {
                outRect.set(0, divider.getIntrinsicHeight(), 0, divider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, 0, divider.getIntrinsicHeight());
            }
        }
    }

    void drawVertical(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

        if (parent.getLayoutManager() == null || divider == null) {
            return;
        }

        canvas.save();

        final int itemCount = parent.getAdapter().getItemCount();
        final int left;
        final int right;

        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.ViewHolder h = parent.findContainingViewHolder(child);

            // in drag, don't draw divider
            if (h == this.dragViewHolder) {
                continue;
            }

            parent.getDecoratedBoundsWithMargins(child, bounds);
            final int bottom = bounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - divider.getIntrinsicHeight();

            int pos = parent.getChildAdapterPosition(child);

            // 列表最顶部
            if (pos == 0 && drawTop) {
                divider.setBounds(left, bounds.top, right, bounds.top + divider.getIntrinsicHeight());
                divider.draw(canvas);
            }

            // 列表最底部
            if ((pos + 1) == itemCount && !drawBottom) {
                continue;
            }

            // background
            if (background != null) {
                background.setBounds(left, top, right, bottom);
                background.draw(canvas);
            }

            // divider
            {
                boolean isLast = (pos + 1 == itemCount);

                int offset = (!isLast)? (int)this.getTranslation(h): 0;

                int x = left;
                x += (!isLast)? this.getMargin(h): 0; // 最后一个限定为没有margin

                divider.setBounds(x + offset, top, right + offset, bottom);
                divider.draw(canvas);
            }

        }

        canvas.restore();
    }

    int getMargin(RecyclerView.ViewHolder holder) {
        return this.margin;
    }

    float getTranslation(RecyclerView.ViewHolder holder) {
        if (holder.getAdapterPosition() < 0) {
            return 0;
        }

        if (holder instanceof Adapter) {
            return ((Adapter)holder).getTranslation(this);
        }

        return 0;
    }

    /**
     *
     */
    public interface Adapter {

        float getTranslation(MarginDividerDecoration decoration);

    }
}
