package com.haiyunshan.whatsnote.entrance.entity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import com.haiyunshan.whatsnote.PackActivity;
import com.haiyunshan.whatsnote.ShowRecordActivity;
import com.haiyunshan.whatsnote.record.RecentRecordFragment;
import com.haiyunshan.whatsnote.record.TagRecordFragment;
import com.haiyunshan.whatsnote.entrance.dataset.EntranceEntry;
import com.haiyunshan.whatsnote.record.entity.FavoriteEntity;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;
import com.haiyunshan.whatsnote.record.entity.TagEntity;

public class EntranceUtils {

    public static final void enter(Fragment fragment, EntranceEntity entity) {
        Activity context = fragment.getActivity();

        String id = entity.getId();
        if (TextUtils.isEmpty(id)) {
            return;
        }

        if (id.equals(EntranceEntry.ID_NOTE)) {
            showNote(context, RecordEntity.ROOT_NOTE);
        } else if (id.equals(EntranceEntry.ID_EXTRACT)) {
            showExtract(context);
        } else if (id.equals(EntranceEntry.ID_RECENT)) {
            showRecent(context, "");
        } else if (id.equals(EntranceEntry.ID_PREVIEW)) {

        } else if (id.equals(EntranceEntry.ID_TRASH)) {
            showTrash(context);
        }
    }

    public static final void enter(Fragment fragment, FavoriteEntity entity) {
        String id = entity.getId();
        if (TextUtils.isEmpty(id)) {
            return;
        }

        Activity context = fragment.getActivity();
        RecordEntity en = RecordEntity.create(id, RecordEntity.TYPE_EMPTY);
        if (en.isDirectory()) {
            showNote(context, en.getId());
        }
    }

    public static final void enter(Fragment fragment, TagEntity entity) {
        Activity context = fragment.getActivity();

        Intent intent = new Intent(context, PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, TagRecordFragment.class.getName());

        intent.putExtra(TagRecordFragment.KEY_TAG, entity.getId());

        context.startActivity(intent);
    }

    static void showNote(Activity context, String parent) {
        ShowRecordActivity.start(context, parent);
    }

    static void showExtract(Activity context) {
        ShowRecordActivity.start(context, RecordEntity.ROOT_EXTRACT);
    }

    static void showTrash(Activity context) {
        ShowRecordActivity.start(context, RecordEntity.ROOT_TRASH);
    }

    static void showRecent(Activity context, String tag) {
        Intent intent = new Intent(context, PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, RecentRecordFragment.class.getName());

        intent.putExtra(RecentRecordFragment.KEY_TAG, tag);

        context.startActivity(intent);
    }
}
