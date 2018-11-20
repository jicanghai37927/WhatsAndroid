package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.WhatsApp;

public class SearchRecordSet extends BaseEntitySet<RecordEntity> {

    String keyword;

    SearchRecordSet(Context context, String keyword) {
        super(context);

        this.keyword = keyword;
    }

    @Override
    public String getId() {
        return "";
    }

    public String getKeyword() {
        return this.keyword;
    }

    @Override
    public void save() {

    }

    public static final SearchRecordSet create(String keyword) {
        return SearchFactory.createRecordSet(WhatsApp.getContext(), keyword);
    }
}
