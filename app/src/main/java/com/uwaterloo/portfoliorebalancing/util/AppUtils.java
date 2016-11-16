package com.uwaterloo.portfoliorebalancing.util;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yuweixu on 15-11-08.
 */
public class AppUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.CANADA);

    private static final String DATE_FORMAT_DISPLAY = "E, MMM d, yyyy";
    private static final DateFormat dateFormatDispay = new SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.CANADA);

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    public static String formatDisplayDate(Date date) {
        return dateFormatDispay.format(date);
    }

    public static Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(Locale.CANADA);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static String getCurrentDateString() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static String getFirstOfMonthString() {
        String curDate = getCurrentDateString();
        return curDate.substring(0, curDate.length() - 2) + "01";
    }

    public static Date getFirstOfMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CANADA);
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /*public static String formatDate(String d) {
        StringBuilder builder = new StringBuilder();
        builder.append(d.substring(0,4)).append("-")
                .append(d.substring(5,7)).append("-")
                .append(d.substring(8));

        return builder.toString();
    }*/
}
