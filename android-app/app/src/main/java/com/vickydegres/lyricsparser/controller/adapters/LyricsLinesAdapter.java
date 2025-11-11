package com.vickydegres.lyricsparser.controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.util.Func;
import com.vickydegres.lyricsparser.util.Lyrics;

import java.util.ArrayList;

public class LyricsLinesAdapter extends RecyclerView.Adapter<LyricsLinesAdapter.ViewHolder>{
    private final ArrayList<ViewHolder> mViewHoldersList;
    private final Lyrics mLyrics;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private OnEditChangedListener onEditChangedListener;

    // data is passed into the constructor
    public LyricsLinesAdapter(Context context, Lyrics data) {
        this.mInflater = LayoutInflater.from(context);
        this.mLyrics = data;
        this.mViewHoldersList = new ArrayList<>();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.lyrics_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ori = mLyrics.getLines().get(position);
        holder.mOriginal.setText(ori);

        if (Func.isBlank(ori)) {
            holder.mEdit.setVisibility(View.GONE);
        }

        mViewHoldersList.add(position, holder);

        holder.mEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String edit = holder.mEdit.getText().toString();
                    onEditChangedListener.onEditChanged(holder.getAdapterPosition(), edit);
                }
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mLyrics.getLines().size();
    }

    @Override
    public long getItemId(int position) {
        return Integer.toUnsignedLong(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mLyrics.getLines().get(id);
    }

    public ViewHolder getVH(int pos) {
        return mViewHoldersList.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mId;
        TextView mOriginal;
        EditText mEdit;

        ViewHolder(View itemView) {
            super(itemView);
            mOriginal = itemView.findViewById(R.id.lyrics_row_ori);
            mEdit = itemView.findViewById(R.id.lyrics_row_edit);
        }

        public int getId() {
            return mId;
        }

        public String getEdit() {
            return mEdit.getText().toString();
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    public interface OnEditChangedListener {
        void onEditChanged(int position, String edit);
    }

    public void setOnEditChangedListener(OnEditChangedListener listener) {
        this.onEditChangedListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}