package com.mohammadkz.musicbox;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadata;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import com.mohammadkz.musicbox.Model.ActionPlaying;
import com.mohammadkz.musicbox.Model.Music;

public class MusicService extends Service {

    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREVIOUS";
    public static final String ACTION_CLOSE = "CLOSE";

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

        // if the service crashed
        if (intent == null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
            stopSelf();
            return START_STICKY;
        }

        Log.e("SERVICE_STARTUP", "onStart");

        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);

        String action = intent.getStringExtra("action");
        Log.e("action", action + "");
        if (action != null) {
            setInfo();
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
                case ACTION_CLOSE:
                    actionPlaying.close();
                    break;
            }
        }


        return START_NOT_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    public void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }

    private MediaSessionCompat.Callback mediaSessionCompatCallBack = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            actionPlaying.play();
        }

        @Override
        public void onPause() {
            super.onPause();
            actionPlaying.play();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            actionPlaying.next();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            actionPlaying.prev();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            String intentAction = mediaButtonEvent.getAction();

            if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (event != null) {
                    int action = event.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        switch (event.getKeyCode()) {
                            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                                // code for fast forward
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                // code for next
                                actionPlaying.next();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                                // code for play/pause
                                actionPlaying.play();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                // code for previous
                                actionPlaying.prev();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_STOP:
                                // code for stop
                                actionPlaying.close();
                                return true;

                        }
                        setInfo();
                        return false;
                    }
                    if (action == KeyEvent.ACTION_UP) {
                    }


                }
            }
            return super.onMediaButtonEvent(mediaButtonEvent);
        }
    };

    private MediaSessionCompat mediaSessionCompat;

    @Override
    public void onCreate() {
        Log.e("SERVICE", "onCreate");

        mediaSessionCompat = new MediaSessionCompat(this, "MEDIA");
        mediaSessionCompat.setCallback(mediaSessionCompatCallBack);
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSessionCompat.setPlaybackState(mStateBuilder.build());
        mediaSessionCompat.setActive(true);
    }

    @Override
    public void onDestroy() {
        Log.e("SERVICE", "onDestroy");
        try {
            actionPlaying.close();
            mediaSessionCompatCallBack.onPause();
        } catch (NullPointerException e) {
            e.getMessage();
        }

        mediaSessionCompat.release();
    }

    public void setInfo() {
        try {
            mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                    // Title.
                    .putString(MediaMetadata.METADATA_KEY_TITLE, actionPlaying.music().getName())
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, actionPlaying.music().getArtist())
                    .putString(MediaMetadata.METADATA_KEY_ALBUM, actionPlaying.music().getAlbum())
                    .build());
        } catch (NullPointerException e) {
            e.getMessage();
            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
}
