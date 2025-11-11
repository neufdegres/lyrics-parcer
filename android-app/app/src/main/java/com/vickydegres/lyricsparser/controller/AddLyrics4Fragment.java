package com.vickydegres.lyricsparser.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.controller.adapters.LyricsLinesAdapter;
import com.vickydegres.lyricsparser.util.Func;
import com.vickydegres.lyricsparser.util.Lyrics;

import java.util.LinkedList;

public class AddLyrics4Fragment extends Fragment {
    RecyclerView mRecyclerView;
    LyricsLinesAdapter mAdapter;
    LinkedList<String> mLines;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_add_lyrics4, container, false);

        mRecyclerView = v.findViewById(R.id.al_4_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Lyrics ori = ((AddLyricsActivity)getActivity()).getModel().getOriginal();

        mLines = Func.initializeAL(ori.getLinesCount());

        // mAdapter = new LyricsLinesAdapter(getActivity(), readLyricsFromFile(R.raw.raise_my_sword));
        mAdapter = new LyricsLinesAdapter(getActivity(), ori);
        mAdapter.setOnEditChangedListener(new LyricsLinesAdapter.OnEditChangedListener() {
            @Override
            public void onEditChanged(int position, String edit) {
                if (!Func.isBlank(edit)) {
                    mLines.set(position, edit);
                } else {
                    mLines.set(position, "\\");
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    public LinkedList<String> getTranslation() {
        Lyrics ori = ((AddLyricsActivity)getActivity()).getModel().getOriginal();
        for (int i=0; i<mLines.size(); i++) {
            if (Func.isBlank(ori.getLines().get(i))) {
                mLines.set(i, "");
            }
        }
        return mLines;
    }

}