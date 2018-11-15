package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.graphics.Color;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.record.dataset.TagDataset;

public class TagUtils {

    static Integer replaceColor;

    public static final boolean exist(Context context, String name) {
        RecordManager mgr = RecordManager.getInstance(context);

        TagDataset ds = mgr.getTagDataset();
        for (int i = 0, size = ds.size(); i < size; i++) {
            if (ds.get(i).getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static final int getDisplayColor(Context context, int color) {
        if (color != Color.TRANSPARENT) {
            return color;
        }

        return getReplaceColor(context);
    }

    public static final int getReplaceColor(Context context) {
        if (replaceColor != null) {
            return replaceColor;
        }

        replaceColor = context.getResources().getColor(R.color.anc_replace_circle_color);
        return replaceColor;
    }

}
