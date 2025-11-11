package com.vickydegres.lyricsparser.util;

import androidx.annotation.NonNull;

public class Language {
    private final String code;
    private final String name;
    private final int flag;

    public Language(String code, String name, int flag) {
        this.code = code;
        this.name = name;
        this.flag = flag;
    }

    public Language(String code) {
        switch(code.toLowerCase()) {
            case "fr" :
                this.code = "FR";
                this.name = "français";
                this.flag = Flag.FR.getId();
                break;
            case "en" :
                this.code = "EN";
                this.name = "english";
                this.flag = Flag.EN.getId();
                break;
            default :
                this.code = "JP";
                this.name = "日本語";
                this.flag = Flag.JP.getId();
                break;
        }
    }

    public Language() { // tmp : JP
        this.code = "JP";
        this.name = "日本語";
        this.flag = Flag.JP.getId();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getFlag() {
        return flag;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
