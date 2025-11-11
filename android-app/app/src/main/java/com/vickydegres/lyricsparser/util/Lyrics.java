package com.vickydegres.lyricsparser.util;

import java.util.Arrays;
import java.util.LinkedList;

public class Lyrics {
    private final LinkedList<String> lines;

    public Lyrics() {
        lines = new LinkedList<>();
    }

    public Lyrics(LinkedList<String> l) {
        this.lines =  l;
        // removeBlankLines();
    }

    public LinkedList<String> getLines() {
        return lines;
    }

    public int getLinesCount() {
        return lines.size();
    }

    public static Lyrics stringToLyrics(String raw) {
        String[] tab = raw.split("\n");
        return new Lyrics(new LinkedList<>(Arrays.asList(tab)));
    }

    public boolean isEmpty() {
        for(String line : lines) {
            if (!(line.length() == 0 || line.equals("\\") || Func.isBlank(line))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String res = "";
        for(String line : lines) {
            res += line + "\n";
        }
        return res.substring(0,res.length()-1);
    }

    /*private void removeBlankLines() {
        int i = 0;
        while(i < lines.size()) {
            if (lines.get(i).isEmpty()) lines.remove(i);
            else i++;
        }
    }*/
}
