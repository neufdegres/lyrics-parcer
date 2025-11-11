package com.vickydegres.lyricsparser.controller.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.util.Song;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private final ArrayList<Song> mSongList;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public ArtistAdapter(Context context, ArrayList<Song> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mSongList = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.artist_row, parent, false);
        return new ArtistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song tmp = mSongList.get(position);

        String txt = "❖ " + tmp.getTitle();
        holder.mTitle.setText(txt);

        Log.v("onBind", "done !");
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    // convenience method for getting data at click position
    public Song getItem(int id) {
        return mSongList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mLine;
        TextView mTitle;

        ViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.artist_row_title);

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
