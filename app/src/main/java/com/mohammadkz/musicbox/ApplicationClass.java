package com.mohammadkz.musicbox;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ApplicationClass extends Application {

    public static final String CHANNEL_ID_1 = "CHANNEL_1";
    public static final String CHANNEL_ID_2 = "CHANNEL_2";
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREVIOUS";
    public static final String ACTION_CLOSE = "CLOSE";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // channel 1
            NotificationChannel notificationChannel_1 = new NotificationChannel(CHANNEL_ID_1, "channel1(1)", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel_1.setDescription("channel 1 ");

            // channel 2
            NotificationChannel notificationChannel_2 = new NotificationChannel(CHANNEL_ID_2, "channel2(2)", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel_2.setDescription("channel 2 ");
            notificationChannel_2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // create notif manager
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel_1);
            notificationManager.createNotificationChannel(notificationChannel_2);

        }

    }
}
