package com.vickydegres.lyricsparser.api;

import android.util.Log;

import com.vickydegres.lyricsparser.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequests {

    public static HashMap<String, Object> generateRomanization(LinkedList<String> lines) {
        MyHttpURLConnection conn = new MyHttpURLConnection();
        HashMap<String, Object> tmp = new HashMap<>();
        tmp.put("lines", lines);
        try {
            String json = toJSON(tmp);
            Log.v("json", json);
            Log.v("okk", "test");
            String serverAddress = BuildConfig.SERVER_ADDRESS;
            String url = serverAddress + "romanizer.php";
            String raw = conn.get(url);
            Log.v("okk", "super !!!!");
            return parseGenerateRomanization(raw);
        } catch (IOException e) {
            Log.e("erreur", "", e.getCause());
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<String, Object> parseGenerateRomanization(String json) {
        HashMap<String, Object> res = new HashMap<>();
        try {
            final JSONObject response = new JSONObject(json);
            res.put("code", response.getString("code"));
            res.put("lines", jsonToLinkedList(response.getJSONArray("lines")));
            Log.v("generateRomanization", res.toString());
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

    private static String valueToJSON(Object value) {
        if (value instanceof String) {
            return String.format("\"%s\"", value);
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
}
