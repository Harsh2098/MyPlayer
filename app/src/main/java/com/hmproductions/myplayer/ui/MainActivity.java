package com.hmproductions.myplayer.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hmproductions.myplayer.R;
import com.hmproductions.myplayer.data.Music;
import com.hmproductions.myplayer.data.MusicContract;
import com.hmproductions.myplayer.data.MusicContract.MusicEntry;
import com.hmproductions.myplayer.services.NextMusicService;
import com.hmproductions.myplayer.services.PlayMusicService;
import com.hmproductions.myplayer.services.PreviousMusicService;
import com.hmproductions.myplayer.utils.MusicLoader;
import com.hmproductions.myplayer.utils.MusicRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements
            MusicRecyclerAdapter.OnMusicItemClickListener,
            LoaderManager.LoaderCallbacks<List<Music>> {

    public static final String ACTION_BUTTON_KEY = "action-button-key";
    private static final int MUSIC_PLAYER_NOTIFICATION_ID = 101;
    private static final int MUSIC_FILES_LOADER_ID = 1001;
    private static final int MUSIC_DATABASE_LOADER_ID = 1002;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_PLAY_MUSIC = 11;
    public static RemoteViews mRemoteViews;
    public static MediaPlayer mediaPlayer;
    public static List<Music> mData = new ArrayList<>();
    public static int currentPosition = 0;
    MusicRecyclerAdapter mAdapter;
    RecyclerView musicRecyclerView;
    ProgressBar loadingMP3_progressBar;
    SeekBar music_seekBar;
    TextView musicTitle_textView, lookingTextView;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    LoaderManager.LoaderCallbacks<Cursor> mMusicDatabaseLoader;
    ImageButton playButton, previousButton, nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MusicRecyclerAdapter(MainActivity.this, null, MainActivity.this);

        BindViews();
        InitialiseMusicDatabaseLoader();
        MusicSeekBarChangeListener();

        PlayButtonClickListener();
        NextButtonClickListener();
        PreviousButtonClickListener();

        getSupportLoaderManager().initLoader(MUSIC_FILES_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(MUSIC_DATABASE_LOADER_ID, null, mMusicDatabaseLoader);

        SetupMusicPlayerNotification();
    }

    private void SetupMusicPlayerNotification()
    {
        if(mRemoteViews == null)
        {
            mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_player);
        }

        PendingIntent playPendingIntent = PendingIntent.getService(
                this,
                RC_PLAY_MUSIC,
                new Intent(this, PlayMusicService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.play_button, playPendingIntent);

        PendingIntent previousPendingIntent = PendingIntent.getService(
                this,
                RC_PLAY_MUSIC,
                new Intent(this, PreviousMusicService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.previous_button, previousPendingIntent);

        PendingIntent nextPendingIntent = PendingIntent.getService(
                this,
                RC_PLAY_MUSIC,
                new Intent(this, NextMusicService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.next_button, nextPendingIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder
                .setContent(mRemoteViews)
                .setSmallIcon(R.mipmap.notification_icon)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis());

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(MUSIC_PLAYER_NOTIFICATION_ID, builder.build());
    }

    private void BindViews() {

        musicRecyclerView = (RecyclerView)findViewById(R.id.musicList_recyclerView);
        loadingMP3_progressBar = (ProgressBar)findViewById(R.id.loadingMP3_progressBar);
        playButton = (ImageButton) findViewById(R.id.play_button);
        music_seekBar = (SeekBar)findViewById(R.id.music_seekBar);
        previousButton = (ImageButton) findViewById(R.id.previous_button);
        nextButton = (ImageButton) findViewById(R.id.next_button);
        musicTitle_textView = (TextView)findViewById(R.id.musicTitle_textView);
        lookingTextView = (TextView)findViewById(R.id.looking_textView);
    }

    private void MusicSeekBarChangeListener() {
        music_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser)
                {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void PlayButtonClickListener() {

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playButton.setImageResource(R.mipmap.play_icon);
                        mRemoteViews.setImageViewResource(R.id.play_button, R.mipmap.play_icon);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
                        builder
                                .setContent(mRemoteViews)
                                .setSmallIcon(R.mipmap.notification_icon)
                                .setOngoing(true)
                                .setWhen(System.currentTimeMillis());

                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(MUSIC_PLAYER_NOTIFICATION_ID, builder.build());
                    } else {
                        mediaPlayer.start();
                        playButton.setImageResource(R.mipmap.pause_icon);
                        mRemoteViews.setImageViewResource(R.id.play_button, R.mipmap.pause_icon);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
                        builder
                                .setContent(mRemoteViews)
                                .setSmallIcon(R.mipmap.notification_icon)
                                .setOngoing(true)
                                .setWhen(System.currentTimeMillis());

                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(MUSIC_PLAYER_NOTIFICATION_ID, builder.build());
                    }
                }
            }
        });
    }

    private void NextButtonClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition == mData.size() - 2)
                    onMusicClick(0);
                else
                    onMusicClick(currentPosition + 1);
            }
        });
    }

    private void PreviousButtonClickListener() {
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition == 0)
                    onMusicClick(mData.size() - 1);
                else
                    onMusicClick(currentPosition - 1);
            }
        });
    }

    @Override
    public void onMusicClick(int listMusicPosition) {

        if(mediaPlayer != null)
        {
            mediaPlayer.release();
        }

        Music currentMusic = mData.get(listMusicPosition);
        musicTitle_textView.setText(currentMusic.getTitle());
        mediaPlayer = MediaPlayer.create(this, Uri.parse(currentMusic.getPath()));

        playButton.setImageResource(R.mipmap.pause_icon);

        // Binding music progress with seekbar
        music_seekBar.setMax(mediaPlayer.getDuration() / 1000);
        new Thread() {
            @Override
            public void run() {
                try {
                    while (mediaPlayer != null) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int currentTrackPosition = mediaPlayer.getCurrentPosition() / 1000;
                                music_seekBar.setProgress(currentTrackPosition);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG,"Interrupted Exception");
                }
            }
        }.start();

        mediaPlayer.start();

        currentPosition = listMusicPosition;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(currentPosition == mData.size() - 2)
                    onMusicClick(0);
                else
                    onMusicClick(currentPosition + 1);
            }
        });
    }

    @Override
    public Loader<List<Music>> onCreateLoader(int id, Bundle args) {
        loadingMP3_progressBar.setVisibility(View.VISIBLE);
        return new MusicLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Music>> loader, List<Music> data) {

        loadingMP3_progressBar.setVisibility(View.GONE);

        for(int i=0 ; i<data.size() ; ++i)
        {
            Music currentMusic = data.get(i);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MusicEntry.COLUMN_NAME, currentMusic.getTitle());
            contentValues.put(MusicEntry.COLUMN_MUSIC_PATH, currentMusic.getPath());
            getContentResolver().insert(MusicContract.CONTENT_URI, contentValues);
        }

        Toast.makeText(this,"Playlist refreshed", Toast.LENGTH_SHORT).show();
        getSupportLoaderManager().restartLoader(MUSIC_DATABASE_LOADER_ID, null, mMusicDatabaseLoader);
    }

    @Override
    public void onLoaderReset(Loader<List<Music>> loader) {

    }

    private void InitialiseMusicDatabaseLoader()
    {
        mMusicDatabaseLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(MainActivity.this, MusicContract.CONTENT_URI, null,null,null,null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

                List<Music> list = new ArrayList<>();

                if(cursor != null)
                {
                    while (cursor.moveToNext())
                    {
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(MusicEntry.COLUMN_NAME));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MusicEntry.COLUMN_MUSIC_PATH));
                        list.add(new Music(title, path));
                    }
                }
                mData = list;
                mAdapter.swapData(list);

                musicRecyclerView.setAdapter(mAdapter);
                musicRecyclerView.setLayoutManager(layoutManager);
                musicRecyclerView.setHasFixedSize(false);

                if(list.size() != 0) {
                    loadingMP3_progressBar.setVisibility(View.GONE);
                    lookingTextView.setVisibility(View.GONE);
                }
                else
                    lookingTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mediaPlayer != null) {
            musicTitle_textView.setText(mData.get(currentPosition).getTitle());

            if (mediaPlayer.isPlaying())
                playButton.setImageResource(R.mipmap.pause_icon);
            else
                playButton.setImageResource(R.mipmap.play_icon);
        }
    }
}
