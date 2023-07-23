package com.example.aplikacjadlabiegacza;


public class DataClass {
    private double latitude;
    private double longitude;
    private double speed;
    private int steps;
    private String date;
    private double burntCalories;
    private Double distanceKm;
    private Double bodyMass;

    public DataClass(double latitude, double longitude, double speed, int steps, String date, double burntCalories, Double distanceKm, Double bodyMass) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.steps = steps;
        this.date = date;
        this.burntCalories = burntCalories;
        this.distanceKm = distanceKm;
        this.bodyMass = bodyMass;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getBurntCalories() {
        return burntCalories;
    }

    public void setBurntCalories(double burntCalories) {
        this.burntCalories = burntCalories;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getBodyMass() {
        return bodyMass;
    }

    public void setBodyMass(Double bodyMass) {
        this.bodyMass = bodyMass;
    }
}


