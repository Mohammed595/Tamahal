package com.example.tamahal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AskedHelp extends AppCompatActivity {

    Button backBtn, getBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asked_help);

        backBtn =  findViewById(R.id.backBtn);
        getBtn = findViewById(R.id.getBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AskedHelp.this, MainActivity.class);
                startActivity(intent);

            }
        });



        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AskedHelp.this, "Done Detected", Toast.LENGTH_LONG).show();

            }
        });

    }
}