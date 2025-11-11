package com.vickydegres.lyricsparser.controller;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.database.AppDatabase;
import com.vickydegres.lyricsparser.database.AppDatabaseSingleton;
import com.vickydegres.lyricsparser.database.Original;
import com.vickydegres.lyricsparser.database.SongInfo;
import com.vickydegres.lyricsparser.database.repositories.OriginalRepository;
import com.vickydegres.lyricsparser.database.repositories.SongRepository;
import com.vickydegres.lyricsparser.util.FileUtils;
import com.vickydegres.lyricsparser.util.Func;
import com.vickydegres.lyricsparser.util.Lyrics;
import com.vickydegres.lyricsparser.util.Song;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class AddMultipleLyricsActivity extends AppCompatActivity {
    AppDatabase mDatabase;
    private SongRepository mSongRep;
    private OriginalRepository mOriginalRepository;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    TextView mSelected;
    Button mSelectButton, mAddButton;
    ActivityResultLauncher<Intent> getFileLauncher;
    private Uri selectedUri;
    private final Object lock = new Object();
    private int done, fail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_multiple_lyrics);

        // build (si nécessaire) de la database
        mDatabase = AppDatabaseSingleton.getInstance(getApplicationContext());
        mSongRep = new SongRepository(mDatabase.songDao());
        mOriginalRepository = new OriginalRepository(mDatabase.lyricsDao());

        selectedUri = null;
        done = 0;
        fail = 0;

        mSelected = findViewById(R.id.add_multiple_select_label);

        mSelectButton = findViewById(R.id.add_multiple_select_button);
        mAddButton = findViewById(R.id.add_multiple_add);
        mAddButton.setEnabled(false);

        mSelectButton.setOnClickListener(view -> getFilePath());

        mAddButton.setOnClickListener(view -> {
            Runnable r = () -> {
                // ouvrir + lire le fichier
                String content = readFile(selectedUri);

                if (content != null) Log.e("contentFile", "not null !!");
                else {
                    Log.e("contentFile", "null !!");
                    return;
                }

                // créer un parseur
                ArrayList<HashMap<String, Object>> songs = new ArrayList<>();
                String[] songList = content.split("----------\n");

                for (String s : songList) {
                    if (s.isEmpty()) continue;
                    String[] lines = s.split("\n");

                    if (lines.length < 4) continue;

                    // title + artist + lang

                    Song meta = new Song();

                    if(Func.isBlank(lines[0])) continue;
                    else meta.setTitle(lines[0]);

                    if(Func.isBlank(lines[1])) continue;
                    else meta.setArtist(lines[1]);

                    // lyrics

                    if(!Func.isBlank(lines[2])) continue;

                    Lyrics lyrics = new Lyrics();

                    for (int i = 3; i<lines.length; i++) {
                        lyrics.getLines().add(lines[i]);
                    }

                    HashMap<String, Object> tmp = new HashMap<>();
                    tmp.put("meta", meta);
                    tmp.put("lyrics", lyrics);

                    songs.add(tmp);
                }

                // ajouter les musiques dans la bdd
                addToDatabase(songs);

                int total = songs.size();

                while (total > (done + fail)) {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException ignored) {}
                }

                AddMultipleLyricsActivity.this.runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(),
                            "Insertion réussies : " + done + "/" + total,
                            Toast.LENGTH_LONG).show();
                });

                finish();
            };
            (new Thread(r)).start();
        });

        getFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();

                        String path = "";
                        if (uri != null) {
                            selectedUri = uri;
                            mAddButton.setEnabled(true);
                            path = FileUtils.getPath(uri, getApplicationContext());
                        } else {
                            path = "pas de data"; // TODO
                        }
                        mSelected.setText(path);
                    } else {
                        // TODO
                    }
                });
    }

    private void getFilePath() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        getFileLauncher.launch(intent);
    }

    private String readFile(Uri uri) {
        // Open a specific media item using ParcelFileDescriptor.
        ContentResolver resolver = getApplicationContext()
                .getContentResolver();

        // "rw" for read-and-write.
        // "rwt" for truncating or overwriting existing file contents.
        String readOnlyMode = "r";
        try (ParcelFileDescriptor pfd =
                     resolver.openFileDescriptor(uri, readOnlyMode)) {
            assert pfd != null;
            FileDescriptor fileDescriptor = pfd.getFileDescriptor();
            FileInputStream fileInputStream = new FileInputStream(fileDescriptor);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            bufferedReader.close();
            pfd.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addToDatabase(ArrayList<HashMap<String, Object>> songs) {
        for (HashMap<String, Object> song : songs) {
            // d'abord on insère les infos, puis on récupère l'id crée

            Song sg = (Song)song.get("meta");
            SongInfo songInfo = SongInfo.toSongInfo(sg);

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
                    Lyrics lyrics = (Lyrics)song.get("lyrics");
                    Original ori = new Original(songId, lyrics.toString());

                    Single<Boolean> result = Single.fromCallable(() -> {
                        mOriginalRepository.insert(ori);
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
                                    synchronized (lock) {
                                        done++;
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    Toast.makeText(getApplicationContext(),
                                            "id : " + l + ", fail lord de l'insertion des lyrics",
                                            Toast.LENGTH_LONG).show();
                                    synchronized (lock) {
                                        fail++;
                                    }
                                }
                            });
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    synchronized (lock) {
                        fail++;
                    }
                }

            });
        }

    }

}