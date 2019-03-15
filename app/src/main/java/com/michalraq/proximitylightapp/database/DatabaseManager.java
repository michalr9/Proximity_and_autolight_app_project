package com.michalraq.proximitylightapp.database;

import android.content.Context;
import android.util.Log;

import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.StateOfLight;
import com.michalraq.proximitylightapp.Util.*;

import java.sql.*;
import java.util.HashMap;

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

    public void checkTimeWork(){
        StateOfLight.summaryOfTimeLightOn = new HashMap<>();
        String SQL1 = "select CMT_PLACES.SUM_TIME_OF_LIGHT_ON from CMT_PLACES\n" +
                "where PLACE = 'biuro'";
        String SQL2 = "select CMT_PLACES.SUM_TIME_OF_LIGHT_ON from CMT_PLACES\n" +
                "where PLACE = 'salon'";
        String SQL3 = "select CMT_PLACES.SUM_TIME_OF_LIGHT_ON from CMT_PLACES\n" +
                "where PLACE = 'kuchnia'";
        if (connection != null) {
            try (Statement stm = connection.createStatement()) {
                ResultSet rs = stm.executeQuery(SQL1);
                rs.next();
                StateOfLight.summaryOfTimeLightOn.put("biuro", rs.getLong("SUM_TIME_OF_LIGHT_ON"));
                 rs = stm.executeQuery(SQL2);
                rs.next();
                StateOfLight.summaryOfTimeLightOn.put("salon", rs.getLong("SUM_TIME_OF_LIGHT_ON"));
                 rs = stm.executeQuery(SQL3);
                rs.next();
                StateOfLight.summaryOfTimeLightOn.put("kuchnia", rs.getLong("SUM_TIME_OF_LIGHT_ON"));
            } catch (SQLException e) {
                System.err.println("Blad podczas sprawdzenia czy istnieje rekord");
                e.printStackTrace();
            }
        } else {
            Log.e("DataBaseManager","checkTimeWork has failed");
        }
    }

    public void checkTimeWork(String startDate,String endDate){
        StateOfLight.summaryOfTimeLightOn = new HashMap<>();
        String stDate = StringOperations.makeTimeIn(startDate);
        String edDate = StringOperations.makeTimeOut(endDate);
        String SQL1 = "select case when SUM( datediff(second,TIME_IN,TIME_OUT)) IS NULL then 0\n" +
                "else SUM( datediff(second,TIME_IN,TIME_OUT))\n" +
                "END\n" +
                "from CMT_STATUS\n" +
                "where CMT_PLACES_PLACE='biuro' AND (TIME_IN >= "+stDate+" AND TIME_OUT <= "+edDate+")";
        String SQL2 = "select case when SUM( datediff(second,TIME_IN,TIME_OUT)) IS NULL then 0\n" +
                "else SUM( datediff(second,TIME_IN,TIME_OUT))\n" +
                "END\n" +
                "from CMT_STATUS\n" +
                "where CMT_PLACES_PLACE='salon' AND (TIME_IN >= "+stDate+" AND TIME_OUT <= "+edDate+")";
        String SQL3 = "select case when SUM( datediff(second,TIME_IN,TIME_OUT)) IS NULL then 0\n" +
                "else SUM( datediff(second,TIME_IN,TIME_OUT))\n" +
                "END\n" +
                "from CMT_STATUS\n" +
                "where CMT_PLACES_PLACE='kuchnia' AND (TIME_IN >= "+stDate+" AND TIME_OUT <= "+edDate+")";
        if (connection != null) {
            try (Statement stm = connection.createStatement()) {
                ResultSet rs = stm.executeQuery(SQL1);
                rs.next();
                StateOfLight.summaryOfTimeLightOn.put("biuro", rs.getLong(1));
                Log.d("DatabaseManager","Biuro "+rs.getLong(1));

                rs = stm.executeQuery(SQL2);
                rs.next();
                StateOfLight.summaryOfTimeLightOn.put("salon", rs.getLong(1));
                Log.d("DatabaseManager","Salon "+rs.getLong(1));

                rs = stm.executeQuery(SQL3);
                rs.next();
                StateOfLight.summaryOfTimeLightOn.put("kuchnia", rs.getLong(1));
                Log.d("DatabaseManager","Kuchnia "+rs.getLong(1));


            } catch (SQLException e) {
                System.err.println("Blad podczas sprawdzenia czy istnieje rekord");
                e.printStackTrace();
            }
        } else {
            Log.e("DataBaseManager","checkTimeWork has failed");
        }
    }

    public Connection getConnection() {
        return connection;
    }
}