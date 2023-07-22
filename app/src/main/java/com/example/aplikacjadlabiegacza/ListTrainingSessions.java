package com.example.aplikacjadlabiegacza;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListTrainingSessions extends AppCompatActivity {

    private DatabaseReference reff;
    private ListView trainingSessionListView;
    String name;


    private ArrayList<String> trainingSessionList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.training_session_list);
        reff = FirebaseDatabase.getInstance().getReference();
        trainingSessionListView = findViewById(R.id.listOfTrainingSessions);
        trainingSessionListView.setOnItemClickListener((parent, view, position, id) -> {
            name = parent.getItemAtPosition(position).toString();  //nazwa klikniętego treningu
            showAlertDialog();
        });

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trainingSessionList);
        trainingSessionListView.setAdapter(arrayAdapter);

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey(); //nazwa danego treningu
                    trainingSessionList.add(key); //dodawanie do listview
                    arrayAdapter.notifyDataSetChanged(); //aktualizacja listy
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ListTrainingSessions.this);

        //tytuł
        builder.setTitle("Choose action");

        //tytuły przycisków
        final CharSequence[] dialogOptions = {"SHOW MAP", "SHOW SPEED CHART", "SHOW STATISTICS", "DELETE TRAINING SESSION", "CANCEL"};

        //po kliknięciu na projekt wybieramy z listy co chcemy robić
        builder.setItems(dialogOptions, (dialog, selectedOption) -> {
            Intent i;

            switch (selectedOption) {

                case 0:

                    //kolejne 4 linijki to przesyłanie nazwy treningu do ShowMap i  uruchamianie ShowMap poprzez intent

                    i = new Intent(ListTrainingSessions.this, ShowMap.class);
                    i.putExtra("name", name);
                    startActivity(i);

//                        // zamykanie całego activity
                    finish();
                    break;
                case 1:

                    i = new Intent(ListTrainingSessions.this, SpeedPlot.class);
                    i.putExtra("name", name);
                    startActivity(i);

//                        // zamykanie całego activity
                    finish();
                    break;
                case 2:

                    i = new Intent(ListTrainingSessions.this, StatisticsActivity.class);
                    i.putExtra("name", name);
                    startActivity(i);

                    // zamykanie całego activity
                    finish();
                    break;
                case 3:
                    deleteTrainingSession(name);
                    break;
                case 4:
                    dialog.cancel();
                    break;
            }
        });
        builder.create().show();
    }

    private void deleteTrainingSession(final String nameToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListTrainingSessions.this);
        builder.setMessage("Are you sure you want to delete this training session?");
        builder.setTitle("Warning!");

        // jeśli będzie true to user klikając poza okienkiem wyłączy je
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference childReference = rootRef.child(nameToDelete);
            childReference.removeValue();

            //restart activity
            Intent i = getIntent();
            finish();
            startActivity(i);
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


