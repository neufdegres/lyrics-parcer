package com.vickydegres.lyricsparser.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.vickydegres.lyricsparser.database.daos.OriginalDAO;
import com.vickydegres.lyricsparser.database.daos.SongInfoDAO;

/* Message à moi du futur :
* si tu veux faire une migration, trouve un moyen de garder ce qu'il y a dans la BDD, si possible :)
* (ou alors sauvegarde tout d'une façon ou d'une autre)
* */

@Database(
        version = 4,
        entities = {SongInfo.class, Original.class}
        /* autoMigrations = {
                @AutoMigration(from = 3, to = 4)
        } */
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SongInfoDAO songDao();
    public abstract OriginalDAO lyricsDao();

}