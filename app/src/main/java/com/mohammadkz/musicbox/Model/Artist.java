package com.mohammadkz.musicbox.Model;

import java.util.ArrayList;

public class Artist {
    String artistName;
    ArrayList<Music> artistMusic;

    public Artist(String artistName, ArrayList<Music> artistMusic) {
        this.artistName = artistName;
        this.artistMusic = artistMusic;
    }

    public Artist() {

    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public ArrayList<Music> getArtistMusic() {
        return artistMusic;
    }

    public void setArtistMusic(ArrayList<Music> artistMusic) {
        this.artistMusic = artistMusic;
    }
}
