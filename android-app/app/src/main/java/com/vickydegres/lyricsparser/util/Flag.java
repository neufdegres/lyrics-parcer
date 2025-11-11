package com.vickydegres.lyricsparser.util;

import com.vickydegres.lyricsparser.R;

public enum Flag {
    EN(R.drawable.en),
    FR(R.drawable.fr),
    JP(R.drawable.jp);

    private final int id;
    private Flag(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
