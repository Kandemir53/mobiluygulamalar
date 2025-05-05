package com.example.camera;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    Button btnOn, btnOff, btnList, btnVisible;
    BluetoothAdapter bluetoothAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        btnOn = findViewById(R.id.btnOn);
        btnOff = findViewById(R.id.btnOff);
        btnList = findViewById(R.id.btnList);
        btnVisible = findViewById(R.id.btnVisible);
        listView = findViewById(R.id.listView);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Android 12+ için runtime izni iste
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
            }
        }

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Cihaz Bluetooth desteklemiyor", Toast.LENGTH_LONG).show();
            finish();
        }

        // Bluetooth Açma
        btnOn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, 0);
                } else {
                    Toast.makeText(this, "Bluetooth zaten açık", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Bluetooth izinleri gerekli", Toast.LENGTH_SHORT).show();
            }
        });

        // Bluetooth Kapatma
        btnOff.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    Toast.makeText(this, "Bluetooth kapatıldı", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Bluetooth izinleri gerekli", Toast.LENGTH_SHORT).show();
            }
        });

        // Bluetooth Görünürlük
        btnVisible.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent visibleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                visibleIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(visibleIntent);
            } else {
                Toast.makeText(this, "Bluetooth görünürlük izni gerekli", Toast.LENGTH_SHORT).show();
            }

    });

        // Eşleşmiş cihazları listele
        btnList.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                ArrayList<String> deviceList = new ArrayList<>();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        deviceList.add(device.getName() + " - " + device.getAddress());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "Eşleşmiş cihaz yok", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Bluetooth izinleri gerekli", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // İzinlerin sonuçlarını kontrol et
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth izni verildi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth izni reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
