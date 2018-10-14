package club.andnext.recyclerview.itemtouch;

import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import club.andnext.recyclerview.R;
import club.andnext.recyclerview.overscroll.OverScrollHelper;

public class ItemSwipeHelper {

    boolean enable;
    int swipeFlags;

    boolean alphaEnable;

    ItemTouchHelper itemTouchHelper;
    ItemSwipeDelegate itemSwipeDelegate;

    OverScrollHelper overScrollHelper;

    public ItemSwipeHelper() {
        this(null);
    }

    public ItemSwipeHelper(OverScrollHelper overScrollHelper) {
        this.overScrollHelper = overScrollHelper;

        this.enable = true;
        this.swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        this.alphaEnable = true;

        this.itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        this.itemSwipeDelegate = new ItemSwipeDelegate(this);
    }


    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void startSwipe(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startSwipe(viewHolder);
    }

    public void setEnable(boolean value) {
        this.enable = value;
    }

    public void setAlphaEnable(boolean value) {
        this.alphaEnable = value;
    }

    public void setLeftEnable(boolean value) {
        if (value) {
            this.swipeFlags |= ItemTouchHelper.LEFT;
        } else {
            this.swipeFlags &= (~ItemTouchHelper.LEFT);
        }
    }

    public void setRightEnable(boolean value) {
        if (value) {
            this.swipeFlags |= ItemTouchHelper.RIGHT;
        } else {
            this.swipeFlags &= (~ItemTouchHelper.RIGHT);
        }
    }

    private ItemTouchHelper.Callback itemTouchCallback = new ItemTouchHelper.Callback() {

        RecyclerView.ViewHolder selected = null;

        @Override
        public boolean isItemViewSwipeEnabled() {
            return enable;
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return super.getSwipeThreshold(viewHolder);
        }

        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            return super.getSwipeEscapeVelocity(defaultValue);
        }

        @Override
        public float getSwipeVelocityThreshold(float defaultValue) {
            return super.getSwipeVelocityThreshold(defaultValue);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            {
                View view = viewHolder.itemView;

                final Object tag = view.getTag(R.id.anc_item_swipe_previous_alpha);
                if (tag != null && tag instanceof Float) {
                    view.setAlpha((Float)tag);
                }
                view.setTag(R.id.anc_item_swipe_previous_alpha, null);
            }

            {

            }
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (!enable) {
                return 0;
            }

            {
                if (!itemSwipeDelegate.isEnable(viewHolder)) {
                    return 0;
                }
            }

            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);

            if (overScrollHelper != null) {
                if (viewHolder == null) {
                    overScrollHelper.attach();
                } else {
                    overScrollHelper.detach();
                }
            }

            {

            }

            this.selected = viewHolder;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            if (isCurrentlyActive) {
                View view = viewHolder.itemView;

                Object originalAlpha = view.getTag(R.id.anc_item_swipe_previous_alpha);
                if (originalAlpha == null) {
                    originalAlpha = view.getAlpha();

                    view.setTag(R.id.anc_item_swipe_previous_alpha, originalAlpha);
                }
            }

            if (alphaEnable) {
                View view = viewHolder.itemView;
                Object originalAlpha = view.getTag(R.id.anc_item_swipe_previous_alpha);
                if (originalAlpha != null) {
                    float alpha = (Float)originalAlpha;

                    float max = this.getSwipeThreshold(viewHolder) * view.getWidth();
                    if (Math.abs(dX) > max) {
                        float value = Math.abs(dX) - max;
                        if (view.getWidth() > max) {

                            value /= (view.getWidth() - max);
                            value = (1 - value);
                            value = (value < 0) ? 0 : value;

                        } else {
                            value = 0;
                        }

                        alpha *= value;
                    }

                    view.setAlpha(alpha);
                }
            }
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            {
                itemSwipeDelegate.onSwiped(viewHolder, direction);
            }
        }

    };

    /**
     *
     */
    private static class ItemSwipeDelegate {

        ItemSwipeHelper helper;

        ItemSwipeDelegate(ItemSwipeHelper helper) {
            this.helper = helper;
        }

        public boolean isEnable(RecyclerView.ViewHolder viewHolder) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                return adapter.isEnable(helper);
            }

            return false;
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onSwiped(helper, direction);
            }
        }

        Adapter getAdapter(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof Adapter) {
                return (Adapter)viewHolder;
            }

            return null;
        }

    }

    /**
     *
     */
    public interface Adapter {

        boolean isEnable(ItemSwipeHelper delegate);

        void onSwiped(ItemSwipeHelper delegate, int direction);
    }
}
