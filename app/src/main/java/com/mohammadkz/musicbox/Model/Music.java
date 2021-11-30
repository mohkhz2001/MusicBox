package com.mohammadkz.musicbox.Model;

public class Music {
    private String Path;
    private String Name;
    private String Album;
    private String Artist;
    private String Duration;
    private long dateAdded;
    private boolean Liked;
    private boolean selected;

    public Music() {
        Liked = false;
        selected = false;
    }

    public Music(String path, String name, String album, String artist, String duration, long dateAdded) {
        Path = path;
        Name = name;
        Album = album;
        Artist = artist;
        Duration = duration;
        Liked = false;
        selected = false;
        this.dateAdded = dateAdded;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
