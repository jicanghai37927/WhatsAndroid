package club.andnext.recyclerview.itemtouch;

import android.graphics.Canvas;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import club.andnext.recyclerview.R;
import club.andnext.overscroll.OverScrollHelper;

public class ItemDragHelper {

    boolean enable;
    int dragFlags;
    boolean longPressDragEnable;

    float elevation;

    ItemTouchHelper itemTouchHelper;
    ItemDragDelegate itemDragDelegate;

    OverScrollHelper overScrollHelper;

    public ItemDragHelper() {
        this(null);
    }

    public ItemDragHelper(OverScrollHelper overScrollHelper) {
        this.overScrollHelper = overScrollHelper;

        this.enable = true;
        this.dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        this.longPressDragEnable = false;

        this.elevation = 6.f;

        this.itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        this.itemDragDelegate = new ItemDragDelegate(this);
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    public void setEnable(boolean value) {
        this.enable = value;
    }

    public void setUpEnable(boolean value) {
        if (value) {
            this.dragFlags |= ItemTouchHelper.UP;
        } else {
            this.dragFlags &= (~ItemTouchHelper.UP);
        }
    }

    public void setDownEnable(boolean value) {
        if (value) {
            this.dragFlags |= ItemTouchHelper.DOWN;
        } else {
            this.dragFlags &= (~ItemTouchHelper.DOWN);
        }
    }

    public void setLongPressDragEnable(boolean value) {
        this.longPressDragEnable = value;
    }

    public void setElevation(float value) {
        this.elevation = value;
    }

    private float findMaxElevation(RecyclerView recyclerView, View itemView) {
        final int childCount = recyclerView.getChildCount();
        float max = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = recyclerView.getChildAt(i);
            if (child == itemView) {
                continue;
            }
            final float elevation = ViewCompat.getElevation(child);
            if (elevation > max) {
                max = elevation;
            }
        }
        return max;
    }

    private ItemTouchHelper.Callback itemTouchCallback = new ItemTouchHelper.Callback() {

        @Override
        public boolean isLongPressDragEnabled() {
            if (!enable) {
                return false;
            }

            return longPressDragEnable;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
            return super.getMoveThreshold(viewHolder);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            // order attention
            {
                View view = viewHolder.itemView;

                final Object tag = view.getTag(R.id.anc_item_drag_previous_elevation);
                if (tag != null && tag instanceof Float) {
                    ViewCompat.setElevation(view, (Float) tag);
                }

                view.setTag(R.id.anc_item_drag_previous_elevation, null);
            }

            {
                super.clearView(recyclerView, viewHolder);
            }

            {
                itemDragDelegate.onEnd(viewHolder);
            }
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (!enable) {
                return 0;
            }

            {
                if (!itemDragDelegate.isEnable(viewHolder)) {
                    return 0;
                }
            }

            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            return super.canDropOver(recyclerView, current, target);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {

            return itemDragDelegate.onMove(viewHolder, target);

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {

            // order attention
            {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            if (isCurrentlyActive) {
                View view = viewHolder.itemView;

                Object originalElevation = view.getTag(R.id.anc_item_drag_previous_elevation);
                if (originalElevation == null) {
                    originalElevation = ViewCompat.getElevation(view);

                    float newElevation = elevation + findMaxElevation(recyclerView, view);
                    ViewCompat.setElevation(view, newElevation);

                    view.setTag(R.id.anc_item_drag_previous_elevation, originalElevation);
                }
            }


        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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

            if (viewHolder != null) {
                itemDragDelegate.onBegin(viewHolder);
            }
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

    };

    /**
     *
     */
    private static class ItemDragDelegate {

        ItemDragHelper helper;

        ItemDragDelegate(ItemDragHelper helper) {
            this.helper = helper;
        }

        public boolean isEnable(RecyclerView.ViewHolder viewHolder) {
            Adapter adapter = getAdapter(viewHolder);
            if (adapter != null) {
                return adapter.isEnable(helper);
            }

            return false;
        }

        public void onBegin(RecyclerView.ViewHolder viewHolder) {
            Adapter adapter = getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onBegin(helper);
            }

        }

        public boolean onMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Adapter adapter = getAdapter(viewHolder);
            if (adapter != null) {
                return adapter.onMove(helper, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            }

            return false;
        }

        public void onEnd(RecyclerView.ViewHolder viewHolder) {
            Adapter adapter = getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onEnd(helper);
            }
        }

        Adapter getAdapter(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof Adapter) {
                return (Adapter) viewHolder;
            }

            return null;
        }

    }

    /**
     *
     */
    public interface Adapter {

        boolean isEnable(ItemDragHelper helper);

        void onBegin(ItemDragHelper helper);

        boolean onMove(ItemDragHelper helper, int from, int to);

        void onEnd(ItemDragHelper helper);
    }
}
