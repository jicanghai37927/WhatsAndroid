package com.haiyunshan.extract;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class ExtractOpenHelper extends SQLiteOpenHelper {

    static final String TABLE_EXTRACT = "extract";

    public ExtractOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        {
            String sql = "create table " + TABLE_EXTRACT
                    + "(_id integer primary key autoincrement, content text, created integer)";

            db.execSQL(sql);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
