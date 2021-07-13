package com.mohammadkz.musicbox.Model;

import java.util.ArrayList;
import java.util.List;

public class PlayList {
    private String name;
    private List<Music> musicList;

    public PlayList() {
        this.musicList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }
}
