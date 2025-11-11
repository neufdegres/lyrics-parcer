package com.vickydegres.lyricsparser.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class LyricsTest {

    @Test
    public void stringToLyricsTest() {
        String s = "くだらねえ いつになりゃ終わる?\n" +
                "なんか死にてえ気持ちで ブラブラブラ\n" +
                "残念 手前じゃ所在ねえ\n" +
                "アジャラカモクレン テケレッツのパー\n\n" +
                "うぜえ じゃらくれたタコが\n" +
                "やってらんねえ 与太吹きブラブラブラ\n" +
                "悪銭 抱えどこへ行く\n" +
                "アジャラカモクレン テケレッツのパー";

        Lyrics l = Lyrics.stringToLyrics(s);
        String ss = l.toString();

        assertEquals(s, ss);
    }

}