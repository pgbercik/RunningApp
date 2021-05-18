package com.example.aplikacjadlabiegacza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {
    private TextView txttrainingSessionName, txtAverageSpeed, txtTotalDistanceKm, txtStartTime, txtStopTime, txtStepsTotal, txtBurntCalories;
    private DatabaseReference rootRef, childReference;
    private TextView txtTrainingDuration, txtBodyMass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        txttrainingSessionName = findViewById(R.id.txtTrainingSessionName);
        txtAverageSpeed = findViewById(R.id.txtAverageSpeed);
        txtTotalDistanceKm = findViewById(R.id.txtTotalDistanceKm);
        txtStartTime = findViewById(R.id.txtStartTime);
        txtStopTime = findViewById(R.id.txtStopTime);
        txtStepsTotal = findViewById(R.id.txtStepsTotal);
        txtTrainingDuration = findViewById(R.id.txtTrainingDuration);
        txtBurntCalories = findViewById(R.id.txtBurntCalories);
        txtBodyMass = findViewById(R.id.txtBodyMass);

        Bundle extras = getIntent().getExtras();
        //jeśli nazwa!=null to pobieramy z bazy resztę potrzebnych danych
        if (extras != null) {
            final String value = extras.getString("name");
            txttrainingSessionName.setText(value);


            rootRef = FirebaseDatabase.getInstance().getReference();
            childReference = rootRef.child(value);

            childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                private int iterationCounter;
                double totalSpeed, averageSpeed, totalDistance, caloriesTotal;
                String startTime, stopTime, totalSteps;

                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        iterationCounter += 1; // liczy ile jest rekordów w tabeli

                        totalSpeed += Double.parseDouble(String.valueOf(ds.child("speed").getValue()));

                        averageSpeed = totalSpeed / iterationCounter;
                        averageSpeed = ValueRescaler.rescaleValue(averageSpeed, 2);

                        if (ds.child("distanceKm").getValue() != null) {
                            totalDistance += Double.parseDouble(String.valueOf(ds.child("distanceKm").getValue()));
                            totalDistance = ValueRescaler.rescaleValue(totalDistance, 2);
                            txtTotalDistanceKm.setText(String.valueOf(totalDistance));
                        } else txtTotalDistanceKm.setText("no data");

                        txtAverageSpeed.setText(String.valueOf(averageSpeed));

                        //data rozpoczęcia treninigu
                        if (iterationCounter == 1 && ds.child("date").getValue() != null) {
                            startTime = String.valueOf(ds.child("date").getValue());
                            txtStartTime.setText(startTime);
                        }

                        //data zakończenia
                        if (ds.child("date").getValue() != null) {
                            stopTime = String.valueOf(ds.child("date").getValue());
                            txtStopTime.setText(stopTime);
                        }
                        //kroki
                        if (ds.child("steps").getValue() != null) {
                            totalSteps = ds.child("steps").getValue().toString();
                            txtStepsTotal.setText(totalSteps);
                        }
                        //liczymy i pokazujemy czas trwania treninigu
                        TrainingTimeCalculation.countAndShowTrainingTime(startTime, stopTime, txtTrainingDuration);

                        //kalorie
                        double calories = Double.parseDouble(ds.child("burntCalories").getValue().toString());
                        caloriesTotal += calories;
                        caloriesTotal = ValueRescaler.rescaleValue(calories, 2);
                        txtBurntCalories.setText(String.valueOf(caloriesTotal));

                        //masa ciała
                        String bodyMass = String.valueOf(ds.child("bodyMass").getValue());
                        txtBodyMass.setText(bodyMass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }

            });

        }

    }


}
