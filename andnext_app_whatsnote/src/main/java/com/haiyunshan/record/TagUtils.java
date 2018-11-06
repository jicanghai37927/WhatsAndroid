package com.haiyunshan.record;

import android.content.Context;
import android.graphics.Color;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.WhatsApp;

public class TagUtils {

    static Integer replaceColor;

    public static final boolean exist(String name) {
        RecordManager mgr = RecordManager.getInstance();

        TagDataset ds = mgr.getTagDataset();
        for (int i = 0, size = ds.size(); i < size; i++) {
            if (ds.get(i).getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static final int getDisplayColor(int color) {
        if (color != Color.TRANSPARENT) {
            return color;
        }

        return getReplaceColor();
    }

    public static final int getReplaceColor() {
        if (replaceColor != null) {
            return replaceColor;
        }

        Context context = WhatsApp.getContext();
        replaceColor = context.getResources().getColor(R.color.anc_replace_circle_color);
        return replaceColor;
    }

}
