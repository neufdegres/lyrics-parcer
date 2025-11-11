package com.vickydegres.lyricsparser.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.vickydegres.lyricsparser.database.Translation;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface TranslationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Translation... translation);

    @Delete
    void delete(Translation translation);

    @Update
    void update(Translation... translation);

    @Query("SELECT * FROM translation WHERE song_id=:songId")
    Flowable<List<Translation>> getTranslationsBySongId(int songId);

    @Query("SELECT * FROM translation WHERE target_lang=:target")
    Flowable<List<Translation>> getTranslationsByTargetLang(String target);

    @Query("SELECT * FROM translation WHERE target_lang=:target AND song_id=:songId")
    Flowable<List<Translation>> getTranslation(int songId, String target);

    @Query("SELECT * FROM translation")
    Flowable<List<Translation>> getAllTranslation();

    @Query("DELETE FROM translation")
    void deleteTable();
}
