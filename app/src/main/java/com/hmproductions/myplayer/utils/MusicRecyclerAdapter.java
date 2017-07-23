package com.hmproductions.myplayer.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmproductions.myplayer.R;
import com.hmproductions.myplayer.data.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harsh Mahajan on 23/7/2017.
 *
 * Music Recycler Adapter to display list in Recycler View
 */

public class MusicRecyclerAdapter extends RecyclerView.Adapter<MusicRecyclerAdapter.MusicViewHolder> {

    private Context mContext;
    private List<Music> mData = new ArrayList<>();
    private OnMusicItemClickListener mClickListener;

    public MusicRecyclerAdapter(Context context, List<Music> data, OnMusicItemClickListener listener) {
        mContext = context;
        mData = data;
        mClickListener = listener;
    }

    @Override
    public MusicRecyclerAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MusicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MusicRecyclerAdapter.MusicViewHolder holder, int position) {

        holder.musicThumbnail_imageView.setImageResource(R.mipmap.notification_icon);

        holder.musicTitle_textView.setText(mData.get(position).getTitle());
    }

    public void swapData(List<Music> data)
    {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mData == null) return 0;
        return mData.size();
    }

    public interface OnMusicItemClickListener {
        void onMusicClick(int listMusicPosition);
    }

    class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView musicThumbnail_imageView;
        TextView musicTitle_textView;


        MusicViewHolder(View itemView) {
            super(itemView);

            musicThumbnail_imageView = (ImageView)itemView.findViewById(R.id.list_thumbnail_imageView);
            musicTitle_textView = (TextView)itemView.findViewById(R.id.list_musicTitle_textView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onMusicClick(getAdapterPosition());
        }
    }
}
