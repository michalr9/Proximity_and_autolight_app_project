package com.michalraq.proximitylightapp;

import java.util.Map;

public class StateOfLight {
    public static Map<String,Long> summaryOfTimeLightOn ;


    public static Long getValueInSec(String place){
       return summaryOfTimeLightOn.get(place);
    }
    public static Long getValueInMin(String place){
        if(summaryOfTimeLightOn.get(place)!=null)
          return   summaryOfTimeLightOn.get(place)%60;
        return (long)0;

    }
    public static Long getValueInHours(String place){
        if(summaryOfTimeLightOn.get(place)!=null)
        return (summaryOfTimeLightOn.get(place)%3600);
        return (long)0;

    }
    public static Long getValueInMilliseconds(Object key){
        return (summaryOfTimeLightOn.get(key)*1000);
    }
}
