package com.vickydegres.lyricsparser.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.vickydegres.lyricsparser.database.Original;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface OriginalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Original... originals);

    @Delete
    void delete(Original original);

    @Update
    void update(Original... originals);

    @Query("UPDATE original SET text=:lyrics WHERE song_id=:songId")
    void update(int songId, String lyrics);

    @Query("DELETE FROM original")
    void deleteTable();

    @Query("SELECT * FROM original WHERE song_id=:songId")
    Flowable<List<Original>> getLyricsBySongId(int songId);

    @Query("SELECT * FROM original")
    Flowable<List<Original>> getAllLyrics();

    @Query("SELECT s.id, s.artist, s.title, o.text FROM original AS o " +
            "INNER JOIN songs_info AS s ON o.song_id = s.id " +
            "WHERE text LIKE '%' || :term || '%'")
    Flowable<List<SearchLyrics>> searchTermInLyrics(String term);

    static class SearchLyrics {
        public int id;
        public String artist, title, text;
    }
}
