package com.vickydegres.lyricsparser.database.repositories;

import com.vickydegres.lyricsparser.database.Romanization;
import com.vickydegres.lyricsparser.database.daos.RomanizationDAO;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RomanizationRepository {
    private final RomanizationDAO romanizationDao;

    public RomanizationRepository(RomanizationDAO romanizationDao) {
        this.romanizationDao = romanizationDao;
    }

    public void insert(Romanization romanization) {
        // Exécuter l'opération d'insertion dans un thread séparé
        Completable.fromAction(() -> romanizationDao.insert(romanization))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void update(Romanization romanization) {
        // Exécuter l'opération de mise à jour dans un thread séparé
        Completable.fromAction(() -> romanizationDao.update(romanization))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void delete(Romanization romanization) {
        // Exécuter l'opération de suppression dans un thread séparé
        Completable.fromAction(() -> romanizationDao.delete(romanization))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Flowable<List<Romanization>> getRomanizationBySongId(int songId) {
        return romanizationDao.getRomanizationBySongId(songId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Romanization>> getAllRomanization() {
        // Récupérer tous les sons sous forme de Flowable pour les mises à jour en temps réel
        return romanizationDao.getAllRomanization()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteTable() {
        Completable.fromAction(romanizationDao::deleteTable)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}
