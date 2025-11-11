package com.vickydegres.lyricsparser.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "translation")
public class Translation {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "song_id")
    private final int songId;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "target_lang")
    private String target;

    public Translation(int songId, String text, String target) {
        this.songId = songId;
        this.text = text;
        this.target = target;
    }

    @Ignore
    public Translation(int songId, String text) {
        this(songId, text, "EN");
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

    public String getTarget() {
        return target;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
