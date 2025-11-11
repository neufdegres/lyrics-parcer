package com.vickydegres.lyricsparser.controller.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.models.DisplayModel;
import com.vickydegres.lyricsparser.util.Func;

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.ViewHolder> {
    private final DisplayModel mModel;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public DisplayAdapter(Context context, DisplayModel data) {
        this.mInflater = LayoutInflater.from(context);
        this.mModel = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.display_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ori = mModel.getOriginal().getLines().get(position);
        String term = mModel.getTerm();
        if (!mModel.getTerm().isEmpty() && ori.toLowerCase().contains(term)) {
            holder.mOriginalView.setText(holder.setEnlightedTerm(ori, mModel.getTerm()));
        } else
            holder.mOriginalView.setText(ori);
        if (!mModel.getRomanization().isEmpty()) {
            String rom = mModel.getRomanization().getLines().get(position);
            holder.mRomanizationView.setText(rom);
            if (!Func.isBlank(rom))
                holder.mRomanizationView.setVisibility(View.VISIBLE);
        }
        if (!mModel.getTranslation().isEmpty()) {
            String tra = mModel.getTranslation().getLines().get(position);
            holder.mTranslationView.setText(tra);
            if (!Func.isBlank(tra))
                holder.mTranslationView.setVisibility(View.VISIBLE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mModel.getOriginal().getLines().size();
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
        return mModel.getOriginal().getLines().get(id);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mLine;

        TextView mOriginalView;
        TextView mRomanizationView;
        TextView mTranslationView;

        ViewHolder(View itemView) {
            super(itemView);
            mOriginalView = itemView.findViewById(R.id.display_row_ori);
            mRomanizationView = itemView.findViewById(R.id.display_row_rom);
            mTranslationView = itemView.findViewById(R.id.display_row_tra);

            // itemView.setOnClickListener(this);
        }

        public int getLine() {
            return mLine;
        }

        private Spanned setEnlightedTerm(String line, String term) {
            String[] tab = line.toLowerCase().split(term, 2);
            String res = "";
            if (tab.length < 2) {
                res = "<font color=\"#FF0000\"><b>"
                        + line.substring(0, term.length())
                        + "</b></font>" + tab[0];
            } else {
                int i1 = line.toLowerCase().indexOf(term);
                res = tab[0] + "<font color=\"#FF0000\"><b>"
                        + line.substring(i1, i1+term.length())
                        + "</b></font>" + tab[1];
            }
            return Html.fromHtml(res, Html.FROM_HTML_MODE_COMPACT);
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
