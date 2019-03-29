package com.michalraq.proximitylightapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class App extends Application {
    public final static String NOTIFICATION_CHANNEL = "channel_1";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    /**
     * Funkcja tworząca wymagany channel notyfikacji w wypadku gdyby bylo ich wiecej
     */
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    "channel_1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setLightColor(Color.YELLOW);
            notificationChannel.enableLights(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
