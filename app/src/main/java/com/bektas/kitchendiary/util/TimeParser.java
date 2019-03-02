package com.bektas.kitchendiary.util;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeParser {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private static LocalTime lc;


    private TimeParser(LocalTime time){
        lc = time;
    }

    public static TimeParser parse(String time){
        time = time.isEmpty() ? "00:00" : time;
        return new TimeParser(LocalTime.parse(time, formatter));
    }

    public static TimeParser fromTotalMinute(int total){
        return new TimeParser(LocalTime.of(total/60, total%60));
    }

    public int totalMinute(){
        return (lc.getHour() * 60) + lc.getMinute();
    }

    public String getTimeToDisplay(){
        return lc.toString();
    }
}
