package com.vickydegres.lyricsparser.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.database.AppDatabase;
import com.vickydegres.lyricsparser.database.AppDatabaseSingleton;
import com.vickydegres.lyricsparser.database.Original;
import com.vickydegres.lyricsparser.database.SongInfo;
import com.vickydegres.lyricsparser.database.repositories.OriginalRepository;
import com.vickydegres.lyricsparser.database.repositories.SongRepository;
import com.vickydegres.lyricsparser.models.EditLyricsModel;
import com.vickydegres.lyricsparser.util.Language;
import com.vickydegres.lyricsparser.util.Lyrics;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class EditLyricsActivity extends AppCompatActivity {
    AppDatabase mDatabase;
    private SongRepository mSongRep;
    private OriginalRepository mOriRep;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    Button mBack, mNext;
    private EditLyricsModel mModel;
    private int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lyrics);

        Intent intent = getIntent();
        int id = intent.getIntExtra("toEdit", -1);
        mModel = new EditLyricsModel(id);

        // build (si nécessaire) de la database
        mDatabase = AppDatabaseSingleton.getInstance(getApplicationContext());
        mSongRep = new SongRepository(mDatabase.songDao());
        mOriRep = new OriginalRepository(mDatabase.lyricsDao());

        currentFragment = 1;

        mBack = findViewById(R.id.edit_back_button);
        mNext = findViewById(R.id.edit_next_button);

        mBack.setActivated(false);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFragment > 1) {
                    if (!mNext.getText().toString().equals("Suivant"))
                        mNext.setText(R.string.edit_next);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.popBackStack();
                    currentFragment--;
                } else {
                    finish();
                }
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("currentFragment", currentFragment + "");
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setReorderingAllowed(true);
                Fragment nextFrag = null;
                switch(currentFragment) {
                    case 1 : //
                        EditLyrics1Fragment curr1 = (EditLyrics1Fragment) fragmentManager.findFragmentById(R.id.edit_fragmentCV);
                        if (curr1.canPassToNextStep()) {
                            mModel.setTitle(curr1.getTitle().trim());
                            mModel.setArtist(curr1.getArtist().trim());
                            mModel.setLanguage(curr1.getLanguage().getCode());
                            Log.v("title", mModel.getTitle());
                            Log.v("artist", mModel.getArtist());
                            Log.v("lang", mModel.getLanguage());
                            mNext.setText(R.string.edit_end);
                            nextFrag = new EditLyrics2Fragment();
                            fragmentTransaction.replace(R.id.edit_fragmentCV, nextFrag, "1");
                            fragmentTransaction.addToBackStack("1"); // Ajouter à la pile de retour si nécessaire
                            currentFragment++;
                        } else {
                            Toast.makeText(EditLyricsActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 2 : // original
                        EditLyrics2Fragment curr2 = (EditLyrics2Fragment) fragmentManager.findFragmentById(R.id.edit_fragmentCV);
                        if (curr2.canPassToNextStep()) {
                            Lyrics ori = Lyrics.stringToLyrics(curr2.getLyrics());
                            mModel.setOriginal(ori);
                        } else {
                            Toast.makeText(EditLyricsActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                        }
                        updateData();
                        finish();
                        break;
                }
                fragmentTransaction.commit();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.edit_fragmentCV, EditLyrics1Fragment.class, null)
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    public EditLyricsModel getModel() {
        return mModel;
    }

    public SongRepository getSongRep() {
        return mSongRep;
    }

    public Button getNext() {
        return mNext;
    }

    private void loadData() {
        mCompositeDisposable.addAll(
            mSongRep.getSongById(mModel.getId())
                    .subscribe(songs -> {
                        SongInfo curr = songs.get(0);
                        mModel.setTitle(curr.getTitle());
                        mModel.setArtist(curr.getArtist());
                        String lang = curr.getLang();
                        mModel.setLanguage(lang);
                        Log.v("loadDataEditActivity", "metadata done");
                    }),
            mOriRep.getLyricsBySongId(mModel.getId())
                    .subscribe(texts -> {
                        Original ori = texts.get(0);
                        mModel.setOriginal(Lyrics.stringToLyrics(ori.getText()));
                        Log.v("loadDataEditActivity", "lyrics done");
                    })
        );
    }

    private void updateData() {
        int id = mModel.getId();
        String title = mModel.getTitle();
        String artist = mModel.getArtist();
        String lang = mModel.getLanguage();
        String lyrics = mModel.getOriginal().toString();

        Log.v("id", id+"");

        SongInfo si = new SongInfo(id, title, artist, lang);
        // Original or = new Original(id, lyrics);

        Single<Boolean> result = Single.zip(
                Single.fromCallable(() -> {
                    mSongRep.update(si);
                    return true;
                }),
                Single.fromCallable(() -> {
                    /*Log.v("oriUpdateId", or.getId()+"");
                    Log.v("oriUpdateSongId", or.getSongId()+"");
                    Log.v("oriUpdateLyrics", or.getText());*/
                    mOriRep.update(id, lyrics);
                    return true;
                }),
                (oriResult, romResult) -> true
        );

        result.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Boolean aBoolean) {
                        Toast.makeText(EditLyricsActivity.this,
                                "Insertion réussie!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(EditLyricsActivity.this,
                                "Erreur lors de l'update des données", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}