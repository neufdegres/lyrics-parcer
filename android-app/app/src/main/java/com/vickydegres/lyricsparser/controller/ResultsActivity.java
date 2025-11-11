package com.vickydegres.lyricsparser.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.controller.adapters.ResultsAdapter;
import com.vickydegres.lyricsparser.database.AppDatabase;
import com.vickydegres.lyricsparser.database.AppDatabaseSingleton;
import com.vickydegres.lyricsparser.database.SongInfo;
import com.vickydegres.lyricsparser.database.daos.OriginalDAO;
import com.vickydegres.lyricsparser.database.daos.SongInfoDAO;
import com.vickydegres.lyricsparser.database.repositories.OriginalRepository;
import com.vickydegres.lyricsparser.database.repositories.SongRepository;
import com.vickydegres.lyricsparser.models.ResultsModel;
import com.vickydegres.lyricsparser.util.Lyrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ResultsActivity extends AppCompatActivity implements ResultsAdapter.ItemClickListener {
    AppDatabase mDatabase;
    private SongRepository mSongRep;
    private OriginalRepository mOriRep;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private ResultsModel mModel;
    EditText mSearchbar;
    TextView mNoResult;
    TabLayout mTabLayout;
    RecyclerView mRecyclerView;
    ResultsAdapter mAdapter;
    private ArrayList<HashMap<String, Object>> mCurrentTabResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        String term = intent.getStringExtra("term");

        mModel = new ResultsModel(term);

        // build (si nécessaire) de la database
        mDatabase = AppDatabaseSingleton.getInstance(getApplicationContext());
        mSongRep = new SongRepository(mDatabase.songDao());
        mOriRep = new OriginalRepository(mDatabase.lyricsDao());

        mCurrentTabResults = new ArrayList<>();

        mNoResult = findViewById(R.id.results_no_result);

        mSearchbar = findViewById(R.id.results_searchbar);
        mSearchbar.setText(term);

        mSearchbar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                String newTerm = mSearchbar.getText().toString().strip();
                if (!newTerm.isEmpty()) {
                    mModel.setTerm(newTerm);
                    clearResults();
                    if (mTabLayout.getSelectedTabPosition() == 1) {
                        loadResultsByLyrics(false);
                    } else {
                        mTabLayout.getTabAt(1).select();
                    }
                    return true;
                }
            }
            return false;
        });

        mTabLayout = findViewById(R.id.results_tabs);
        mTabLayout.getTabAt(1).select();

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0 : // All
                        loadAllResults();
                        break;
                    case 1 : // Lyrics
                        ArrayList<HashMap<String, Object>> byLyrics = mModel.getByLyrics();
                        if (!mModel.getStatus().byLyrics) {
                            loadResultsByLyrics(false);
                        } else {
                            mCurrentTabResults.clear();
                            mCurrentTabResults.addAll(byLyrics);
                            if (mCurrentTabResults.isEmpty()) {
                                mNoResult.setVisibility(View.VISIBLE);
                            } else {
                                mNoResult.setVisibility(View.GONE);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 2 : // Title
                        ArrayList<HashMap<String, Object>> byTitle = mModel.getByTitle();
                        if (!mModel.getStatus().byTitle) {
                            loadResultsByTitle(false);
                        } else {
                            mCurrentTabResults.clear();
                            mCurrentTabResults.addAll(byTitle);
                            if (mCurrentTabResults.isEmpty()) {
                                mNoResult.setVisibility(View.VISIBLE);
                            } else {
                                mNoResult.setVisibility(View.GONE);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 3 : // Artist
                        ArrayList<HashMap<String, Object>> byArtist = mModel.getByArtist();
                        if (!mModel.getStatus().byArtist) {
                            loadResultsByArtist(false);
                        } else {
                            mCurrentTabResults.clear();
                            mCurrentTabResults.addAll(byArtist);
                            if (mCurrentTabResults.isEmpty()) {
                                mNoResult.setVisibility(View.VISIBLE);
                            } else {
                                mNoResult.setVisibility(View.GONE);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        mRecyclerView = findViewById(R.id.results_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ResultsAdapter(this, mCurrentTabResults);
        mAdapter.setClickListener(this);
        mAdapter.addContext(ResultsActivity.this);

        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        clearResults();
        loadResultsByLyrics(false);
    }

    public ResultsModel getModel() {
        return mModel;
    }

    private void loadAllResults() {
        mCurrentTabResults.clear();
        Runnable r = () -> {
            ResultsModel.Status status = mModel.getStatus();
            // byTitle
            if (!status.byTitle) {
                loadResultsByTitle(true);
                while (!status.byTitle) {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException ignored) {}
                }
            }
            mCurrentTabResults.addAll(mModel.getByTitle());
            // byArtist
            if (!status.byArtist) {
                loadResultsByArtist(true);
                while (!status.byArtist) {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException ignored) {}
                }
            }
            mCurrentTabResults.addAll(mModel.getByArtist());
            // byLyrics
            if (!status.byLyrics) {
                loadResultsByLyrics(true);
                while (!status.byLyrics) {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException ignored) {}
                }
            }
            mCurrentTabResults.addAll(mModel.getByLyrics());

            ResultsActivity.this.runOnUiThread(() -> {
                if (mCurrentTabResults.isEmpty()) mNoResult.setVisibility(View.VISIBLE);
                else mNoResult.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            });

        };

        (new Thread(r)).start();
    }

    private void loadResultsByLyrics(boolean forAll) {
        // load des resultats des lyrics
        mCompositeDisposable.add(mOriRep.searchTermInLyrics(mModel.getTerm())
                .subscribe(songs -> {
                    ArrayList<HashMap<String, Object>> byLyrics = mModel.getByLyrics();
                    for (OriginalDAO.SearchLyrics s : songs) {
                        HashMap<String, Object> tmp = new HashMap<>();
                        tmp.put("type", "lyrics");
                        tmp.put("id", s.id);
                        tmp.put("artist", s.artist);
                        tmp.put("title", s.title);
                        tmp.put("text", Lyrics.stringToLyrics(s.text));
                        byLyrics.add(tmp);
                        Log.v("results", s.artist + " " + s.title);
                    }
                    Collections.reverse(byLyrics);
                    mModel.getStatus().byLyrics = true;
                    if (!forAll) {
                        if (byLyrics.isEmpty()) mNoResult.setVisibility(View.VISIBLE);
                        else mNoResult.setVisibility(View.GONE);
                        mCurrentTabResults.clear();
                        mCurrentTabResults.addAll(byLyrics);
                        mAdapter.notifyDataSetChanged();
                    }
                }));
    }

    private void loadResultsByTitle(boolean forAll) {
        mCompositeDisposable.add(mSongRep.searchTermInTitles(mModel.getTerm())
                .subscribe(songs -> {
                    ArrayList<HashMap<String, Object>> byTitle = mModel.getByTitle();
                    for (SongInfo s : songs) {
                        HashMap<String, Object> tmp = new HashMap<>();
                        tmp.put("type", "title");
                        tmp.put("id", s.getId());
                        tmp.put("title", s.getTitle());
                        tmp.put("artist", s.getArtist());
                        byTitle.add(tmp);
                        Log.v("results", s.getTitle() + " - " + s.getArtist());
                    }
                    Collections.reverse(byTitle);
                    mModel.getStatus().byTitle = true;
                    if (!forAll) {
                        if (byTitle.isEmpty()) mNoResult.setVisibility(View.VISIBLE);
                        else mNoResult.setVisibility(View.GONE);
                        mCurrentTabResults.clear();
                        mCurrentTabResults.addAll(byTitle);
                        mAdapter.notifyDataSetChanged();
                    }
                })
        );
    }

    private void loadResultsByArtist(boolean forAll) {
        mCompositeDisposable.add(mSongRep.searchTermInArtists(mModel.getTerm())
                .subscribe(artists -> {
                    ArrayList<HashMap<String, Object>> byArtist = mModel.getByArtist();
                    for (SongInfoDAO.SearchArtists a : artists) {
                        HashMap<String, Object> tmp = new HashMap<>();
                        tmp.put("type", "artist");
                        tmp.put("name", a.name);
                        tmp.put("count", a.count);
                        byArtist.add(tmp);
                        Log.v("results", a.name + " - " + a.count);
                    }
                    Collections.reverse(byArtist);
                    mModel.getStatus().byArtist = true;
                    if (!forAll) {
                        if (byArtist.isEmpty()) mNoResult.setVisibility(View.VISIBLE);
                        else mNoResult.setVisibility(View.GONE);
                        mCurrentTabResults.clear();
                        mCurrentTabResults.addAll(byArtist);
                        mAdapter.notifyDataSetChanged();
                    }
                })
        );
    }

    private void clearResults() {
        mModel.getByLyrics().clear();
        mModel.getByArtist().clear();
        mModel.getByTitle().clear();

        mModel.getStatus().byLyrics = false;
        mModel.getStatus().byTitle = false;
        mModel.getStatus().byArtist = false;
    }

    @Override
    public void onItemClick(View view, int position) {
        String type = (String)mAdapter.getItem(position).get("type");
        if (!type.equals("artist")) {
            int idSelected = (int) mAdapter.getItem(position).get("id");
            Intent displayActivityIntent = new Intent(this, DisplayActivity.class);
            displayActivityIntent.putExtra("selected", idSelected);
            if (type.equals("lyrics"))
                displayActivityIntent.putExtra("term", mModel.getTerm());
            startActivity(displayActivityIntent);
        } else {
            String selected = (String) mAdapter.getItem(position).get("name");
            Intent artistActivityIntent = new Intent(this, ArtistActivity.class);
            artistActivityIntent.putExtra("selected", selected);
            startActivity(artistActivityIntent);
        }
    }
}