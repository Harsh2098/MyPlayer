package com.hmproductions.myplayer.utils;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.AsyncTaskLoader;

import com.hmproductions.myplayer.data.Music;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Harsh Mahajan on 23/7/2017.
 *
 * MusicLoader to get all the .mp3 files on background thread
 */

public class MusicLoader extends AsyncTaskLoader<List<Music>> {

    private static final String FILE_PATH = "file-path";
    private static final String FILE_NAME = "file-name";

    public MusicLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Music> loadInBackground() {

        List<Music> mData = new ArrayList<>();

        ArrayList<HashMap<String,String>> songList=getPlayList(Environment.getExternalStorageDirectory().getAbsolutePath());

        if(songList!=null)
        {
            for (int i=0; i < songList.size(); i++)
            {
                mData.add(new Music(songList.get(i).get(FILE_NAME), songList.get(i).get(FILE_PATH)));
            }
        }
        return mData;
    }

    private ArrayList<HashMap<String,String>> getPlayList(String rootPath) {

        ArrayList<HashMap<String,String>> fileList = new ArrayList<>();

        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory())
                {
                    if (getPlayList(file.getAbsolutePath()) != null)
                        fileList.addAll(getPlayList(file.getAbsolutePath()));

                    else break;
                }
                else if (file.getName().endsWith(".mp3"))
                {
                    HashMap<String, String> song = new HashMap<>();
                    song.put(FILE_PATH, file.getAbsolutePath());
                    song.put(FILE_NAME, file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }
}
