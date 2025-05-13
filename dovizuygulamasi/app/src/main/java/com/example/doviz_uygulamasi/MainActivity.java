package com.example.doviz_uygulamasi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView chfText, usdText, jpyText, tryText, cadText;
    private Button getRatesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chfText = findViewById(R.id.chfText);
        usdText = findViewById(R.id.usdText);
        jpyText = findViewById(R.id.jpyText);
        tryText = findViewById(R.id.tryText);
        cadText = findViewById(R.id.cadText);
        getRatesButton = findViewById(R.id.getRatesButton);

        getRatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRates();
            }
        });
    }

    private void getRates() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = "https://data.fixer.io/api/latest?access_key=3a01e27604ccd29d8dbf7c9f72c42dd9";
                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );

                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    JSONObject json = new JSONObject(response.toString());
                    final JSONObject rates = json.getJSONObject("rates");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                chfText.setText("CHF: " + rates.getDouble("CHF"));
                                usdText.setText("USD: " + rates.getDouble("USD"));
                                jpyText.setText("JPY: " + rates.getDouble("JPY"));
                                tryText.setText("TRY: " + rates.getDouble("TRY"));
                                cadText.setText("CAD: " + rates.getDouble("CAD"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
