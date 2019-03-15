package com.michalraq.proximitylightapp.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringOperations {

    public static String addSingleQuotes(String word){
        StringBuilder build = new StringBuilder("'");
        build.append(word).append("'");
        word=build.toString();
        return word;
    }

    public static String makeTimeIn(String date) {
        StringBuilder build = new StringBuilder("'");
        build.append(date).append(" 00:00:00").append("'");
        date=build.toString();
        return date;
    }
    public static String makeTimeOut(String date) {
        StringBuilder build = new StringBuilder("'");
        build.append(date).append(" 23:59:59").append("'");
        date=build.toString();
        return date;
    }


}
