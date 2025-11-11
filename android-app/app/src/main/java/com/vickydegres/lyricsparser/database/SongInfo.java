package com.vickydegres.lyricsparser.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.vickydegres.lyricsparser.util.Song;

@Entity(tableName = "songs_info")
public class SongInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "lang")
    private String lang;

    public SongInfo(int id, String title, String artist, String lang) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.lang = lang;
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

    public static SongInfo toSongInfo(Song sg) {
        if (sg == null) throw new IllegalArgumentException();
        return new SongInfo(sg.getId(),
                sg.getTitle(),
                sg.getArtist(),
                sg.getLang());
    }

}
