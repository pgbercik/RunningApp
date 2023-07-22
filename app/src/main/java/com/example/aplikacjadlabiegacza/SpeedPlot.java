package com.example.aplikacjadlabiegacza;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpeedPlot extends AppCompatActivity {


    private DatabaseReference rootRef;
    private DatabaseReference childReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_plot);

        //pobieramy nazwę treningu
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("name");

            rootRef = FirebaseDatabase.getInstance().getReference();
            childReference = rootRef.child(value);

            childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    BarChart barchart;
                    barchart = findViewById(R.id.barchart);
                    barchart.setDrawValueAboveBar(true); //wartość nad słupkiem zamiast wewnątrz
                    barchart.setPinchZoom(true);  //przybliżanie za pomocą rozciągnięcia palcami

                    //wartości prędkości
                    ArrayList<BarEntry> barEntries = new ArrayList<>();

                    //opisy osi X
                    ArrayList<String> strings = new ArrayList<>();
                    strings.add(""); //dodajemy jeden opis pusty bo ArrayList numeruje od 0, a wartości w bazie są numerowane od 1

                    int i = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        i += 1;
                        double speed = Double.parseDouble(ds.child("speed").getValue().toString());
                        String date = String.valueOf(ds.child("date").getValue());
                        strings.add(date/*.substring(11, 19)*/);
                        barEntries.add(new BarEntry(i, (float) speed));
                    }
                    BarDataSet barDataSet = new BarDataSet(barEntries, "Speed over time [km/h]");
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barDataSet.setDrawValues(true);
                    barDataSet.setValueTextSize(12f); //rozmiar podpisów wewnątrz słupków widocznych po powiększeniu
                    BarData data = new BarData(barDataSet);

                    data.setBarWidth(0.9f);
                    barchart.setData(data);
                    Legend l = barchart.getLegend();
                    l.setTextSize(15f);

                    barchart.getAxisRight().setDrawLabels(false);
                    barchart.getDescription().setTextSize(10f);
                    barchart.getDescription().setText("");

                    // dodawanie wartości osi X, tj. czas pobrania danej próbki
                    XAxis xAxis = barchart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(strings));

                    //legenda osi X u góry
                    xAxis.setPosition(XAxis.XAxisPosition.TOP);

                    //obrót legendy o 90 stopni
                    xAxis.setLabelRotationAngle(-90);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    }
}
