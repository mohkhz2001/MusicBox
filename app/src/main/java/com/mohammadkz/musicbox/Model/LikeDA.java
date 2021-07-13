package com.mohammadkz.musicbox.Model;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LikeDA {
    private DB sqliteDB;
    private SQLiteDatabase database;

    public static final String path = "Path";

    public LikeDA(Activity context) {
        sqliteDB = new DB(context);
    }

    public void openDB() {
        database = sqliteDB.getWritableDatabase();
    }

    public void closeDB() {
        database.close();
    }

    public boolean newMusicLiked(Music music) {
        try {

            database.execSQL("INSERT INTO 'like' VALUES ('" + music.getPath() + "' , '" + music.getName() + "' )");
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }

    public List<Music> getAllLiked(ArrayList<Music> musicList) {
        try {
            Cursor cursor = database.rawQuery("SELECT Path FROM like", null);

            ArrayList<Music> musicArrayList = new ArrayList<>();


            if (cursor.moveToFirst()) {
                do {

                    String Path = cursor.getString(cursor.getColumnIndex(path));
                    Music music = new Music();
                    music.setPath(Uri.parse(Path));

                    for (int i = 0; i < musicList.size(); i++) {
                        if (musicList.get(i).getPath().toString().equals(Path)) {
                            musicArrayList.add(musicList.get(i));
                        }
                    }

                } while (cursor.moveToNext());
            }

            // sort the music list (A-Z)
            Collections.sort(musicArrayList, new Comparator<Music>() {
                @Override
                public int compare(Music music, Music music1) {

                    return music.getName().compareTo(music1.getName());
                }
            });

            Log.i("size", " " + musicArrayList.size());
            return musicArrayList;

        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public boolean removeMusicLiked(String path) {
        try {

            database.execSQL("DELETE FROM like  WHERE path ='" + path + "'");
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }

}
