package com.example.aplikacjadlabiegacza;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TrainingTimeCalculation {

    private TrainingTimeCalculation() {
    }

    /**
     * Liczenie i wyświetlanie czasu trwania treningu.
     */
    public static String getTrainingTimeToShowOnScreen(String startTime, String stopTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.forLanguageTag("pl-PL"));
        Date d1, d2;
        try {
            d1 = format.parse(startTime);
            d2 = format.parse(stopTime);
        } catch (ParseException e) {
            Log.d(TAG, "getTrainingTimeToShowOnScreen:" + e.getMessage());
            return "Error while parsing dates";
        }
        long diff = Math.abs(d2.getTime() - d1.getTime());

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long hours = diff / hoursInMilli;
        diff = diff % hoursInMilli;

        long minutes = diff / minutesInMilli;
        diff = diff % minutesInMilli;

        long seconds = diff / secondsInMilli;


        return addLeadingZero(hours) + ":" + addLeadingZero(minutes) + ":" + addLeadingZero(seconds);
    }

    private static String addLeadingZero(long number) {
        return number < 10 ? "0" + number : String.valueOf(number);
    }
}
