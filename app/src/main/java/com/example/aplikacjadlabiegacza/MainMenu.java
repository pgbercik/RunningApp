package com.example.aplikacjadlabiegacza;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {
    Button btnNewTraining, btnTrainingList;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showAlertDialog();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        btnNewTraining = (Button) findViewById(R.id.btnNewTraining);
        btnTrainingList = (Button) findViewById(R.id.btnTrainingList);

        enableButtons();
    }

    private void enableButtons() {
        btnNewTraining.setOnClickListener(v -> {
            Intent trainingIntent = new Intent(getApplicationContext(), TrainingActivity.class);
            startActivity(trainingIntent);
        });
        btnTrainingList.setOnClickListener(v -> {
            Intent showTrainingSessionsIntent = new Intent(getApplicationContext(), ListTrainingSessions.class);
            startActivity(showTrainingSessionsIntent);
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        builder.setMessage("Are you sure you want to exit ?");
        builder.setTitle("Attention!");
        // jeśli będzie true to user klikając poza okienkiem wyłączy je
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
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
