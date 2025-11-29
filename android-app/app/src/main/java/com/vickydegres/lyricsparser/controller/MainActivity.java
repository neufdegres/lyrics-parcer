package com.vickydegres.lyricsparser.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.controller.adapters.RecentAdapter;
import com.vickydegres.lyricsparser.database.AppDatabase;
import com.vickydegres.lyricsparser.database.AppDatabaseSingleton;
import com.vickydegres.lyricsparser.database.SongInfo;
import com.vickydegres.lyricsparser.database.repositories.SongRepository;
import com.vickydegres.lyricsparser.util.Song;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements RecentAdapter.ItemClickListener {
    AppDatabase mDatabase;
    private SongRepository mSongRep;
    EditText mSearchbar;
    ImageButton mAddLyrics;
    TextView mAddMultiple;
    RecyclerView mRecyclerView;
    RecentAdapter mAdapter;
    ArrayList<Song> mRecentSongs;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);


        mRecentSongs = new ArrayList<>();

        // build (si nécessaire) de la database
        mDatabase = AppDatabaseSingleton.getInstance(this);
        mSongRep = new SongRepository(mDatabase.songDao());

        mSearchbar = findViewById(R.id.home_searchbar);

        mSearchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String term = mSearchbar.getText().toString().strip();
                    if (!term.isEmpty()) {
                        Intent resultsActivityIntent = new Intent(MainActivity.this, ResultsActivity.class);
                        resultsActivityIntent.putExtra("term", term);
                        startActivity(resultsActivityIntent);
                        return true;
                    }
                }
                return false;
            }
        });

        mAddLyrics = findViewById(R.id.home_addlyrics_button);

        mAddLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addLyricsActivityIntent = new Intent(MainActivity.this, AddLyricsActivity.class);
                startActivity(addLyricsActivityIntent);
            }
        });

        mAddMultiple = findViewById(R.id.main_add_multiple);

        mAddMultiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMultipleLyricsActivityIntent = new Intent(MainActivity.this, AddMultipleLyricsActivity.class);
                startActivity(addMultipleLyricsActivityIntent);
            }
        });

        mRecyclerView = findViewById(R.id.main_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RecentAdapter(this, mRecentSongs);
        mAdapter.setClickListener(this);

        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        loadRecentSongs();
        mSearchbar.setText("");
        mSearchbar.clearFocus();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadRecentSongs() {
        mCompositeDisposable.add(mSongRep.getLastSongs(5)
                .subscribe(songs -> {
                    mRecentSongs.clear();
                    for (SongInfo s : songs) {
                        mRecentSongs.add(new Song(s.getId(),
                                            s.getTitle(),
                                            s.getArtist(),
                                            s.getLang()));
                        Log.v("song", mRecentSongs.get(mRecentSongs.size()-1).toString());
                    }
                    mAdapter.notifyDataSetChanged();
                }));
    }

    @Override
    public void onItemClick(View view, int position) {
        /* Toast.makeText(this, "song="+mAdapter.getItem(position).getTitle(),
                Toast.LENGTH_SHORT).show(); */
        int idSelected = mAdapter.getItem(position).getId();
        Intent displayActivityIntent = new Intent(this, DisplayActivity.class);
        displayActivityIntent.putExtra("selected", idSelected);
        startActivity(displayActivityIntent);
    }

    private void deleteById(int id) {
        Single<Boolean> result = Single.fromCallable(() -> {
                    mSongRep.deleteById(id);
                    return true;
        });
        result.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Boolean aBoolean) {
                        Toast.makeText(MainActivity.this,
                                "Suppression réussie!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(MainActivity.this,
                                "Erreur lors de l'update des données", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}