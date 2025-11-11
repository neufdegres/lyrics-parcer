package com.vickydegres.lyricsparser.util;

import java.util.ArrayList;
import java.util.LinkedList;

public class Func {

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static LinkedList<String> initializeAL(int size) {
        LinkedList<String> res = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            res.add("\\");
        }
        return res;
    }

    public static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                // .replace("\'", "\\'")      // <== not necessary
                .replace("\"", "\\\"");
    }
}
