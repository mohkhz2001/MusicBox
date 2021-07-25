package com.mohammadkz.musicbox.Model;

import android.net.Uri;

public class Music {
    private String Path;
    private String Name;
    private String Album;
    private String Artist;
    private String Duration;
    private boolean Liked;

    public Music() {
        Liked = false;
    }

    public Music(String path, String name, String album, String artist, String duration) {
        Path = path;
        Name = name;
        Album = album;
        Artist = artist;
        Duration = duration;
        Liked = false;
    }

    public boolean isLiked() {
        return Liked;
    }

    public void setLiked(boolean liked) {
        Liked = liked;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }
}
