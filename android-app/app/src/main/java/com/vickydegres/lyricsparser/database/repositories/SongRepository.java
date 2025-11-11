package com.vickydegres.lyricsparser.database.repositories;

import com.vickydegres.lyricsparser.database.SongInfo;
import com.vickydegres.lyricsparser.database.daos.OriginalDAO;
import com.vickydegres.lyricsparser.database.daos.SongInfoDAO;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SongRepository {
    private final SongInfoDAO mSongInfoDao;
    
    public SongRepository(SongInfoDAO songInfoDao) {
        this.mSongInfoDao = songInfoDao;
    }

    public Single<Long> insert(SongInfo songInfo) {
        return Single.fromCallable(() -> mSongInfoDao.insert(songInfo))
                .subscribeOn(Schedulers.io());
                //.observeOn(AndroidSchedulers.mainThread())
    }


    public void update(SongInfo songInfo) {
        // Exécuter l'opération de mise à jour dans un thread séparé
        Completable.fromAction(() -> mSongInfoDao.update(songInfo))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void delete(SongInfo songInfo) {
        // Exécuter l'opération de suppression dans un thread séparé
        Completable.fromAction(() -> mSongInfoDao.delete(songInfo))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Flowable<List<SongInfo>> getSongById(int id) {
        return mSongInfoDao.getSongById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<SongInfo>> getSongByArtist(String artist) {
        return mSongInfoDao.getSongsByArtist(artist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<SongInfo>> getSongByLanguage(String lang) {
        return mSongInfoDao.getSongsByLanguage(lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<SongInfo>> getSong(String title, String artist) {
        return mSongInfoDao.getSong(title, artist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<SongInfo>> getAllSongs() {
        // Récupérer tous les sons sous forme de Flowable pour les mises à jour en temps réel
        return mSongInfoDao.getAllSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<SongInfo>> getLastSongs(int i) {
        // Récupérer tous les sons sous forme de Flowable pour les mises à jour en temps réel
        return mSongInfoDao.getLastSongs(i)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteTable() {
        Completable.fromAction(mSongInfoDao::deleteTable)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteById(int id) {
        // Exécuter l'opération de suppression dans un thread séparé
        Completable.fromAction(() -> mSongInfoDao.deleteById(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Flowable<List<SongInfo>> searchTermInTitles(String term) {
        // Récupérer tous les sons sous forme de Flowable pour les mises à jour en temps réel
        return mSongInfoDao.searchTermInTitles(term)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<SongInfoDAO.SearchArtists>> searchTermInArtists(String term) {
        // Récupérer tous les sons sous forme de Flowable pour les mises à jour en temps réel
        return mSongInfoDao.searchTermInArtists(term)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
