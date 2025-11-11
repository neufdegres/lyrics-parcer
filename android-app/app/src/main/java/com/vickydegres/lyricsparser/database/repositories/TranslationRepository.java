package com.vickydegres.lyricsparser.database.repositories;

import com.vickydegres.lyricsparser.database.Translation;
import com.vickydegres.lyricsparser.database.daos.TranslationDAO;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TranslationRepository {
    private final TranslationDAO translationDao;

    public TranslationRepository(TranslationDAO translationDao) {
        this.translationDao = translationDao;
    }

    public void insert(Translation translation) {
        // Exécuter l'opération d'insertion dans un thread séparé
        Completable.fromAction(() -> translationDao.insert(translation))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void update(Translation translation) {
        // Exécuter l'opération de mise à jour dans un thread séparé
        Completable.fromAction(() -> translationDao.update(translation))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void delete(Translation translation) {
        // Exécuter l'opération de suppression dans un thread séparé
        Completable.fromAction(() -> translationDao.delete(translation))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Flowable<List<Translation>> getTranslationsBySongId(int songId) {
        return translationDao.getTranslationsBySongId(songId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Translation>> getTranslationsByTargetLang(String target) {
        return translationDao.getTranslationsByTargetLang(target)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Translation>> getTranslation(int songId, String target) {
        return translationDao.getTranslation(songId, target)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Translation>> getAllTranslation() {
        // Récupérer tous les sons sous forme de Flowable pour les mises à jour en temps réel
        return translationDao.getAllTranslation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteTable() {
        Completable.fromAction(translationDao::deleteTable)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}
