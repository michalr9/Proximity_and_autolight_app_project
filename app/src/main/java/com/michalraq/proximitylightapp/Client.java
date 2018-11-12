package com.michalraq.proximitylightapp;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Service {

    private Socket socket;
    DataInputStream in;
    DataOutputStream out;
    InetAddress serverAddr;
    String connectedWithServerInfo;

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

                        receiveMessage();
                        showToastInIntentService(connectedWithServerInfo);

                        startWorking();

                } catch (UnknownHostException e) {
                    showToastInIntentService("Podano zły adres hosta!");
                    closeSocket();

                } catch (IOException e) {
                    showToastInIntentService("Wystąpił błąd podczas próby nawiazania połączenia!");
                    closeSocket();
                    stopSelf();
                }
            }
        });

        thread.start();

        return START_STICKY;

    }

    private void startWorking(){

        if (socket.isConnected()) {
         //   String wiadomoscodebrana = "wysylam wiadomosc";
         //   byte[] wiadomosc = wiadomoscodebrana.getBytes();
            try {
                do {
                    if (socket != null) {

                        out = new DataOutputStream(socket.getOutputStream());
                        out.flush();
               //         out.write(wiadomosc);
                    }
                } while (ServerManager.isServiceStarted);

                out.close();
                socket.close();
                showToastInIntentService("Połączenie zamknięte.");

            } catch (IOException e) {
                showToastInIntentService("Wystąpił błąd podczas działania usługi!");
            }

        }
        else{
            showToastInIntentService("Spróbuj połączyć się ponownie.");
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

    private void receiveMessage(){
        byte[] data=null;
        try {
        in = new DataInputStream(socket.getInputStream());
        data = new byte[ 256 ];
            in.read(data);
        } catch (IOException e) {
            Log.e("Client","Error data null");
        }
        if(data!=null) {
            connectedWithServerInfo = new String(data).trim();
        }else
        {
            Log.e("Client","Error data null");
        }
    }


    @Override
    public void onDestroy() {

    closeSocket();
    showToastInIntentService("Usługa zatrzymana.");

        super.onDestroy();
    }

    private void closeSocket(){
        try {
            if (socket != null ) socket.close();
            if(out != null) out.close();
            if(in != null) in.close();



        } catch (IOException ioException) {
        Log.e("Client","Error zamkniecie socket'a");
        }
    }

}
