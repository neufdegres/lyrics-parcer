package com.vickydegres.lyricsparser.util;

import androidx.annotation.NonNull;

import com.vickydegres.lyricsparser.database.SongInfo;

public class Song {
    private int id;
    private String title, artist;
    private  String lang; // TODO : remplacer par objet Language


    public Song(int id, String title, String artist, String lang) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.lang = lang;
    }

    public Song() {
        this(0, "", "", "JP");
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getLang() {
        return lang;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public static Song toSong(SongInfo si) {
        return new Song(
                si.getId(),
                si.getTitle(),
                si.getArtist(),
                si.getLang()
        );
    }

    @NonNull
    @Override
    public String toString() {
        return "id=" + id + ",title=" + title + ",artist=" + artist + ",lang=" + lang;
    }
}
