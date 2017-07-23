package com.hmproductions.myplayer.services;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.hmproductions.myplayer.ui.MainActivity;

/**
 * Created by Harsh Mahajan on 23/7/2017.
 */

public class PreviousMusicService extends IntentService {

    public PreviousMusicService() {
        super("PreviousMusicService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (MainActivity.mediaPlayer != null)
            MainActivity.mediaPlayer.release();

        if (MainActivity.currentPosition == 0)
            MainActivity.currentPosition = MainActivity.mData.size() - 1;

        else {
            MainActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(MainActivity.mData.get(--MainActivity.currentPosition).getPath()));
            MainActivity.mediaPlayer.start();
        }
    }
}
