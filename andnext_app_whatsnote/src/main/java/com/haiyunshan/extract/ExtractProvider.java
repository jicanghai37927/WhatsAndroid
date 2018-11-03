package com.haiyunshan.extract;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.storage.LocalStorage;

import java.io.File;

public class ExtractProvider extends ContentProvider {

    static final String AUTHORITY = "com.haiyunshan.whatsnote.extractprovider";

    static final String PATH_EXTRACT = "extract";

    static final int CODE_EXTRACT           = 1001;
    static final int CODE_EXTRACT_SINGLE    = 1002;

    Uri extractUri;
    SQLiteDatabase extractDb;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, PATH_EXTRACT, CODE_EXTRACT);
        uriMatcher.addURI(AUTHORITY, PATH_EXTRACT + "/#", CODE_EXTRACT_SINGLE);
    }

    @Override
    public boolean onCreate() {
        File file = LocalStorage.getInstance().getDirectory(getContext(), LocalStorage.PATH_EXTRACT_DIR);
        file = new File(file, "extract_ds.db");

        ExtractOpenHelper helper = new ExtractOpenHelper(getContext(), file.getAbsolutePath(), null, 1);
        this.extractDb = helper.getWritableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String table = getTable(uri);
        if (TextUtils.isEmpty(table)) {
            throw new IllegalArgumentException("Unsupported uri = " + uri);
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();//最API
        qb.setTables(table);

        if (uriMatcher.match(uri) == CODE_EXTRACT_SINGLE) {
            long id = ContentUris.parseId(uri);
            qb.appendWhere(ExtractColumns._ID + "=" + id);
        }

        Cursor cursor = qb.query(extractDb,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String table = getTable(uri);
        if (TextUtils.isEmpty(table)) {
            throw new IllegalArgumentException("Unsupported uri = " + uri);
        }

        long id = extractDb.insert(table, null, values);
        if (id > 0) {

            uri = ContentUris.withAppendedId(getExtractUri(), id);

            getContext().getContentResolver().notifyChange(uri, null);

        }

        return uri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table = getTable(uri);
        if (TextUtils.isEmpty(table)) {
            throw new IllegalArgumentException("Unsupported uri = " + uri);
        }

        int count;
        if (uriMatcher.match(uri) == CODE_EXTRACT_SINGLE) {
            long id = ContentUris.parseId(uri);
            count = extractDb.delete(ExtractOpenHelper.TABLE_EXTRACT, ExtractColumns._ID + " = " + id, selectionArgs);
        } else {
            count = extractDb.delete(ExtractOpenHelper.TABLE_EXTRACT, selection, selectionArgs);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    String getTable(Uri uri) {
        String table = null;

        int code = uriMatcher.match(uri);
        switch (code) {
            case CODE_EXTRACT:
            case CODE_EXTRACT_SINGLE: {
                table = ExtractOpenHelper.TABLE_EXTRACT;

                break;
            }
        }

        return table;
    }

    Uri getExtractUri() {
        if (extractUri == null) {
            extractUri = obtainUri();
        }

        return extractUri;
    }

    public static final Uri obtainUri() {
        String text = "content://" + AUTHORITY + "/" + PATH_EXTRACT;
        Uri uri = Uri.parse(text);

        return uri;
    }

    public static final ContentValues create(CharSequence text) {
        String str = text.toString().trim();
        if (TextUtils.isEmpty(str)) {
            return null;
        }

        ContentValues values = new ContentValues();

        values.put(ExtractColumns.CONTENT, text.toString());
        values.put(ExtractColumns.CREATED, System.currentTimeMillis());

        return values;
    }
}
