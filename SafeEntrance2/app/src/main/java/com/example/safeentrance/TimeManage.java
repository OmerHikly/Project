package com.example.safeentrance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;


public class TimeManage {

    public static  Calendar calendar=Calendar.getInstance();
    public static int Year;
    public static int Month;
    public static int Day;
    public static int Hour;
    public static int Minute;
    public static int Second;




    public static long GetCurrentTime() {

        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Month = Month + 1;
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Second=calendar.get(Calendar.SECOND);

        String myDate = Year + "/" + Month + "/" + Day + " " + Hour + ":" + Minute + ":" + Second;


        SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        java.util.Date date = null;
        try {
            date = sd.parse(myDate);
        } catch (
                ParseException e) {
            e.printStackTrace();
        }
        long d= date.getTime();
        return d;
    }

    /*
    public static String GetFutureTime() {

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        if(currentHour+12>=24){
            currentHour=currentHour-12;
            Day=Day+1;
        }
        else{
            currentHour=currentHour+12;
        }
        int minutes = calendar.get(Calendar.MINUTE);

        String time = currentHour + ":" + minutes;

        Dol = Day + "/" + Month + "/" + Year + "  " + time;

        return Dol;
    }
     */

    public static long ConvertToMillis(String datee){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        java.util.Date date = null;
        try {
            date = sd.parse(datee);
        } catch (
                ParseException e) {
            e.printStackTrace();
        }
       long current = date.getTime();
        return  current;
    }



}
