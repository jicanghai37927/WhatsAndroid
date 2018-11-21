package com.haiyunshan.whatsnote.author.entity;

import android.content.Context;
import android.net.Uri;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.author.dataset.AuthorEntry;

public class AuthorEntity {

    AuthorEntry entry;

    Context context;

    AuthorEntity(Context context, AuthorEntry entry) {
        this.context = context.getApplicationContext();

        this.entry = entry;
    }

    public CharSequence getName() {
        return "济沧海 x 远沧溟";
    }

    public Uri getPortrait() {
        return Uri.parse("file:///android_asset/author/portrait.jpg");
    }

    public Uri getQRCode() {
        return Uri.parse("file:///android_asset/author/qrcode.png");
    }

    public CharSequence getDesc() {
        return "编写于 开发中的未名笔记";
    }

    public static final AuthorEntity create(String id) {
        return AuthorFactory.create(WhatsApp.getContext(), id);
    }
}
