package com.michalraq.proximitylightapp.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.michalraq.proximitylightapp.Views.ServerManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Service {

    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static PrintWriter printWriter;
    InetAddress serverAddr;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

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
                    closeSocket();

                } catch (IOException e) {
                    showToastInIntentService("Wystąpił błąd podczas połączenia!");
                    sendBroadcast(true,"fail");
                    closeSocket();
                    stopSelf();
                }
            }
        });

        thread.start();

        return START_STICKY;

    }


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

    private void sendBroadcast (Boolean status,String name){
        switch (name) {
            case "fail":
                Intent intent = new Intent ("message");
                intent.putExtra(name, status);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;
        }

    }

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


    @Override
    public void onDestroy() {

        ServerManager.isServiceStarted = false;
        closeSocket();
        showToastInIntentService("Usługa zatrzymana.");
        super.onDestroy();
    }

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