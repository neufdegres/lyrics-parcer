package com.vickydegres.lyricsparser.database.repositories;

import com.vickydegres.lyricsparser.database.Original;
import com.vickydegres.lyricsparser.database.daos.OriginalDAO;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OriginalRepository {
    private final OriginalDAO mOriginalDao;

    public OriginalRepository(OriginalDAO originalDao) {
        this.mOriginalDao = originalDao;
    }

    public void insert(Original original) {
        // Exécuter l'opération d'insertion dans un thread séparé
        Completable.fromAction(() -> mOriginalDao.insert(original))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void update(Original original) {
        // Exécuter l'opération de mise à jour dans un thread séparé
        Completable.fromAction(() -> mOriginalDao.update(original))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void update(int songId, String lyrics) {
        Completable.fromAction(() -> mOriginalDao.update(songId, lyrics))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void delete(Original original) {
        // Exécuter l'opération de suppression dans un thread séparé
        Completable.fromAction(() -> mOriginalDao.delete(original))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Flowable<List<Original>> getLyricsBySongId(int songId) {
        return mOriginalDao.getLyricsBySongId(songId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Original>> getAllLyrics() {
        // Récupérer tous les sons sous forme de Flowable pour les mises à jour en temps réel
        return mOriginalDao.getAllLyrics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<OriginalDAO.SearchLyrics>> searchTermInLyrics(String term) {
        // Récupérer tous les sons sous forme de Flowable pour les mises à jour en temps réel
        return mOriginalDao.searchTermInLyrics(term)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteTable() {
        Completable.fromAction(mOriginalDao::deleteTable)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}
