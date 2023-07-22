package com.example.aplikacjadlabiegacza;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GPSService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    double distance;
    private int amountofPositions = 0; // licznik ile pozycji gps wykryto, przydatny przy liczeniu odległości między punktami
    double latitude1;
    double longitude1;
    double latitude2;
    double longitude2;


    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(calendar.getTime());

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                amountofPositions += 1;

                //convert speed to km/h
                double speedkmH = Math.round(location.getSpeed() * 3.6 * 100) / 100.0;


                if (amountofPositions < 2) {
                    latitude2 = location.getLatitude();
                    longitude2 = location.getLongitude();
                }
                if (amountofPositions >= 2) {
                    latitude1 = latitude2;
                    longitude1 = longitude2;
                    latitude2 = location.getLatitude();
                    longitude2 = location.getLongitude();
                    countDistance(latitude1, longitude1, latitude2, longitude2);  //zwraca odległość w globalnej zmiennej distance - globalnej wewnątrz klasy
                }


                Intent i = new Intent("location_update");
                i.putExtra("longitude", location.getLongitude())
                        .putExtra("latitude", location.getLatitude())
                        .putExtra("speed", speedkmH)
                        .putExtra("time", getCurrentDateTime())
                        .putExtra("distance", distance);
                sendBroadcast(i);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            //noinspection MissingPermission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, listener);
        } else {
            Toast.makeText(getApplicationContext(), "Please turn on the GPS and restart the app.", Toast.LENGTH_LONG).show();
        }

    }

    //odległość między 2 punktami
    private void countDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        Location l1 = new Location("l1");
        l1.setLatitude(latitude1);
        l1.setLongitude(longitude1);
        Location l2 = new Location("l2");
        l2.setLatitude(latitude2);
        l2.setLongitude(longitude2);
        distance = l1.distanceTo(l2);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //noinspection MissingPermission
        if (locationManager != null) locationManager.removeUpdates(listener);
    }
}
