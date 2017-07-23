package com.hmproductions.myplayer.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Harsh Mahajan on 25/6/2017.
 */

public class MusicContract {

    static final String CONTENT_AUTHORITY = "com.hmproductions.myplayer";

    static final String PATH_CAPTION = "music";

    private static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_CAPTION);

    public class MusicEntry implements BaseColumns
    {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MUSIC_PATH = "pathUri";

        public static final String TABLE_NAME = "user";
    }
}
