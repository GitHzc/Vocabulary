package com.example.granddictionary;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2018/7/23 0023.
 */

public class DictContentProvider extends ContentProvider {
    private Context mContext;
    public static final String AUTHORITY = "com.example.granddictionary.DictContentProvider";
    public static final int DICT_URI_CODE = 0;
    private static final UriMatcher uriMatch = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatch.addURI(AUTHORITY, DBOpenHandler.TABLE_DICT_NAME, DICT_URI_CODE);
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatch.match(uri) == DICT_URI_CODE) {
            DictDb db = new DictDb(mContext);
            return db.update(values, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("UnSupport URI:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatch.match(uri) == DICT_URI_CODE) {
            DictDb db = new DictDb(mContext);
            db.insert(values);
        } else {
            throw new IllegalArgumentException("UnSupport URI:" + uri);
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatch.match(uri) == DICT_URI_CODE) {
            DictDb db = new DictDb(mContext);
            return db.delete(selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("UnSupport URI:" + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (uriMatch.match(uri) == DICT_URI_CODE) {
            DictDb db = new DictDb(mContext);
            return db.query(projection, selection, selectionArgs, sortOrder);
        } else {
            throw new IllegalArgumentException("UnSupport URI:" + uri);
        }
    }
}
