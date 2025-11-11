package com.vickydegres.lyricsparser.controller.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.vickydegres.lyricsparser.util.Language;

public class SpinLangAdapter extends ArrayAdapter<Language> {
    private final Language[] mValues;
    private Language mSelectedItem;
    
    public SpinLangAdapter(Context context, int resource, Language[] values) {
        super(context, resource);
        this.mValues = values;
        this.mSelectedItem = mValues[0];
    }

    public Language[] getValues() {
        return mValues;
    }

    @Override
    public int getCount() {
        return mValues.length;
    }

    @Override
    public Language getItem(int position) {
        return mValues[position];
    }

    /* @Override
    public long getItemId(int position) {
        return mValues[position].getId();
    }*/

    public Language getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(Language sv) {
        mSelectedItem = sv;
    }
}
