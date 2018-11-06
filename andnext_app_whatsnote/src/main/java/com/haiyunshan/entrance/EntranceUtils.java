package com.haiyunshan.entrance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.haiyunshan.record.RecordEntity;
import com.haiyunshan.whatsnote.PackActivity;
import com.haiyunshan.whatsnote.ShowRecordActivity;
import com.haiyunshan.whatsnote.record.RecentRecordFragment;

public class EntranceUtils {

    public static final void enter(Fragment fragment, EntranceEntity entity) {
        Activity context = fragment.getActivity();

        String id = entity.getId();
        if (TextUtils.isEmpty(id)) {
            return;
        }

        if (id.equals(EntranceEntry.ID_NOTE)) {
            showNote(context);
        } else if (id.equals(EntranceEntry.ID_EXTRACT)) {
            showExtract(context);
        } else if (id.equals(EntranceEntry.ID_RECENT)) {
            showRecent(context, "");
        } else if (id.equals(EntranceEntry.ID_PREVIEW)) {

        } else if (id.equals(EntranceEntry.ID_TRASH)) {
            showTrash(context);
        }
    }

    static void showNote(Activity context) {
        ShowRecordActivity.start(context, RecordEntity.ROOT_NOTE);
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
