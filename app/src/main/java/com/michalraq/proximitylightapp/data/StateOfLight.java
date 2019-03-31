package com.michalraq.proximitylightapp.data;

import java.util.Map;

/**
 * Klasa odpowiadająca za przechowywanie statusu świateł w pomieszczeniach.
 */
public class StateOfLight {
    public static Map<String,Long> summaryOfTimeLightOn ;

    /**
     * Zwraca wartość w sekundach dla włączonego światła.
     * @param place Nazwa pomieszczenia.
     * @return
     */
    public static Long getValueInSec(String place){
       return summaryOfTimeLightOn.get(place);
    }
}
