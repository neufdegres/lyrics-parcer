package com.vickydegres.lyricsparser.models;

import com.vickydegres.lyricsparser.util.Language;
import com.vickydegres.lyricsparser.util.Lyrics;

public class DisplayModel {
    private int id;
    private String title, titleRomanized, artist;
    private Language lang;
    private Lyrics original, romanization, translation;
    private String term;
    private TITLE_MODE titleMode;

    public enum TITLE_MODE {ORIGINAL, ROMANIZED};

    public DisplayModel(int id) {
        this.id = id;
        this.title = "";
        this.titleRomanized = "";
        this.titleMode = TITLE_MODE.ORIGINAL;
        this.artist = "";
        this.lang = new Language();
        this.original = new Lyrics();
        this.romanization = new Lyrics();
        this.translation = new Lyrics();
        this.term = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleRomanized() {
        return titleRomanized;
    }

    public void setTitleRomanized(String titleRomanized) {
        this.titleRomanized = titleRomanized;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Language getLang() {
        return lang;
    }

    public void setLang(Language lang) {
        this.lang = lang;
    }

    public Lyrics getOriginal() {
        return original;
    }

    public void setOriginal(Lyrics original) {
        this.original = original;
    }

    public Lyrics getRomanization() {
        return romanization;
    }

    public void setRomanization(Lyrics romanization) {
        this.romanization = romanization;
    }

    public Lyrics getTranslation() {
        return translation;
    }

    public void setTranslation(Lyrics translation) {
        this.translation = translation;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public TITLE_MODE getTitleMode() {
        return titleMode;
    }

    public void setTitleMode(TITLE_MODE titleMode) {
        this.titleMode = titleMode;
    }
}
