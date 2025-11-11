package com.vickydegres.lyricsparser.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.controller.adapters.SpinLangAdapter;
import com.vickydegres.lyricsparser.database.AppDatabase;
import com.vickydegres.lyricsparser.database.AppDatabaseSingleton;
import com.vickydegres.lyricsparser.database.repositories.SongRepository;
import com.vickydegres.lyricsparser.util.Func;
import com.vickydegres.lyricsparser.util.Language;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class AddLyrics1Fragment extends Fragment {
    AppDatabase mDatabase;
    private SongRepository mSongRep;
    EditText mTitle, mArtist;
    Spinner mLanguage;
    AddLyricsActivity mActivity;
    private final Handler handler = new Handler();
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_add_lyrics1, container, false);

        mActivity = (AddLyricsActivity)getActivity();
        assert mActivity != null;

        mSongRep = mActivity.getSongRep();

        mTitle = v.findViewById(R.id.al_1_title_field);
        mArtist = v.findViewById(R.id.al_1_artist_field);
        mLanguage = v.findViewById(R.id.al_1_language_spinner);

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || Func.isBlank(s)) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (s.length() == 0 || Func.isBlank(s)) {
                                mTitle.setError("Ce champ ne peut pas être vide.");
                            } else {
                                mTitle.setError(null);
                            }
                        }
                    }, 1000);
                } else {
                    mTitle.setError(null);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!Func.isBlank(mArtist.getText().toString()))
                                checkIfIsAlreadyInDatabase();
                        }
                    }, 1000);
                }
            }
        });

        mArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || Func.isBlank(s)) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (s.length() == 0 || Func.isBlank(s)) {
                                mArtist.setError("Ce champ ne peut pas être vide.");
                            } else {
                                mArtist.setError(null);
                            }
                        }
                    }, 1000);
                } else {
                    mArtist.setError(null);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!Func.isBlank(mTitle.getText().toString()))
                                checkIfIsAlreadyInDatabase();
                        }
                    }, 1000);
                }
            }
        });

        setSpinnerData();

        return v;
    }

    private void setSpinnerData() {
        SpinLangAdapter dataAdapter = new SpinLangAdapter(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, getLanguages());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // dataAdapter.setDropDownViewResource(R.layout.languages_spinner);

        // attaching data adapter to spinner
        mLanguage.setAdapter(dataAdapter);

        mLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                Language lang = dataAdapter.getItem(position);
                dataAdapter.setSelectedItem(lang);
                // Here you can do the action you want to...
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        mLanguage.setSelection(2);
    }

    private void checkIfIsAlreadyInDatabase() {
        String title = mTitle.getText().toString().trim();
        String artist = mArtist.getText().toString().trim();

        mCompositeDisposable.add(mSongRep.getSong(title, artist)
                .subscribe(songs -> {
                    if (!songs.isEmpty()) {
                        Toast.makeText(getActivity(), "Ce morceau existe déjà dans la base de données", Toast.LENGTH_LONG).show();
                        mActivity.getNext().setEnabled(false);
                    } else {
                        mActivity.getNext().setEnabled(true);
                    }
                })
        );
    }

    public String getTitle() {
        return mTitle.getText().toString();
    }

    public String getArtist() {
        return mArtist.getText().toString();
    }

    public Language getLanguage() {
        return ((SpinLangAdapter)mLanguage.getAdapter()).getSelectedItem();
    }

    public boolean canPassToNextStep() {
        String title = mTitle.getText().toString();
        String artist = mArtist.getText().toString();
        return (title.length() > 0 && !Func.isBlank(title)
            && artist.length() > 0 && !Func.isBlank(artist));
    }

    private Language[] getLanguages() {
        Scanner sc;
        InputStream is = getResources().openRawResource(R.raw.languages);
        sc = new Scanner(is);

        ArrayList<Language> array = new ArrayList<>();

        String tmp = "";
        while (sc.hasNextLine()) {
            tmp = sc.nextLine();
            if (tmp.startsWith("code,")) continue;
            String[] tab = tmp.split(",");
            array.add(new Language(tab[0]));
        }

        sc.close();
        try {
            is.close();
        } catch (IOException e) {
            return null;
        }

        Language[] res = new Language[array.size()];

        for(int i=0; i<res.length; i++) {
            res[i] = array.get(i);
        }

        return res;
    }
}