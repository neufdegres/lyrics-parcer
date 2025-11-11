package com.vickydegres.lyricsparser.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "original")
public class Original {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "song_id")
    private final int songId;

    @ColumnInfo(name = "text")
    private String text;

    public Original(int songId, String text) {
        this.songId = songId;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public int getSongId() {
        return songId;
    }

    public String getText() {
        return text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }
}
