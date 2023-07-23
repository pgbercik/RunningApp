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
        btnNewTraining.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), TrainingActivity.class)));
        btnTrainingList.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ListTrainingSessions.class)));
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this)
                .setMessage("Are you sure you want to exit ?")
                .setTitle("Attention!");

        builder.setCancelable(false);  // jeśli będzie true to user klikając poza okienkiem wyłączy je
        builder.setPositiveButton("Yes", (dialog, which) -> finish()); // zamykanie całego activity
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel()); // wyłączania okienka dialog
        // tworzenie okienka
        AlertDialog alertDialog = builder.create();
        // tutaj sprawiamy, że okienko będzie widoczne
        alertDialog.show();
    }
}
