package com.hmproductions.myplayer.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.hmproductions.myplayer.R;
import com.hmproductions.myplayer.ui.MainActivity;

/**
 * Created by Harsh Mahajan on 23/7/2017.
 */

public class PlayMusicService extends IntentService {

    private static final int MUSIC_PLAYER_NOTIFICATION_ID = 101;

    public PlayMusicService() {
        super("PlayMusicService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (MainActivity.mediaPlayer != null) {
            if (MainActivity.mediaPlayer.isPlaying()) {
                MainActivity.mediaPlayer.pause();
                MainActivity.mRemoteViews.setImageViewResource(R.id.play_button, R.mipmap.play_icon);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder
                        .setContent(MainActivity.mRemoteViews)
                        .setSmallIcon(R.mipmap.notification_icon)
                        .setOngoing(true)
                        .setWhen(System.currentTimeMillis());

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(MUSIC_PLAYER_NOTIFICATION_ID, builder.build());

            } else {
                MainActivity.mediaPlayer.start();
                MainActivity.mRemoteViews.setImageViewResource(R.id.play_button, R.mipmap.pause_icon);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder
                        .setContent(MainActivity.mRemoteViews)
                        .setSmallIcon(R.mipmap.notification_icon)
                        .setOngoing(true)
                        .setWhen(System.currentTimeMillis());

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(MUSIC_PLAYER_NOTIFICATION_ID, builder.build());
            }
        }
    }
}
