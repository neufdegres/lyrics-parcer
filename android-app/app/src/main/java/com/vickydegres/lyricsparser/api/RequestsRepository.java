package com.vickydegres.lyricsparser.api;

import java.util.LinkedList;
import java.util.HashMap;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RequestsRepository {

    public static Single<HashMap<String, Object>> generateRomanization(LinkedList<String> lines) {
        return Single.fromCallable(() -> HttpRequests.generateRomanization(lines))
                .observeOn(Schedulers.io());
    }
}
