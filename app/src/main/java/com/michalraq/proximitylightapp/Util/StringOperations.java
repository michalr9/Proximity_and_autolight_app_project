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

}
