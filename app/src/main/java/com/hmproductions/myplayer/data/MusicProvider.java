package com.hmproductions.myplayer.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Harsh Mahajan on 25/6/2017.
 */

public class MusicProvider extends ContentProvider {

    static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    MusicDbHelper mDatabaseHelper;
    SQLiteDatabase mDatabase;

    private static final int URI_WITHOUT_PATH = 100;
    private static final int URI_WITH_PATH = 101;

    static {
        mUriMatcher.addURI(MusicContract.CONTENT_AUTHORITY, MusicContract.PATH_CAPTION, URI_WITHOUT_PATH);
        mUriMatcher.addURI(MusicContract.CONTENT_AUTHORITY, MusicContract.PATH_CAPTION + "/#", URI_WITH_PATH);
    }

    @Override
    public boolean onCreate() {

        mDatabaseHelper = new MusicDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        mDatabase = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = null;

        switch (mUriMatcher.match(uri))
        {
            case URI_WITHOUT_PATH :
                cursor = mDatabase.query(MusicContract.MusicEntry.TABLE_NAME, projection, selection, selectionArgs, null,null,sortOrder);
                break;

            case URI_WITH_PATH :
                selection = MusicContract.MusicEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = mDatabase.query(MusicContract.MusicEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default : throw new IllegalArgumentException("Cannot serve URI request at this moment.");
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        mDatabase = mDatabaseHelper.getWritableDatabase();

        long colID;
        switch (mUriMatcher.match(uri)) {
            case URI_WITHOUT_PATH:
                colID = mDatabase.insert(MusicContract.MusicEntry.TABLE_NAME, null, values);
                if (colID == -1)
                    Toast.makeText(getContext(), "Failed to insert", Toast.LENGTH_SHORT).show();
                break;

            default:
                throw new IllegalArgumentException("Cannot serve URI request at this moment.");
        }

        return ContentUris.withAppendedId(uri, colID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new IllegalArgumentException("Not implemented here.");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new IllegalArgumentException("Not implemented here.");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
