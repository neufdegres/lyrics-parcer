package com.vickydegres.lyricsparser.models;

import com.vickydegres.lyricsparser.util.Lyrics;

public class EditLyricsModel {
    private final int id;
    private String mTitle;
    private String mArtist;
    private String mLanguage;
    private Lyrics mOriginal;

    public EditLyricsModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public Lyrics getOriginal() {
        return mOriginal;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public void setOriginal(Lyrics original) {
        mOriginal = original;
    }
}
