package com.vickydegres.lyricsparser.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.controller.adapters.LyricsLinesAdapter;
import com.vickydegres.lyricsparser.util.Func;
import com.vickydegres.lyricsparser.util.Lyrics;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

public class AddLyrics3Fragment extends Fragment {
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
        View v =  inflater.inflate(R.layout.fragment_add_lyrics3, container, false);

        mRecyclerView = v.findViewById(R.id.al_3_rv);
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

    public LinkedList<String> getRomanization() {
        Lyrics ori = ((AddLyricsActivity)getActivity()).getModel().getOriginal();
        for (int i=0; i<mLines.size(); i++) {
            if (Func.isBlank(ori.getLines().get(i))) {
                mLines.set(i, "");
            }
        }
        return mLines;
    }

    private Lyrics readLyricsFromFile(int id) {
        Scanner sc;
        InputStream is = getResources().openRawResource(id);
        sc = new Scanner(is);

        LinkedList<String> lines = new LinkedList<>();

        String tmp = "";
        while (sc.hasNextLine()) {
            tmp = sc.nextLine();
            if (tmp.isEmpty()) tmp = "(vide)";
            lines.add(tmp);
        }

        sc.close();
        try {
            is.close();
        } catch (IOException e) {
            return null;
        }

        return new Lyrics(lines);
    }
}