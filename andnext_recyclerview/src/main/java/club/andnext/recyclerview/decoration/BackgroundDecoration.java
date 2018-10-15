package club.andnext.recyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class BackgroundDecoration extends RecyclerView.ItemDecoration {

    Drawable background;

    int offset;
    int range;

    public BackgroundDecoration(RecyclerView view, Drawable d) {
        this.background = d;

        view.addOnScrollListener(scrollListener);
    }

    public void setBackground(Drawable d) {
        this.background = d;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);

        if (this.background == null) {
            return;
        }

        canvas.save();
        canvas.translate(0, -offset);

        background.draw(canvas);

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (this.background == null) {
            return;
        }

        this.updateOffsetAndRange(parent);
    }

    void updateOffsetAndRange(RecyclerView parent) {

        this.offset = parent.computeVerticalScrollOffset();
        this.range = parent.computeVerticalScrollRange();

        if (parent.getChildCount() != 0) {
            int index = parent.getChildCount() - 1;
            View v = parent.getChildAt(index);
            int bottom = v.getBottom();

            bottom += offset;
            bottom += parent.getPaddingBottom();

            range = (range < bottom)? bottom: range;
        }

        range = (range < parent.getHeight())? parent.getHeight(): range;

        int width = parent.getWidth();
        int height = background.getBounds().height();
        if (height != range) {
            background.setBounds(0, 0, width, range);
        }
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            updateOffsetAndRange(recyclerView);
        }
    };
}
