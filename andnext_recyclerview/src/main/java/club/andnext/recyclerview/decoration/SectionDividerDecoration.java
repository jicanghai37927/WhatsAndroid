package club.andnext.recyclerview.decoration;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

public class SectionDividerDecoration extends MarginDividerDecoration {

    public SectionDividerDecoration(Context context) {
        super(context);
    }

    @Override
    int getMargin(RecyclerView.ViewHolder holder) {
        if (holder instanceof Adapter) {
            return ((Adapter)holder).getMargin(this);
        }

        return super.getMargin(holder);
    }

    /**
     *
     */
    public interface Adapter {

        int getMargin(SectionDividerDecoration decoration);

    }
}
