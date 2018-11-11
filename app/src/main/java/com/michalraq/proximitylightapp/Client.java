package com.michalraq.proximitylightapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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

        try{
            //nawiazanie polaczenia
            Log.d("Client",ServerContent.SERVER_IP +" - "+ ServerContent.PORT_NUMBER);

            serverAddr = InetAddress.getByName(ServerContent.SERVER_IP);
            socket = new Socket(serverAddr, ServerContent.PORT_NUMBER);

        //    Toast.makeText(getApplicationContext(), "Połączono!", Toast.LENGTH_LONG);
            Log.d("Client","Połączono");
        }  catch (UnknownHostException e) {
         //   Toast.makeText(getApplicationContext(), "Podano zły adres hosta!", Toast.LENGTH_LONG);
            Log.d("Client","zly host");

        }catch (IOException e) {
           // Toast.makeText(getApplicationContext(), "Wystąpił błąd podczas próby nawiazania połączenia!", Toast.LENGTH_LONG);
            Log.d("Client","blad podczas polaczenia");

        }

                String wiadomoscodebrana = "wysylam wiadomosc";
                byte[] wiadomosc = wiadomoscodebrana.getBytes();
        try {
                do {
                    if(socket!=null) {
                        //obsluga

                        out = new DataOutputStream(socket.getOutputStream());
                        out.flush();

                        out.write(wiadomosc);
                    }
                }while(ServerManager.isServiceStarted);

                    out.close();
                    socket.close();
                  //  Toast.makeText(getApplicationContext(), "Połączenie zamknięte.", Toast.LENGTH_LONG);
            Log.d("Client","Połączonie zamkniete");

            }  catch (IOException e) {
           // Toast.makeText(getApplicationContext(), "Wystąpił błąd podczas działania usługi!", Toast.LENGTH_LONG);
            Log.d("Client","Blad podczas dzialania uslugi");

        }

            }
        });

        thread.start();

        return START_STICKY;

    }

    @Override
    public void onDestroy() {

        try {
            if(!socket.isClosed()) {
                out.close();
                socket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        Log.d("Client","wszystko zamkniete");
        super.onDestroy();
    }

}
