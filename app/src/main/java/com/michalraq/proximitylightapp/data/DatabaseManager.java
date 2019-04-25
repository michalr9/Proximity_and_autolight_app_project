package com.michalraq.proximitylightapp.data;

import android.content.Context;
import android.util.Log;
import com.michalraq.proximitylightapp.Util.*;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private final String TAG = "DataBaseManager";
     private String url ;
     private Connection connection;
    /**
     *Konstruktor w którym następuje inicjalizacja zmiennych. Danymi do połączenia z bazą danych.
     * @param context
     */
     DatabaseManager(Context context) {
         String hostName="",dbName="",user="",password="";
         try {
         hostName =  FileGetter.getProperty("host_name",context);
         dbName = FileGetter.getProperty("database_name",context);
         user = FileGetter.getProperty("user",context);
         password = FileGetter.getProperty("password",context);


            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG,"Missing class lib");
        } catch (IOException e) {
             e.printStackTrace();
             Log.e(TAG,"Reading from file failed!!!");
         }
         if(!hostName.isEmpty() && !dbName.isEmpty() && !user.isEmpty() && !password.isEmpty()) {
             url = String.format("jdbc:jtds:sqlserver://%s:1433/" +
                             "%s" +
                             ";user=%s" +
                             ";password=%s;",
                     hostName,
                     dbName,
                     user,
                     password);
         }
         else
         {
             Log.e(TAG,"Empty values for database url link.");
         }
    }

    /**
     * Funkcja odpowiedzialna za połączenie z bazą danych.
     * @return True jeżeli połączenie zakończy się sukcesem.
     */
    public Boolean connectDatabase(){
        try {
            connection = DriverManager.getConnection(url);
            Log.d(TAG,"Connection with database established " + connection);
            return true;
        }
        catch (Exception e) {
            Log.e(TAG,"Error occured during attempt to connect to database!");
            if(connection!=null)
                disconnectDatabase();
            return false;
        }
    }

    /**
     * Rozłączenie z bazą danych.
     * @return True jeżeli rozłączenie się powiedzie.
     */
    public Boolean disconnectDatabase(){
        try {
            if(connection!=null)
                connection.close();
            Log.d(TAG,"Connection with database closed");
        } catch (SQLException e) {
            Log.e(TAG,"Error occured during disconnect to database!");
            return false;
        }
        return true;
    }

    /**
     * Funkcja sprawdzająca czy w danym pomieszczeniu jest włączone światło.
     * @param place Nazwa pomieszczenia.
     * @return True jeżeli jest zapalone światło.
     */
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
                Log.e(TAG,"Error checkStatusOfLight during checking for records.");
                e.printStackTrace();
            }
        } else {
            Log.e("DataBaseManager","checkStatusOfLight has failed");
        }
        return status;
    }

    public void checkTimeWork(){
        LightContent.summaryOfTimeLightOn = new HashMap<>();
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
                LightContent.summaryOfTimeLightOn.put("biuro", rs.getLong("SUM_TIME_OF_LIGHT_ON"));
                 rs = stm.executeQuery(SQL2);
                rs.next();
                LightContent.summaryOfTimeLightOn.put("salon", rs.getLong("SUM_TIME_OF_LIGHT_ON"));
                 rs = stm.executeQuery(SQL3);
                rs.next();
                LightContent.summaryOfTimeLightOn.put("kuchnia", rs.getLong("SUM_TIME_OF_LIGHT_ON"));
            } catch (SQLException e) {
                Log.e(TAG,"Error checkTimeWork during checking for records.");
                e.printStackTrace();
            }
        } else {
            Log.e(TAG,"checkTimeWork has failed");
        }
    }

    /**
     * Funkcja sprawdzająca czas pracy włączonego światła w każdym z pomieszczeń.
     * @param startDate Data początkowa okresu rozliczeniowego.
     * @param endDate Data końcowa okresu rozliczeniowego.
     */
    public void checkTimeWork(String startDate,String endDate){
        LightContent.summaryOfTimeLightOn = new HashMap<>();
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
                LightContent.summaryOfTimeLightOn.put("biuro", rs.getLong(1));
                Log.d(TAG,"Biuro "+rs.getLong(1));

                rs = stm.executeQuery(SQL2);
                rs.next();
                LightContent.summaryOfTimeLightOn.put("salon", rs.getLong(1));
                Log.d(TAG,"Salon "+rs.getLong(1));

                rs = stm.executeQuery(SQL3);
                rs.next();
                LightContent.summaryOfTimeLightOn.put("kuchnia", rs.getLong(1));
                Log.d(TAG,"Kuchnia "+rs.getLong(1));


            } catch (SQLException e) {
                Log.e(TAG,"Error checkTimeWork during checking for records.");
                e.printStackTrace();
            }
        } else {
            Log.e(TAG,"checkTimeWork has failed");
        }
    }

    /**
     * Funkcja pobierające dane związane ze szczegółowym czasem wejścia i opuszczenia pomieszczenia.
     * @param place Nazwa pomieszczenia
     * @return Dane dla każdego z pomieszczeń.
     */
    public Map<Integer,String> getCheckoutData(String place) {
        Map<Integer, String> result = new HashMap<>();
        place=StringOperations.addSingleQuotes(place);
        String checkoutData;
        int id;
        String SQL = "select ID_STATUS, TIME_IN, TIME_OUT from CMT_STATUS\n" +
                "where CMT_PLACES_PLACE = "+place+" AND TIME_OUT IS NOT NULL\n" +
                "order by TIME_IN asc ;";
        if (connection != null) {
            try (Statement stm = connection.createStatement()) {
                ResultSet rs = stm.executeQuery(SQL);
                while (rs.next()) {
                    StringBuilder builder = new StringBuilder("-->" + rs.getString("TIME_IN"));
                    builder.append('\n').append("<--").append(rs.getString("TIME_OUT"));
                    checkoutData = builder.toString();
                    id = rs.getInt("ID_STATUS");

                    result.put(id, checkoutData);
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error during fetching checkout data");
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Connection is NULL in getCheckoutData");
        }
        return result;

    }

    /**
     * Funkcja pobierająca nazwy pomieszczeń zapisane w bazie danych.
     * @return
     */
    public Map<Integer,String> getPlaces() {
        Map<Integer, String> result = new HashMap<>();

        String SQL = "select UPPER(PLACE) from CMT_PLACES order by PLACE asc;";
        if (connection != null) {
            try (Statement stm = connection.createStatement()) {
                ResultSet rs = stm.executeQuery(SQL);
                int i=0;
                while (rs.next()) {
                    i++;
                    String place =rs.getString(1);
                    result.put(i, place);
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error during getting places names.");
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Connection is NULL in getPlaces function.");
        }
        return result;
    }

    public Connection getConnection() {
        return connection;
    }

}