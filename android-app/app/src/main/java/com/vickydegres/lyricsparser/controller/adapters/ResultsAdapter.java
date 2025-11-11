package com.vickydegres.lyricsparser.controller.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.controller.ResultsActivity;
import com.vickydegres.lyricsparser.util.Lyrics;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
    private final ArrayList<HashMap<String, Object>> mResults;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    // data is passed into the constructor
    public ResultsAdapter(Context context, ArrayList<HashMap<String, Object>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mResults = data;
        this.mContext = null;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.results_row, parent, false);
        return new ResultsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HashMap<String, Object> tmp = mResults.get(position);
        // holder.mColor.setImageResource(R.drawable.fr);
        // tmp.get("text").substring(0,15)

        String type = (String)tmp.getOrDefault("type", "");
        String term = ((ResultsActivity) mContext).getModel().getTerm().toLowerCase();
        String lower = "";
        switch(type) {
            case "lyrics":
                holder.mColor.setImageResource(R.drawable.results_lyrics_shape);
                if (mContext.getClass().equals(ResultsActivity.class)) {
                    holder.mUpperText.setText(
                            getUpperText((Lyrics)tmp.get("text"),
                            term));
                }
                else holder.mUpperText.setText("-");
                lower = (String)tmp.get( "title") + " - " + (String)tmp.get("artist");
                holder.mLowerText.setText(lower);
                break;
            case "title":
                holder.mColor.setImageResource(R.drawable.results_title_shape);
                if (mContext.getClass().equals(ResultsActivity.class)) {
                    holder.mUpperText.setText(
                            getUpperText((String)tmp.get("title"),
                                    term));
                }
                else holder.mUpperText.setText("-");
                lower = (String)tmp.get("artist");
                holder.mLowerText.setText(lower);
                break;
            case "artist":
                holder.mColor.setImageResource(R.drawable.results_artist_shape);
                if (mContext.getClass().equals(ResultsActivity.class)) {
                    holder.mUpperText.setText(
                            getUpperText((String)tmp.get("name"),
                            term));
                }
                else holder.mUpperText.setText("-");
                lower = (int)tmp.get("count") + " résultats";
                holder.mLowerText.setText(lower);
                break;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mResults.size();
    }

    // convenience method for getting data at click position
    public HashMap<String, Object> getItem(int id) {
        return mResults.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void addContext(Context c) {
        mContext = c;
    }

    private Spanned getUpperText(@NonNull String l, String term) {
        // we look if the text has mostly latin characters or not
        // TODO
        // we add bold and red color to the term
        Log.v("l", l);
        Log.v("term", term);
        String[] tab = splitLineFromTerm(l, term);
        String res = "";
        if (tab.length < 2) {
            res = "<font color=\"#FF0000\"><b>"
                    + l.substring(0, term.length())
                    + "</b></font>" + tab[0];
        } else {
            int i1 = l.toLowerCase().indexOf(term);
            res = tab[0] + "<font color=\"#FF0000\"><b>"
                    + l.substring(i1, i1+term.length())
                    + "</b></font>" + tab[1];
        }
        return Html.fromHtml(res, Html.FROM_HTML_MODE_COMPACT);
    }

    private Spanned getUpperText(@NonNull Lyrics l, String term) {
        String tmp = "";
        // we find the first line that contains the term
        for (String line : l.getLines()) {
            if (line.toLowerCase().contains(term)) {
                tmp = line;
                break;
            }
        }
        return getUpperText(tmp, term);
    }

    private String[] splitLineFromTerm(String line, String term) {
        ArrayList<String> list = new ArrayList<>();
        String tmp = line;
        int idx = tmp.toLowerCase().indexOf(term);

        if (idx == -1) return new String[] {line};

        if (idx > 0) list.add(tmp.substring(0,idx));
        tmp = tmp.substring(idx+term.length());
        list.add(tmp);

        String[] res = new String[list.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = list.get(i);
        }
        return res;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mColor;
        TextView mUpperText;
        TextView mLowerText;

        ViewHolder(View itemView) {
            super(itemView);
            mColor = itemView.findViewById(R.id.results_row_color);

            mUpperText = itemView.findViewById(R.id.results_row_upper_text);
            mLowerText = itemView.findViewById(R.id.results_row_lower_text);

            itemView.setOnClickListener(this);
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
