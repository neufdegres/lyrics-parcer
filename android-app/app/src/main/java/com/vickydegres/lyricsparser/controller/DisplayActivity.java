package com.vickydegres.lyricsparser.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vickydegres.lyricsparser.BuildConfig;
import com.vickydegres.lyricsparser.R;
import com.vickydegres.lyricsparser.controller.adapters.DisplayAdapter;
import com.vickydegres.lyricsparser.database.AppDatabase;
import com.vickydegres.lyricsparser.database.AppDatabaseSingleton;
import com.vickydegres.lyricsparser.database.Original;
import com.vickydegres.lyricsparser.database.SongInfo;
import com.vickydegres.lyricsparser.database.repositories.OriginalRepository;
import com.vickydegres.lyricsparser.database.repositories.SongRepository;
import com.vickydegres.lyricsparser.models.DisplayModel;
import com.vickydegres.lyricsparser.net.RequestQueueSingleton;
import com.vickydegres.lyricsparser.util.Func;
import com.vickydegres.lyricsparser.util.Language;
import com.vickydegres.lyricsparser.util.Lyrics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class DisplayActivity extends AppCompatActivity
                             implements DisplayActionDialogFragment.DisplayActionDialogListener{
    private AppDatabase mDatabase;
    private SongRepository mSongRep;
    private OriginalRepository mOriRep;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    DisplayModel mModel;
    TextView mTitle, mArtist, mLangText;
    ImageView mLangFlag;
    Button mRomanize, mTranslate;
    FloatingActionButton mAction;
    RecyclerView mRecyclerView;
    DisplayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mModel = new DisplayModel(getIntent().getIntExtra("selected", -1));

        if (getIntent().hasExtra("term")) {
            String term = getIntent().getStringExtra("term");
            mModel.setTerm(term);
        }

        // build (si nécessaire) de la database
        mDatabase = AppDatabaseSingleton.getInstance(getApplicationContext());
        mSongRep = new SongRepository(mDatabase.songDao());
        mOriRep = new OriginalRepository(mDatabase.lyricsDao());

        mTitle = findViewById(R.id.display_title);
        mArtist = findViewById(R.id.display_artist);
        mLangText = findViewById(R.id.display_lang_text);

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mModel.getTitleMode() == DisplayModel.TITLE_MODE.ORIGINAL) {
                    mTitle.setText(mModel.getTitleRomanized());
                    mModel.setTitleMode(DisplayModel.TITLE_MODE.ROMANIZED);
                } else {
                    mTitle.setText(mModel.getTitle());
                    mModel.setTitleMode(DisplayModel.TITLE_MODE.ORIGINAL);

                }
            }
        });

        mArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selected = mModel.getArtist();
                Intent artistActivityIntent = new Intent(DisplayActivity.this, ArtistActivity.class);
                artistActivityIntent.putExtra("selected", selected);
                startActivity(artistActivityIntent);
            }
        });

        mLangFlag = findViewById(R.id.display_lang_flag);

        mRomanize = findViewById(R.id.display_romanize_button);
        mTranslate = findViewById(R.id.display_translate_button);
        mAction = findViewById(R.id.display_action_button);

        mRomanize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRomanize.setEnabled(false);
                mRomanize.setText(R.string.display_romanization_ongoing);
                loadRomanization();
            }
        });

        mTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTranslate.setEnabled(false);
                mTranslate.setText(R.string.display_translation_ongoing);
                loadTranslation();
            }
        });

        mAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionDialog();
            }
        });

        mRecyclerView = findViewById(R.id.display_lyrics_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new DisplayAdapter(this, mModel);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }

    private void setHeader() {
        mTitle.setText(mModel.getTitle());
        mArtist.setText(mModel.getArtist());
        String lang = mModel.getLang().getName();
        lang = lang.substring(0,1).toUpperCase() + lang.substring(1).toLowerCase();
        mLangText.setText(lang);
        mLangFlag.setImageResource(mModel.getLang().getFlag());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadData() {
        mCompositeDisposable.addAll(
                mSongRep.getSongById(mModel.getId())
                        .subscribe(songs -> {
                            SongInfo curr = songs.get(0);
                            mModel.setTitle(curr.getTitle());
                            mModel.setArtist(curr.getArtist());
                            String lang = curr.getLang();
                            mModel.setLang(new Language(lang));
                            if (!lang.equals("JP")) {
                                mRomanize.setEnabled(false);
                                mTranslate.setEnabled(false);
                            } else {
                                loadTitleRomanization();
                            }
                            setHeader();
                        }),
                mOriRep.getLyricsBySongId(mModel.getId())
                        .subscribe(texts -> {
                            Original ori = texts.get(0);
                            mModel.setOriginal(Lyrics.stringToLyrics(ori.getText()));
                            mAdapter.notifyDataSetChanged();
                        })
        );
    }

    private void loadTitleRomanization() {
        // Instantiate the RequestQueue.
        // RequestQueue queue = Volley.newRequestQueue(this);
        RequestQueue queue = RequestQueueSingleton.getInstance(
                this.getApplicationContext()).getRequestQueue();

        String serverAddress = BuildConfig.SERVER_ADDRESS;
        String url = serverAddress + "romanizer.php";

        // Request a string response from the provided URL.
        try {
            // Create JSON request
            HashMap<String, Object> tmp = new HashMap<>();
            ArrayList<String> tmpArray = new ArrayList<>();
            tmpArray.add(mModel.getTitle());
            tmp.put("lines", tmpArray);
            JSONObject json = new JSONObject(toJSON(tmp));
            Log.v("json", json.toString());

            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            HashMap<String, Object> map = parseRequest(response);
                            if (map.get("code").equals("0")) {
                                LinkedList<String> tmp2list = (LinkedList<String>) map.get("lines");
                                mModel.setTitleRomanized(tmp2list.get(0));
                            } else {
                                Toast.makeText(DisplayActivity.this, map.get("code").toString(), Toast.LENGTH_LONG).show();
                                mModel.setTitleRomanized("[romanization impossible]");
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("volleyyyyy", error.toString());
                            Toast.makeText(DisplayActivity.this, "Volley error. 0", Toast.LENGTH_LONG).show();
                            mModel.setTitleRomanized("[romanization impossible]");
                        }
                    });

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Add the request to the RequestQueue.
            queue.add(request);

        } catch (JSONException e) {
            Toast.makeText(DisplayActivity.this, "JSONObject error.", Toast.LENGTH_LONG).show();
            Log.e("JSONException",e.getMessage());
            mModel.setTitleRomanized("[romanization impossible]");
        }
    }

    private void loadRomanization() {
        // Instantiate the RequestQueue.
        // RequestQueue queue = Volley.newRequestQueue(this);
        RequestQueue queue = RequestQueueSingleton.getInstance(
                this.getApplicationContext()).getRequestQueue();

        String serverAddress = BuildConfig.SERVER_ADDRESS;
        String url = serverAddress + "romanizer.php";

        // Request a string response from the provided URL.
        try {
            // Create JSON request
            HashMap<String, Object> tmp = new HashMap<>();
            tmp.put("lines", mModel.getOriginal().getLines());
            JSONObject json = new JSONObject(toJSON(tmp));
            Log.v("json", json.toString());

            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            HashMap<String, Object> map = parseRequest(response);
                            if (map.get("code").equals("0")) {
                                Lyrics rom = new Lyrics((LinkedList<String>) map.get("lines"));
                                mModel.setRomanization(rom);
                                mAdapter.notifyDataSetChanged();
                                mRomanize.setText(R.string.display_romanized);
                            } else {
                                Toast.makeText(DisplayActivity.this, map.get("code").toString(), Toast.LENGTH_LONG).show();
                                mRomanize.setText(R.string.display_romanize_error);
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("volleyyyyy", error.toString());
                    Toast.makeText(DisplayActivity.this, "Volley error. 1", Toast.LENGTH_LONG).show();
                    mRomanize.setText(R.string.display_romanize_error);
                }
            });

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Add the request to the RequestQueue.
            queue.add(request);

        } catch (JSONException e) {
            Toast.makeText(DisplayActivity.this, "JSONObject error.", Toast.LENGTH_LONG).show();
            Log.e("JSONException",e.getMessage());
            mRomanize.setText(R.string.display_romanize_error);
        }
    }

    private void loadTranslation() {
        // Instantiate the RequestQueue.
        RequestQueue queue = RequestQueueSingleton.getInstance(
                this.getApplicationContext()).getRequestQueue();

        String serverAddress = BuildConfig.SERVER_ADDRESS;
        String url = serverAddress + "translator.php";

        // Request a string response from the provided URL.
        try {
            // Create JSON request
            HashMap<String, Object> tmp = new HashMap<>();
            tmp.put("lines", mModel.getOriginal().getLines());
            JSONObject json = new JSONObject(toJSON(tmp));
            Log.v("json", json.toString());

            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            HashMap<String, Object> map = parseRequest(response);
                            if (map.get("code").equals("0")) {
                                Lyrics rom = new Lyrics((LinkedList<String>) map.get("lines"));
                                mModel.setTranslation(rom);
                                mAdapter.notifyDataSetChanged();
                                mTranslate.setText(R.string.display_translated);
                            } else {
                                Toast.makeText(DisplayActivity.this, map.get("code").toString(), Toast.LENGTH_LONG).show();
                                mTranslate.setText(R.string.display_translate_error);
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("volleyyyyy", error.toString());
                            Toast.makeText(DisplayActivity.this, "Volley error. 2", Toast.LENGTH_LONG).show();
                            mTranslate.setText(R.string.display_translate_error);
                        }
                    });

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Add the request to the RequestQueue.
            queue.add(request);

        } catch (JSONException e) {
            Toast.makeText(DisplayActivity.this, "JSONObject error.", Toast.LENGTH_LONG).show();
            Log.e("JSONException",e.getMessage());
            mTranslate.setText(R.string.display_translate_error);
        }
    }

    private void showActionDialog() {
        DisplayActionDialogFragment dialog = new DisplayActionDialogFragment();
        dialog.show(getSupportFragmentManager(), "DisplayActionDialogFragment");
    }

    private static HashMap<String, Object> parseRequest(JSONObject response) {
        HashMap<String, Object> res = new HashMap<>();
        try {
            res.put("code", response.getString("code"));
            res.put("lines", jsonToLinkedList(response.getJSONArray("lines")));
            Log.v("generateRomTra", res.toString());
        } catch (JSONException e) {
            Log.v("TAG", "[JSONException] e : " + e.getMessage());
        }
        return res;
    }

    private static LinkedList<String> jsonToLinkedList(JSONArray ja) {
        LinkedList<String> res = new LinkedList<>();
        try {
            if (ja != null) {
                int len = ja.length();
                for (int i=0;i<len;i++){
                    res.add(ja.get(i).toString());
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return res;
    }

    private static String toJSON(HashMap<String,Object> data) {
        StringBuilder res = new StringBuilder();
        res.append("{");
        String tmp = "";
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            tmp = String.format("\"%s\":", key);
            tmp += valueToJSON(value);
            res.append(tmp);
            res.append(",");
        }
        return res.substring(0,res.length()-1) + "}";
    }

    private static String valueToJSON(Object value) {
        if (value instanceof String) {
            String tmp = Func.escape((String)value);
            return String.format("\"%s\"", tmp);
        } else if (value instanceof Number) {
            return String.valueOf(value);
        } else if (value instanceof Boolean) {
            return String.format("%b", value);
        } else if (value instanceof ArrayList || value instanceof LinkedList) {
            List<Object> list = (List<Object>) value;
            StringBuilder res = new StringBuilder();
            res.append("[");
            for(Object v : list) {
                res.append(valueToJSON(v));
                res.append(",");
            }
            return res.toString().substring(0,res.length()-1) + "]";
        }
        return value.toString();
    }

    public void onCopyClick(DialogFragment dialog) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("lyrics", mModel.getOriginal().toString());
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void onEditClick(DialogFragment dialog) {
        // TODO : créer page édit
        Intent editActivityIntent = new Intent(DisplayActivity.this, EditLyricsActivity.class);
        editActivityIntent.putExtra("toEdit", mModel.getId());
        startActivity(editActivityIntent);
    }

    @Override
    public void onDeleteClick(DialogFragment dialog) {
        // TODO : créer fragment "vous vous VRAIMENT supprimer ces lyrics ??"
    }
}