package com.hmproductions.myplayer.data;

/**
 * Created by Harsh Mahajan on 23/7/2017.
 */

public class Music {

    private String mTitle, mPath;

    public Music(String title, String path) {
        mTitle = title;
        mPath = path;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPath() {
        return mPath;
    }
}
