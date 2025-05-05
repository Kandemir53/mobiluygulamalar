package com.example.camera;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WifiActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ToggleButton toggleWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        // Wi-Fi Manager'ı başlatıyoruz
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        toggleWifi = findViewById(R.id.toggleWifi);

        // Wi-Fi durumunu kontrol et ve ToggleButton'ı buna göre ayarla
        toggleWifi.setChecked(wifiManager.isWifiEnabled());

        // ToggleButton tıklama olayını ayarlıyoruz
        toggleWifi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Wi-Fi'yi açıyoruz
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(WifiActivity.this, "Wi-Fi Açıldı", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Wi-Fi'yi kapatıyoruz
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    Toast.makeText(WifiActivity.this, "Wi-Fi Kapalı", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
