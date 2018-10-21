package me.everything.android.ui.overscroll;

import android.view.View;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import me.everything.android.ui.overscroll.adapters.*;

/**
 * @author amit
 */
public class OverScrollDecoratorHelper {

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    /**
     * Set up the over-scroll effect over a specified {@link RecyclerView} view.
     * <br/>Only recycler-views using <b>native</b> Android layout managers (i.e. {@link LinearLayoutManager},
     * {@link GridLayoutManager} and {@link StaggeredGridLayoutManager}) are currently supported
     * by this convenience method.
     *
     * @param recyclerView The view.
     * @param orientation Either {@link #ORIENTATION_HORIZONTAL} or {@link #ORIENTATION_VERTICAL}.
     *
     * @return The over-scroll effect 'decorator', enabling further effect configuration.
     */
    public static IOverScrollDecor setUpOverScroll(RecyclerView recyclerView, int orientation) {
        IOverScrollDecor decor = null;

        switch (orientation) {
            case ORIENTATION_HORIZONTAL: {
                decor = new HorizontalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerView));
                break;
            }
            case ORIENTATION_VERTICAL: {
                decor = new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerView));
                break;
            }

            default:
                throw new IllegalArgumentException("orientation");
        }

        if (decor != null) {
            decor.attach();
        }

        return decor;
    }

    public static IOverScrollDecor setUpOverScroll(ListView listView) {
        IOverScrollDecor decor = new VerticalOverScrollBounceEffectDecorator(new AbsListViewOverScrollDecorAdapter(listView));
        decor.attach();

        return decor;
    }

    public static IOverScrollDecor setUpOverScroll(GridView gridView) {
        IOverScrollDecor decor = new VerticalOverScrollBounceEffectDecorator(new AbsListViewOverScrollDecorAdapter(gridView));
        decor.attach();

        return decor;
    }

    public static IOverScrollDecor setUpOverScroll(ScrollView scrollView) {
        IOverScrollDecor decor = new VerticalOverScrollBounceEffectDecorator(new ScrollViewOverScrollDecorAdapter(scrollView));
        decor.attach();

        return decor;
    }

    public static IOverScrollDecor setUpOverScroll(NestedScrollView scrollView) {
        IOverScrollDecor decor = new VerticalOverScrollBounceEffectDecorator(new NestedScrollViewOverScrollDecorAdapter(scrollView));
        decor.attach();

        return decor;
    }

    public static IOverScrollDecor setUpOverScroll(HorizontalScrollView scrollView) {
        IOverScrollDecor decor = new HorizontalOverScrollBounceEffectDecorator(new HorizontalScrollViewOverScrollDecorAdapter(scrollView));
        decor.attach();

        return decor;
    }

    /**
     * Set up the over-scroll over a generic view, assumed to always be over-scroll ready (e.g.
     * a plain text field, image view).
     *
     * @param view The view.
     * @param orientation One of {@link #ORIENTATION_HORIZONTAL} or {@link #ORIENTATION_VERTICAL}.
     *
     * @return The over-scroll effect 'decorator', enabling further effect configuration.
     */
    public static IOverScrollDecor setUpStaticOverScroll(View view, int orientation) {
        IOverScrollDecor decor = null;

        switch (orientation) {
            case ORIENTATION_HORIZONTAL: {
                decor = new HorizontalOverScrollBounceEffectDecorator(new StaticOverScrollDecorAdapter(view));
                break;
            }
            case ORIENTATION_VERTICAL: {
                decor = new VerticalOverScrollBounceEffectDecorator(new StaticOverScrollDecorAdapter(view));
                break;
            }
            default:
                throw new IllegalArgumentException("orientation");
        }

        if (decor != null) {
            decor.attach();
        }

        return decor;
    }

    public static IOverScrollDecor setUpOverScroll(ViewPager viewPager) {
        IOverScrollDecor decor = new HorizontalOverScrollBounceEffectDecorator(new ViewPagerOverScrollDecorAdapter(viewPager));
        decor.attach();

        return decor;
    }

}
