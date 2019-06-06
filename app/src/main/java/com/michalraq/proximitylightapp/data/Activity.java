package com.michalraq.proximitylightapp.data;

public class Activity {

public static boolean activityBiuro = false;
public static boolean activityKuchnia = false;
public static boolean activitySalon = false;


public static final String EAT = "Jedzenie";
public static final String COOK = "Gotowanie";
public static final String WORK = "Praca przy komputerze";
public static final String WATCHING_TV = "Oglądanie telewizji";
public static final String WORKOUT = "Ćwiczenia";
public static final String RUN = "Przemieszczanie się";

public static double EATX = 7.0507 ;
public static double EATY = -3.7522 ;
public static double EATZ = -1.5299 ;

    public static double COOKX = 0.5347 ;
    public static double COOKY = -9.4463 ;
    public static double COOKZ = 1.0928 ;

    public static double WORKX = 6.2528 ;
    public static double WORKY = -0.2588 ;
    public static double WORKZ = -2.0566 ;

    public static double WATCHING_TVX = 8.1962 ;
    public static double WATCHING_TVY = -2.3206 ;
    public static double WATCHING_TVZ = 0.3778 ;

    public static double WORKOUTX = 0.0098 ;
    public static double WORKOUTY = -9.6568 ;
    public static double WORKOUTZ = 0.8430 ;

    public static double RUNY = 2.3795 ;
    public static double RUNZ = -8.8632 ;
    public static double RUNX = 1.4539 ;

    public static double makeIntervalMinus(double v){
        v-=0.5;
        return v;
    }
    public static double makeIntervalPlus(double v){
        v+=0.5;
        return v;
    }
}
