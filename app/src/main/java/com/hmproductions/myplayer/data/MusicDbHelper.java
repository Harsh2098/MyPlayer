package com.hmproductions.myplayer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Harsh Mahajan on 25/6/2017.
 *
 * Music Database Helper to create SQL tables
 */

public class MusicDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "music";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MusicContract.MusicEntry.TABLE_NAME + " (" +
                    MusicContract.MusicEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MusicContract.MusicEntry.COLUMN_NAME + " TEXT," +
                    MusicContract.MusicEntry.COLUMN_MUSIC_PATH + " TEXT NOT NULL, " +
                    "UNIQUE (" + MusicContract.MusicEntry.COLUMN_MUSIC_PATH + ") ON CONFLICT REPLACE);";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + MusicContract.MusicEntry.TABLE_NAME;


    public MusicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
