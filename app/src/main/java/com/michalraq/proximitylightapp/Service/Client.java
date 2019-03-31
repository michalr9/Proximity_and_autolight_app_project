package com.michalraq.proximitylightapp.Service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.Views.ServerManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static com.michalraq.proximitylightapp.App.NOTIFICATION_CHANNEL;

/**
 * Klasa świadcząca usługę klienta, odpowiedzialna za komunikację z serwerem.
 */
public class Client extends Service {

    private static final String PREFERENCES = "myPreferences";
    private static final String IS_SERVICE_STARTED = "isServiceStarted";
    private SharedPreferences preferences;
    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static PrintWriter printWriter;
    private NotificationManagerCompat notificationManagerCompat;
    PendingIntent pendingIntent;
    InetAddress serverAddr;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        createNotificationActionIntent();
    }

    /**
     * W momencie wywołania metody zostaje uruchomiona usługa i nastepuje połączenie z serwerem.
     * Start_sticky  pozwala na ponowne uruchomienie usługi przez system w wypadku, gdyby zabrakło pamięci urządzeniu w trakcie jej wykonywania.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    serverAddr = InetAddress.getByName(ServerContent.SERVER_IP);
                    socket = new Socket(serverAddr, ServerContent.PORT_NUMBER);

                    ServerManager.isServiceStarted = true;

                    printWriter = new PrintWriter(socket.getOutputStream());
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message;
                    while((message = bufferedReader.readLine())!=null) { //receive message
                        showToastInIntentService(message);
                    }


                } catch (UnknownHostException e) {
                    showToastInIntentService("Podano zły adres hosta!");
                    saveState(false);
                    closeSocket();

                } catch (IOException e) {
                    saveState(false);
                    showToastInIntentService("Wystąpił błąd podczas połączenia!");
                    sendBroadcast(true,"fail");
                    showErrorNotification();
                    closeSocket();
                    stopSelf();
                }
            }
        });

        thread.start();

        return START_STICKY;
    }

    /**
     * Metoda odpowiedzialna za wysyłanie wiadomości do serwera.
     * @param message
     */
    public static void sendMessage(final String message){

        if (socket.isConnected()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                        printWriter.println(message);
                        printWriter.flush();
                }
            });
            thread.start();
        }
    }

    /**
     * Metoda odpowiedzialna za broadcast powiadomień z usługi do systemu, umożliwiając tym samym jej odbiór w innych aktywnościach.
     * @param status
     * @param name
     */
    private void sendBroadcast (Boolean status,String name){
        switch (name) {
            case "fail":
                Intent intent = new Intent ("message");
                intent.putExtra(name, status);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;
        }

    }

    /**
     * Metoda odpowiedzialna za pokazywanie toastów.
     * @param text
     */
    public void showToastInIntentService(final String text) {
        final Context MyContext = this;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast1 = Toast.makeText(MyContext, text, Toast.LENGTH_LONG);
                toast1.show();
            }
        });
    }

    /**
     * Wyswietlenie notyfikacji push o zerwaniu połączenia z serwerem.
     */
    void showErrorNotification(){
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.notification_icon_error)
                .setContentTitle(this.getString(R.string.notification_error))
                .setContentText(this.getString(R.string.notification_error_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();
        notificationManagerCompat.notify(1,notification);
    }

    /**
     * Stworzenie intencji uruchamianej po kliknięciu w notyfikacje przez użytkownika
     */
    void createNotificationActionIntent(){
        Intent serverIntent = new Intent(this, ServerManager.class);
        serverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         pendingIntent = PendingIntent.getActivity(this, 0, serverIntent, 0);
    }

    /**
     * Metoda służąca do zachowywania stanu usługi w pamięci.
     * @param isServiceStarted
     */
    private void saveState(Boolean isServiceStarted){
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        preferencesEditor.putBoolean(IS_SERVICE_STARTED,isServiceStarted);
        preferencesEditor.apply();
    }


    @Override
    public void onDestroy() {

        closeSocket();
        showToastInIntentService("Usługa zatrzymana.");
        super.onDestroy();
    }

    /**
     * Metoda odpowiedzialna za zamknięcie połączenia z serwerem.
     */
    private void closeSocket(){
        try {
            if (socket != null ) socket.close();
            if(printWriter != null) printWriter.close();
            if(bufferedReader != null) bufferedReader.close();
        } catch (IOException ioException) {
        Log.e("Client","Error zamkniecie socket'a");
        }
    }

}
