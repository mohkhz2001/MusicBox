package com.mohammadkz.musicbox;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mohammadkz.musicbox.Model.ActionPlaying;

public class MusicService extends Service {

    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREVIOUS";

    private IBinder mBinder = new MyBinder();

    ActionPlaying actionPlaying;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");
        Log.e("action" , action + "");
        if (action != null)
            switch (action) {
                case ACTION_PLAY:
                    actionPlaying.play();
                    break;
                case ACTION_NEXT:
                    actionPlaying.next();
                    break;
                case ACTION_PREV:
                    actionPlaying.prev();
                    break;
            }

        return START_STICKY;
    }

    public void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }
}
