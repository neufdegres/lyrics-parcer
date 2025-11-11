package com.vickydegres.lyricsparser.database;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseSingleton {
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "lyrics-parser")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
