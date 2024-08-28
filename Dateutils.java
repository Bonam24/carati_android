package com.example.carati;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Dateutils {
    //function to get current date and time
    public static LocalDateTime getCurrentDateTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now();
        }
        return null;
    }
    //function to change the date and time to the format 'yyyy-mm-dd hh:mm:ss
    public static LocalDateTime parseDateTime(String dateString) {
        DateTimeFormatter dtf = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateString, dtf);
        }
        return null;
    }
    //function to calculate the difference of the time in seconds
    public static long calculateDifferenceInSeconds(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long secondsBetween = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            secondsBetween = ChronoUnit.SECONDS.between(startDateTime, endDateTime);
        }
        return secondsBetween;
    }
    //function to add seconds to the current date time
    public static LocalDateTime addSeconds(LocalDateTime dateTime, int seconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return dateTime.plusSeconds(seconds);
        }
        return  null;
    }
    //formate date and time to a string
    public static String formatDateTime(LocalDateTime dateTime) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTime.format(dtf);
        }
        return  null;
    }
}
