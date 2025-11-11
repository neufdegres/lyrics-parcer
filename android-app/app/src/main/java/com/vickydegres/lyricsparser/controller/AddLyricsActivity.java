package com.vickydegres.lyricsparser.controller;

import static io.reactivex.rxjava3.internal.operators.flowable.FlowableBlockingSubscribe.subscribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.database.AppDatabase;
import com.vickydegres.lyricsparser.database.AppDatabaseSingleton;
import com.vickydegres.lyricsparser.database.Original;
import com.vickydegres.lyricsparser.database.Romanization;
import com.vickydegres.lyricsparser.database.SongInfo;
import com.vickydegres.lyricsparser.database.Translation;
import com.vickydegres.lyricsparser.database.repositories.OriginalRepository;
import com.vickydegres.lyricsparser.database.repositories.RomanizationRepository;
import com.vickydegres.lyricsparser.database.repositories.SongRepository;
import com.vickydegres.lyricsparser.database.repositories.TranslationRepository;
import com.vickydegres.lyricsparser.models.AddLyricsModel;
import com.vickydegres.lyricsparser.util.Lyrics;
import com.vickydegres.lyricsparser.util.Song;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;

public class AddLyricsActivity extends AppCompatActivity {
    AppDatabase mDatabase;
    private SongRepository mSongRep;
    private OriginalRepository mOriginalRepository;
    private RomanizationRepository mRomRep;
    private TranslationRepository mTransRep;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    Button mBack, mNext;
    private AddLyricsModel mModel;
    private int currentFragment;
    private boolean isInDatabase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lyrics);

        // build (si nécessaire) de la database
        mDatabase = AppDatabaseSingleton.getInstance(getApplicationContext());
        mSongRep = new SongRepository(mDatabase.songDao());
        mOriginalRepository = new OriginalRepository(mDatabase.lyricsDao());
//        mRomRep = new RomanizationRepository(mDatabase.romanizationDao());
//        mTransRep = new TranslationRepository(mDatabase.translationDao());

        mModel = new AddLyricsModel();

        currentFragment = 1;

        mBack = findViewById(R.id.al_back_button);
        mNext = findViewById(R.id.al_next_button);
        mNext.setEnabled(false);

        mBack.setActivated(false);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFragment > 1) {
                    if (!mNext.getText().toString().equals("Suivant"))
                        mNext.setText(R.string.al_next);
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
                        AddLyrics1Fragment curr1 = (AddLyrics1Fragment) fragmentManager.findFragmentById(R.id.al_fragmentCV);
                        if (curr1.canPassToNextStep()) {
                            mModel.setTitle(curr1.getTitle().trim());
                            mModel.setArtist(curr1.getArtist().trim());
                            mModel.setLanguage(curr1.getLanguage().getCode());
                            Log.v("title", mModel.getTitle());
                            Log.v("artist", mModel.getArtist());
                            Log.v("lang", mModel.getLanguage());
                            nextFrag = (AddLyrics2Fragment) new AddLyrics2Fragment();
                            fragmentTransaction.replace(R.id.al_fragmentCV, nextFrag, "1");
                            fragmentTransaction.addToBackStack("1"); // Ajouter à la pile de retour si nécessaire
                            currentFragment++;
                        } else {
                            Toast.makeText(AddLyricsActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 2 : // original
                        AddLyrics2Fragment curr2 = (AddLyrics2Fragment) fragmentManager.findFragmentById(R.id.al_fragmentCV);
                        if (curr2.canPassToNextStep()) {
                            Lyrics ori = Lyrics.stringToLyrics(curr2.getLyrics());
                            mModel.setOriginal(ori);
                            Lyrics rom = new Lyrics();
                            mModel.setRomanization(rom);
                            Lyrics tra = new Lyrics();
                            mModel.setTranslation(tra);
                            addToDatabase();
                            finish();
                        } else {
                            Toast.makeText(AddLyricsActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                        }
                        break;
                    /* case 3 : // romanization
                        AddLyrics3Fragment curr3 = (AddLyrics3Fragment) fragmentManager.findFragmentById(R.id.al_fragmentCV);
                        Lyrics rom = new Lyrics(curr3.getRomanization());
                        mModel.setRomanization(rom);
                        mNext.setText(R.string.al_end);
                        nextFrag = (AddLyrics4Fragment) new AddLyrics4Fragment();
                        fragmentTransaction.replace(R.id.al_fragmentCV, nextFrag, "3");
                        fragmentTransaction.addToBackStack("3");
                        currentFragment++;
                        break;
                    case 4 : // traduction
                        AddLyrics4Fragment curr4  = (AddLyrics4Fragment) fragmentManager.findFragmentById(R.id.al_fragmentCV);
                        Lyrics tra = new Lyrics(curr4.getTranslation());
                        mModel.setTranslation(tra);
                        addToDatabase();
                        finish();
                        break; */
                }
                fragmentTransaction.commit();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.al_fragmentCV, AddLyrics1Fragment.class, null)
                    .commit();
        }
    }

    public AddLyricsModel getModel() {
        return mModel;
    }

    public SongRepository getSongRep() {
        return mSongRep;
    }

    public Button getBack() {
        return mBack;
    }

    public Button getNext() {
        return mNext;
    }

    private void addToDatabase() {
        // d'abord on insère les infos, puis on récupère l'id crée
        SongInfo songInfo = new SongInfo(0, mModel.getTitle(), mModel.getArtist(),mModel.getLanguage());

        Single<Long> insertSingle =
                mSongRep.insert(songInfo)
                        .observeOn(AndroidSchedulers.mainThread());
        insertSingle.subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Long l) {
                        int songId = l.intValue();
                        Original ori = new Original(songId, mModel.getOriginal().toString());
                        Romanization rom = null;
                        Translation tra = null;
                        if (!mModel.getRomanization().isEmpty())
                            rom = new Romanization(songId, mModel.getRomanization().toString());
                        if (!mModel.getTranslation().isEmpty())
                            tra = new Translation(songId, mModel.getTranslation().toString());

                        Log.v("coucou", "bande de nouilles !!!!");
                        Log.v("songId", songId + "");
                        insertOriginalAndElse(ori, rom, tra);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {}
                });

        //mCompositeDisposable.add(dis)
    }

    private void insertOriginalAndElse(Original ori, Romanization rom, Translation tra) {
        Single<Boolean> result = null;
        if (rom != null && tra != null) {
            result = Single.zip(
                    Single.fromCallable(() -> {
                        mOriginalRepository.insert(ori);
                        return true;
                    }),
                    Single.fromCallable(() -> {
                        mRomRep.insert(rom);
                        return true;
                    }),
                    Single.fromCallable(() -> {
                        mTransRep.insert(tra);
                        return true;
                    }),
                    (oriResult, romResult, traResult) -> true
            );
        } else if (rom != null) {
            result = Single.zip(
                    Single.fromCallable(() -> {
                        mOriginalRepository.insert(ori);
                        return true;
                    }),
                    Single.fromCallable(() -> {
                        mRomRep.insert(rom);
                        return true;
                    }),
                    (oriResult, romResult) -> true
            );
        } else if (tra != null) {
            result = Single.zip(
                    Single.fromCallable(() -> {
                        mOriginalRepository.insert(ori);
                        return true;
                    }),
                    Single.fromCallable(() -> {
                        mTransRep.insert(tra);
                        return true;
                    }),
                    (oriResult, traResult) -> true
            );
        } else {
            result = Single.fromCallable(() -> {
                        mOriginalRepository.insert(ori);
                        return true;
            });
        }

        result.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Boolean aBoolean) {
                        Toast.makeText(AddLyricsActivity.this,
                                "Insertion réussie!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }


}