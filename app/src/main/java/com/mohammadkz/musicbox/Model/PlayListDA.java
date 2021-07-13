package com.mohammadkz.musicbox.Model;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class PlayListDA {
    private DB sqliteDB;
    private SQLiteDatabase database;

    public PlayListDA(Activity context) {
        sqliteDB = new DB(context);
    }

    public void openDB() {
        database = sqliteDB.getWritableDatabase();
    }

    public void closeDB() {
        database.close();
    }

    public boolean newPlayList(String name) {
        try {
            database.execSQL("insert into 'playList' values('" + name + "') ");
            database.execSQL("create table `" + name + "` ( path Text , musicName Text ) ");
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean newMusic_playList(List<Music> musics, String name) {

        try {
            for (int i = 0; i < musics.size(); i++) {
                database.execSQL("insert into `" + name + "` values( '" + musics.get(i).getPath() + "' ,  '" + musics.get(i).getName() + "' )");
            }
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }

    }

    public List<Music> getMusic(String name) {

        try {

            String sql = "SELECT * FROM `" + name + "`";
            Cursor cursor = database.rawQuery(sql, null);

            ArrayList<Music> musics = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {

                    String Path = cursor.getString(cursor.getColumnIndex("path"));
                    String musicName = cursor.getString(cursor.getColumnIndex("musicName"));

                    Music music = new Music();
                    music.setPath(Uri.parse(Path));
                    music.setName(musicName);

                    musics.add(music);

                } while (cursor.moveToNext());
            }

            return musics;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public List<PlayList> getPlayList() {
        try {

            Cursor cursor = database.rawQuery("SELECT `name` FROM playList", null);

            ArrayList<PlayList> playLists = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {

                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    PlayList playList = new PlayList();

                    playList.setMusicList(getMusic(name));

                    playList.setName(name);
                    playLists.add(playList);
                } while (cursor.moveToNext());
            }

            return playLists;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }

    }

    public boolean deletePlayList(PlayList playList) {
        try {
            database.execSQL("drop table `" + playList.getName() + "`");
            database.execSQL("delete from playList where name = '" + playList.getName() + "' ");
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }

}
