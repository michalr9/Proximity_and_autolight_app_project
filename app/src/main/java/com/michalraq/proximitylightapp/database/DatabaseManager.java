package com.michalraq.proximitylightapp.database;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.Util.*;

import java.sql.*;
public class DatabaseManager {

     private String url ;
     private Connection connection;

    public DatabaseManager(Context context) {
        String hostName =   context.getResources().getString(R.string.server);
        String dbName = "Proximity";
        String user = context.getResources().getString(R.string.user);
        String password = context.getResources().getString(R.string.pass);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        url = String.format("jdbc:jtds:sqlserver://%s:1433/" +
                            "%s" +
                            ";user=%s" +
                            ";password=%s;",
                    hostName,
                    dbName,
                    user,
                    password);
    }
//TODO zmienic przechowywanie hasla i danych do bazy pobrac odpowiedni driver
    public Boolean connectDatabase(){
        try {
            connection = DriverManager.getConnection(url);
            Log.d("DataBaseManager","Connection with database established " + connection);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error occured during attempt to connect to database!");
            if(connection!=null)
                disconnectDatabase();
            return false;
        }
    }

    public Boolean disconnectDatabase(){
        try {
            if(connection!=null)
                connection.close();
            Log.d("DataBaseManager","Connection with database closed");
        } catch (SQLException e) {
            System.err.println("Error occured during disconnect to database!");
            return false;
        }
        return true;
    }

    public Boolean checkStatusOfLight(String place){
        place = StringOperations.addSingleQuotes(place);
        Boolean status=false;
        String SQL = "select CMT_PLACES.POWER_ON from CMT_PLACES\n" +
                "where PLACE =" + place;
        if (connection != null) {
            try (Statement stm = connection.createStatement()) {
                ResultSet rs = stm.executeQuery(SQL);
                rs.next();
                status = rs.getBoolean("POWER_ON");
            } catch (SQLException e) {
                System.err.println("Blad podczas sprawdzenia czy istnieje rekord");
                e.printStackTrace();
            }
        } else {
            Log.e("DataBaseManager","Connection is NULL");
        }
        return status;
    }


    public Connection getConnection() {
        return connection;
    }
}