package com.michalraq.proximitylightapp.data;

import java.util.Map;

/**
 * Klasa odpowiadająca za przechowywanie statusu świateł w pomieszczeniach.
 */
 class LightContent {
     static Map<String,Long> summaryOfTimeLightOn ;

    /**
     * Zwraca wartość w sekundach dla włączonego światła.
     * @param place Nazwa pomieszczenia.
     * @return
     */
     static Long getValueInSec(String place){
       return summaryOfTimeLightOn.get(place);
    }
}
