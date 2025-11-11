package com.vickydegres.lyricsparser.controller;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.util.Func;

public class EditLyrics2Fragment extends Fragment {
    EditLyricsActivity mActivity;
    EditText mLyrics;
    private final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_lyrics2, container, false);

        mActivity = (EditLyricsActivity)getActivity();
        assert mActivity != null;

        mLyrics = v.findViewById(R.id.al_2_lyrics);

        mLyrics.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || Func.isBlank(s)) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (s.length() == 0 || Func.isBlank(s)) {
                                mLyrics.setError("Ce champ ne peut pas être vide.");
                            } else {
                                mLyrics.setError(null);
                            }
                        }
                    }, 1000);
                } else {
                    mLyrics.setError(null);
                }
            }
        });

        setLyricsData();

        return v;
    }

    public String getLyrics() {
        return mLyrics.getText().toString();
    }

    private void setLyricsData() {
        Runnable r = () -> {
            while (mActivity.getModel() == null && mActivity.getModel().getOriginal() == null) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ignored) {}
            }

            mActivity.runOnUiThread(() -> {
                mLyrics.setText(mActivity.getModel().getOriginal().toString());
            });
        };

        (new Thread(r)).start();
    }

    public boolean canPassToNextStep() {
        String text = mLyrics.getText().toString();
        return text.length() > 0 && !Func.isBlank(text);
    }
}
