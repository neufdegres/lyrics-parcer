package com.vickydegres.lyricsparser.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.controller.adapters.ArtistAdapter;
import com.vickydegres.lyricsparser.database.AppDatabase;
import com.vickydegres.lyricsparser.database.AppDatabaseSingleton;
import com.vickydegres.lyricsparser.database.repositories.SongRepository;
import com.vickydegres.lyricsparser.util.Song;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ArtistActivity extends AppCompatActivity implements ArtistAdapter.ItemClickListener {
    AppDatabase mDatabase;
    private SongRepository mSongRep;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    TextView mTitle, mCount;
    RecyclerView mRecyclerView;
    ArtistAdapter mAdapter;
    private String artistName;
    private ArrayList<Song> mSongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Intent intent = getIntent();
        artistName = intent.getStringExtra("selected");

        mSongList = new ArrayList<>();

        // build (si nécessaire) de la database
        mDatabase = AppDatabaseSingleton.getInstance(getApplicationContext());
        mSongRep = new SongRepository(mDatabase.songDao());

        mTitle = findViewById(R.id.artist_title);
        mCount = findViewById(R.id.artist_count);

        String txt = "Liste des chansons de l\'artiste\n\"" + artistName + "\"";
        mTitle.setText(txt);

        mRecyclerView = findViewById(R.id.artist_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ArtistAdapter(this, mSongList);
        mAdapter.setClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSongList.clear();
        loadData();
    }

    private void loadData() {
        mCompositeDisposable.add(mSongRep.getSongByArtist(artistName)
                .subscribe(songs -> {
                    songs.forEach(s -> mSongList.add(Song.toSong(s)));
                    int count = mSongList.size();
                    String txt = count + " résultat";
                    if (count > 1) txt += "s";
                    mCount.setText(txt);
                    mAdapter.notifyDataSetChanged();
                })
        );
    }

    @Override
    public void onItemClick(View view, int position) {
        int idSelected = mAdapter.getItem(position).getId();
        Intent displayActivityIntent = new Intent(this, DisplayActivity.class);
        displayActivityIntent.putExtra("selected", idSelected);
        startActivity(displayActivityIntent);
    }
}