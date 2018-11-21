package com.haiyunshan.whatsnote.author.entity;

import android.content.Context;

class AuthorFactory {

    static final AuthorEntity create(Context context, String id) {
        return new AuthorEntity(context, null);
    }
}
