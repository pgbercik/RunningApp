package com.example.aplikacjadlabiegacza;

public final class ValueRescaler {
    private ValueRescaler() {
    }

    /**
     * Zaokrąglanie do 2 miejsc po przecinku.
     */
    public static double rescaleValue(double value, double digits) {
        value *= Math.pow(10, digits);
        value = Math.round(value);
        value = value / Math.pow(10, digits);
        return value;
    }
}
