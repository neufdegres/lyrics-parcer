package com.vickydegres.lyricsparser.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.io.InputStream;
import java.io.BufferedReader;
import java.util.HashMap;

public class MyHttpURLConnection {

    public String get(String url) throws IOException {
        InputStream is = null;
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            is = conn.getInputStream();
            // Read the InputStream and save it in a string
            return readIt(is);
        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != null) {
                is.close();
            }
        }
    }

    // link est DEJA au format JSON !!!
    public String get(String url, String post) throws IOException {
        InputStream is = null;
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(10000 /* milliseconds */); //10000
            conn.setConnectTimeout(15000 /* milliseconds */); //15000
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.v("hihi", "hihi");
            conn.connect();
            Log.v("hihi", "hehe");
            // POST query
            OutputStream os = conn.getOutputStream();
            os.write(post.getBytes());
            os.flush();
            os.close();
            // Starts the query
            is = conn.getInputStream();
            // Read the InputStream and save it in a string
            return readIt(is);
        } catch(IOException e) {
            Log.e("TAG", "[IOException] e : " + e.getMessage());
            return "";
        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != null) {
                is.close();
            }
        }
    }

    private String readIt(InputStream is) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            response.append(line).append('\n');
        }
        return response.toString();
    }

    private String toJSON(HashMap<String, String> map) {
        JSONObject tmp = new JSONObject(map);
        return tmp.toString();
    }
}