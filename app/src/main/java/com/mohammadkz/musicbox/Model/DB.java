package com.mohammadkz.musicbox.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

class DB extends SQLiteOpenHelper {

    public static final int DB_VERSION = 5;

    public DB(Context context) {
        super(context, "like", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql_like = "create table like ( Path Text , musicName Text )";
        db.execSQL(sql_like);

        String sql_playList = "create table playList (name Text)";
        db.execSQL(sql_playList);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS like");
        db.execSQL("DROP TABLE IF EXISTS playList");

        onCreate(db);
    }
}