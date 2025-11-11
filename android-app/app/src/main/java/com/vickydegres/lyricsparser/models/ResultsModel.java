package com.vickydegres.lyricsparser.models;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultsModel {
    private final ArrayList<HashMap<String, Object>> byLyrics;
    private final ArrayList<HashMap<String, Object>> byTitle;
    private final ArrayList<HashMap<String, Object>> byArtist;
    private final Status status;
    private String term;

    public ResultsModel(String term) {
        byLyrics = new ArrayList<>();
        byTitle = new ArrayList<>();
        byArtist = new ArrayList<>();
        status = new Status();
        this.term = term;
    }

    public ArrayList<HashMap<String, Object>> getByLyrics() {
        return byLyrics;
    }

    public ArrayList<HashMap<String, Object>> getByTitle() {
        return byTitle;
    }

    public ArrayList<HashMap<String, Object>> getByArtist() {
        return byArtist;
    }

    public Status getStatus() {
        return status;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public static class Status {
        public boolean byLyrics, byTitle, byArtist;

        public Status() {
            byLyrics = false;
            byTitle = false;
            byArtist = false;
        }
    }

}
