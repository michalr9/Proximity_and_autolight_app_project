package com.michalraq.proximitylightapp.service;

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
import com.michalraq.proximitylightapp.data.ServerContent;
import com.michalraq.proximitylightapp.view.ServiceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocket;
import org.apache.http.conn.ssl.SSLSocketFactory;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static com.michalraq.proximitylightapp.App.NOTIFICATION_CHANNEL;

/**
 * Klasa świadcząca usługę klienta, odpowiedzialna za komunikację z serwerem.
 */
public class Client extends Service {

    private static final String PREFERENCES = "myPreferences";
    private static final String IS_SERVICE_STARTED = "isServiceStarted";
    private SharedPreferences preferences;
    private static SSLSocket socket;
    private static BufferedReader bufferedReader;
    private static PrintWriter printWriter;
    private NotificationManagerCompat notificationManagerCompat;
    PendingIntent pendingIntent;
    String serverAddr;
    SSLSocketFactory sslSocketFactory;
    private char keystorepass[] = "password".toCharArray();


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
     * @return int code
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    serverAddr =ServerContent.SERVER_IP;

                    KeyStore ks = KeyStore.getInstance("BKS");
                    InputStream keyin = getResources().openRawResource(R.raw.clientcert);
                    ks.load(keyin,keystorepass);
                    sslSocketFactory = new SSLSocketFactory(ks);
                    sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); //pozwala na uzycie  wygenerowanego certyfikatu
                    socket = (SSLSocket) sslSocketFactory.createSocket(new Socket(serverAddr,ServerContent.PORT_NUMBER), serverAddr, ServerContent.PORT_NUMBER, false);
                    socket.startHandshake();

                    ServiceManager.isServiceStarted = true;

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
                } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException | UnrecoverableKeyException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        return START_STICKY;
    }

    /**
     * Metoda odpowiedzialna za wysyłanie wiadomości do serwera.
     * @param message wiadomość do wysyłki
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
     * @param status status powodzenia
     * @param name nazwa błędu
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
     * @param text  wiadomość
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
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(285 ,500,5000)
                .setAutoCancel(true).build();
        notificationManagerCompat.notify(1,notification);
    }

    /**
     * Stworzenie intencji uruchamianej po kliknięciu w notyfikacje przez użytkownika
     */
    void createNotificationActionIntent(){
        Intent serverIntent = new Intent(this, ServiceManager.class);
        serverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         pendingIntent = PendingIntent.getActivity(this, 0, serverIntent, 0);
    }

    /**
     * Metoda służąca do zachowywania stanu usługi w pamięci.
     * @param isServiceStarted zmienna mówiąca o uruchomionym serwisie - true-tak false -nie
     */
    private void saveState(Boolean isServiceStarted){
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        preferencesEditor.putBoolean(IS_SERVICE_STARTED,isServiceStarted);
        preferencesEditor.apply();
    }


    @Override
    public void onDestroy() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                closeSocket();
            }
        });
        thread.start();
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
