package com.michalraq.proximitylightapp.Util;

/**
 * Klasa narzędziowa do operacji ze zmiennymi typu String.
 */
public class StringOperations {
    /**
     * Metoda dodająca pojedyncze cudzysłowie do @param
     * @param word przekazane słowo.
     * @return
     */
    public static String addSingleQuotes(String word){
        StringBuilder build = new StringBuilder("'");
        build.append(word).append("'");
        word=build.toString();
        return word;
    }

    /**
     * Metoda dodająca czas do przekazanej daty
     * @param date
     * @return
     */
    public static String makeTimeIn(String date) {
        StringBuilder build = new StringBuilder("'");
        build.append(date).append(" 00:00:00").append("'");
        date=build.toString();
        return date;
    }

    /**
     * Metoda dodająca czas do przekazanej daty
     * @param date
     * @return
     */
    public static String makeTimeOut(String date) {
        StringBuilder build = new StringBuilder("'");
        build.append(date).append(" 23:59:59").append("'");
        date=build.toString();
        return date;
    }


}
