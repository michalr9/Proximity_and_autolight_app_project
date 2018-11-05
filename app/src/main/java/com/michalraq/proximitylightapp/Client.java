package com.michalraq.proximitylightapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
        //nawiazanie polaczenia
        try {
            serverAddr = InetAddress.getByName(ServerContent.SERVER_IP);
            socket = new Socket(serverAddr,ServerContent.PORT_NUMBER);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Polaczono");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try{

            //obsluga
            if (!socket.isClosed()){


               // String wiadomoscodebrana = etContent.getText().toString();
             //   byte[] wiadomosc = wiadomoscodebrana.getBytes();

                    out = new DataOutputStream(socket.getOutputStream());
                    out.flush();

                //    out.write(wiadomosc);


                }
                else{
                Toast.makeText(getApplicationContext(),"Połączenie zamknięte.",Toast.LENGTH_LONG);
            }

        }
         catch (IOException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {

        try {
            out.close();
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        System.out.println("Wszystko zamkniete");
        super.onDestroy();
    }

}
