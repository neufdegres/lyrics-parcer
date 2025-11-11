package com.vickydegres.lyricsparser.controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.util.Song;

import java.util.ArrayList;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {
    private final ArrayList<Song> mSongs;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecentAdapter(Context context, ArrayList<Song> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mSongs = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recent_layout, parent, false);
        return new RecentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song tmp = mSongs.get(position);
        holder.mTitle.setText(tmp.getTitle());
        holder.mArtist.setText(tmp.getArtist());
        // TODO : remplacer par setImageResource(tmp.getLang().getFlag().getId())
        switch(tmp.getLang()) {
            case "FR" :
                holder.mFlag.setImageResource(R.drawable.fr);
                break;
            case "EN" :
                holder.mFlag.setImageResource(R.drawable.en);
                break;
            case "JP" :
                holder.mFlag.setImageResource(R.drawable.jp);
                break;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    // convenience method for getting data at click position
    public Song getItem(int id) {
        return mSongs.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mLine;
        TextView mTitle;
        TextView mArtist;
        ImageView mFlag;

        ViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.recent_title);
            mArtist = itemView.findViewById(R.id.recent_artist);

            mFlag = itemView.findViewById(R.id.recent_flag);

            itemView.setOnClickListener(this);
        }

        public int getLine() {
            return mLine;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
