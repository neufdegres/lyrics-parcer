package com.vickydegres.lyricsparser.models;

import com.vickydegres.lyricsparser.util.Lyrics;

import java.util.Arrays;
import java.util.LinkedList;

public class AddLyricsModel {
    private String mTitle;
    private String mArtist;
    private String mLanguage;
    private Lyrics mOriginal;
    private Lyrics mRomanization;
    private Lyrics mTranslation;

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

    public Lyrics getRomanization() {
        return mRomanization;
    }

    public Lyrics getTranslation() {
        return mTranslation;
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

    public void setRomanization(Lyrics romanization) {
        mRomanization = romanization;
    }

    public void setTranslation(Lyrics translation) {
        mTranslation = translation;
    }

}
