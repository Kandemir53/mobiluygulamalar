package com.example.sensor_uygulamas;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private void setupButton(int buttonId, final int sensorType) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("sensorType", sensorType);
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButton(R.id.btnAccelerometer, Sensor.TYPE_ACCELEROMETER);
        setupButton(R.id.btnCompass, Sensor.TYPE_ORIENTATION); // Deprecated ama örnek olarak
        setupButton(R.id.btnGyroscope, Sensor.TYPE_GYROSCOPE);
        setupButton(R.id.btnHumidity, Sensor.TYPE_RELATIVE_HUMIDITY);
        setupButton(R.id.btnLight, Sensor.TYPE_LIGHT);
        setupButton(R.id.btnMagnometer, Sensor.TYPE_MAGNETIC_FIELD);
        setupButton(R.id.btnPressure, Sensor.TYPE_PRESSURE);
        setupButton(R.id.btnProximity, Sensor.TYPE_PROXIMITY);
        setupButton(R.id.btnThermometer, Sensor.TYPE_AMBIENT_TEMPERATURE);
    }
}
