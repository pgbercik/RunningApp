package com.example.aplikacjadlabiegacza;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrainingActivity extends AppCompatActivity implements SensorEventListener {

    private Button btnStart, btnStop, btnClear;
    private TextView count, showDistance, showCalories, showSpeed, showTime;
    private EditText editTrainingName, editBodyMass;
    private BroadcastReceiver broadcastReceiver;
    LocationManager lm;
    private SensorManager sensorManager;

    boolean activityRunning;
    private float initialAmmount = 0; // wartość countera przy uruchomieniu programu
    private boolean alreadyMeasured = false;// sprawdza czy pierwszy raz jest robiony odczyt kroków, jeśłi tak to zapisuje wartośc do initialAmount
    private boolean showSteps = false;  //czy pokazywać kroki w textview count
    private double latitude = 0.0;
    private int stepsMeasured = 0;

    private double speedkmH = 0;
    private String trainingSessionName, startTime, stopTime; //nazwa sesji treningowej
    DatabaseReference reff;  //referencja do bazy
    int maxId = 0; //liczba wierszy
    double bodyMass;
    double distance, distanceKm, totalDistanceKm, calories, caloriesTotal;
    int iteration = 0;

    @Override
    public void onBackPressed() {
        showAlertDialog();

    }

    @Override
    protected void onResume() {

        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Double latitude = (Double) intent.getExtras().get("latitude");
                    Double longitude = (Double) intent.getExtras().get("longitude");
                    Double speedkmH = (Double) intent.getExtras().get("speed");

                    String currentDateandTime = String.valueOf(intent.getExtras().get("time"));

                    //pobieranie danych potrzebnych do liczenie ile już trwa trening -> countAndShowTainingTime()
                    if (iteration == 0) {
                        startTime = currentDateandTime;
                        stopTime = startTime;
                    }
                    iteration += 1;
                    if (iteration != 0) {
                        stopTime = currentDateandTime;
                    }
                    //liczymy ile już trwa trening i wyrzucamy to na ekran
                    showTime.setText(TrainingTimeCalculation.getTrainingTimeToShowOnScreen(startTime, stopTime));

                    distanceKm = (double) intent.getExtras().get("distance") / 1000;
                    totalDistanceKm += distanceKm;
                    totalDistanceKm = ValueRescaler.rescaleValue(totalDistanceKm, 2);

                    calories = bodyMass * distanceKm;
                    caloriesTotal += calories;
                    showDistance.setText(String.valueOf(totalDistanceKm));

                    caloriesTotal = ValueRescaler.rescaleValue(calories, 2);
                    showCalories.setText(String.valueOf(caloriesTotal));
                    showSpeed.setText(String.valueOf(speedkmH));


                    count.setText(String.valueOf(stepsMeasured));

                    //dodawanie tabeli( nazwę ustawić w nawiasie)
                    reff = FirebaseDatabase.getInstance().getReference().child(trainingSessionName);

                    DataClass data = new DataClass(
                            latitude,
                            longitude,
                            speedkmH,
                            stepsMeasured,
                            currentDateandTime,
                            calories,
                            distanceKm,
                            bodyMass);

                    maxId += 1;
                    reff.child(String.valueOf(maxId)).setValue(data); //wysyłanie do bazy
                }


            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        editTrainingName = findViewById(R.id.editTrainingName);
        editBodyMass = findViewById(R.id.editBodyMass);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        count = findViewById(R.id.txtSteps);
        showDistance = findViewById(R.id.txtDistance);
        showCalories = findViewById(R.id.txtCalories);
        showSpeed = findViewById(R.id.txtSpeed);
        showTime = findViewById(R.id.txtTime);

        if (!runtime_permissions()) enable_buttons();


    }

    private void enable_buttons() {
        btnStart.setOnClickListener(v -> {
            lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            trainingSessionName = editTrainingName.getText().toString().trim();

            String bodyMassString = editBodyMass.getText().toString().trim();
            if (bodyMassString.matches("")) bodyMass = 0.0;
            else bodyMass = Double.parseDouble(bodyMassString);

            if (isGPSEnabled) {

                if (!trainingSessionName.equals("") && bodyMass != 0.0) {
                    stepsMeasured = 0;
                    disableTrainingEditField();
                    Intent i = new Intent(getApplicationContext(), GPSService.class);
                    startService(i);

                    alreadyMeasured = false; //wymuszamy co by step counter zresetował wskazania
                    showSteps = true;
                } else
                    Toast.makeText(getApplicationContext(), "Insert training session name and body mass", Toast.LENGTH_SHORT).show();

            } else {
                final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        btnStop.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), GPSService.class);
            stopService(i);
            maxId = 0; //resetowanie auto_increment
            showSteps = false; //wyłączamy pokazywanie kroków

            //zeruje zmienne dotyczące przebytego dystansu, spalonych kalorii i prędkości biegu
            resetVariables();

            // umożliwia wpisywanie nazwy treningu imasy ciała
            // po zakończeniu bieżacego treningu
            enableTrainingEditField();

        });

    }

    private void resetVariables() {
        distanceKm = 0;
        totalDistanceKm = 0;
        calories = 0;
        caloriesTotal = 0;
        speedkmH = 0;
        iteration = 0;
    }

    private void enableTrainingEditField() {

        editTrainingName.setText(null);
        editTrainingName.setEnabled(true);
        editTrainingName.requestFocus();
        editBodyMass.setText("");
        editBodyMass.setEnabled(true);
    }

    private void disableTrainingEditField() {
        editTrainingName.setEnabled(false);
        editBodyMass.setEnabled(false);

    }

    private boolean runtime_permissions() {
        if (/*Build.VERSION.SDK_INT >= 23 &&*/ ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enable_buttons();
            } else runtime_permissions();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning) {
            if (!alreadyMeasured) {
                initialAmmount = event.values[0];
                alreadyMeasured = true;
            }
            if (showSteps) {
                stepsMeasured = (int) (event.values[0] - initialAmmount);
            } else stepsMeasured = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.this);
        builder.setMessage("Are you sure you want to exit? Clicking Yes will end current training session.");
        builder.setTitle("Warning!");

        // jeśli będzie true to user klikając poza okienkiem wyłączy je
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent i = new Intent(getApplicationContext(), GPSService.class);
            stopService(i);
            showSteps = false; //wyłączamy pokazywanie kroków
            activityRunning = false;
            // zamykanie całego activity
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // wyłączania okienka dialog
            dialog.cancel();
        });

        // tworzenie okienka
        AlertDialog alertDialog = builder.create();

        // tutaj sprawiamy, że okienko będzie widoczne
        alertDialog.show();
    }

}
