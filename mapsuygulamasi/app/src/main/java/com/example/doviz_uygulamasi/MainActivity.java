package com.example.doviz_uygulamasi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCurrent = findViewById(R.id.btnCurrentLocation);
        Button btnFixed = findViewById(R.id.btnFixedLocation);

        btnCurrent.setOnClickListener(v -> startActivity(new Intent(this, CurrentLocationActivity.class)));
        btnFixed.setOnClickListener(v -> startActivity(new Intent(this, FixedLocationActivity.class)));
    }
}
