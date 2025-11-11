package com.vickydegres.lyricsparser.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.vickydegres.lyricsparser.database.SongInfo;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface SongInfoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SongInfo songInfo);

    @Delete
    void delete(SongInfo songInfo);

    @Update
    void update(SongInfo... songs_infoInfo);

    @Query("SELECT * FROM songs_info WHERE id=:id")
    Flowable<List<SongInfo>> getSongById(int id);

    @Query("SELECT * FROM songs_info WHERE artist=:artist ORDER BY artist DESC")
    Flowable<List<SongInfo>> getSongsByArtist(String artist);

    @Query("SELECT * FROM songs_info WHERE lang=:lang")
    Flowable<List<SongInfo>> getSongsByLanguage(String lang);

    @Query("SELECT * FROM songs_info WHERE artist=:artist AND title=:title")
    Flowable<List<SongInfo>> getSong(String title, String artist);

    @Query("SELECT * FROM songs_info")
    Flowable<List<SongInfo>> getAllSongs();

    @Query("SELECT * FROM songs_info ORDER BY id DESC LIMIT :i")
    Flowable<List<SongInfo>> getLastSongs(int i);

    @Query("DELETE FROM songs_info")
    void deleteTable();

    @Query("DELETE FROM songs_info WHERE id=:id")
    void deleteById(int id);

    @Query("SELECT * FROM songs_info " +
            "WHERE title LIKE '%' || :term || '%' ")
    Flowable<List<SongInfo>> searchTermInTitles(String term);

    @Query("SELECT DISTINCT artist AS name, COUNT(*) AS count FROM songs_info " +
            "WHERE artist LIKE '%' || :term || '%' " +
            "GROUP BY artist")
    Flowable<List<SearchArtists>> searchTermInArtists(String term);

    static class SearchArtists {
        public String name;
        public int count;
    }
}
