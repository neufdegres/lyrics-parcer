package com.vickydegres.lyricsparser.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.vickydegres.lyricsparser.database.Romanization;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface RomanizationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Romanization... romanization);

    @Delete
    void delete(Romanization romanization);

    @Update
    void update(Romanization... romanization);

    @Query("SELECT * FROM romanization WHERE song_id=:songId")
    Flowable<List<Romanization>> getRomanizationBySongId(int songId);

    @Query("SELECT * FROM romanization")
    Flowable<List<Romanization>> getAllRomanization();

    @Query("DELETE FROM romanization")
    void deleteTable();

//    @Query("DROP TABLE if exists romanization")
//    void dropRomanization();
}
