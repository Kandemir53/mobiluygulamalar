package com.example.myapplication;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CoinManager {
    private static final String COIN_FILE_NAME = "coins.txt";
    private Context context;

    public CoinManager(Context context) {
        this.context = context.getApplicationContext(); // Context güvenli şekilde alındı
    }

    public int getCoins() {
        try {
            FileInputStream fis = context.openFileInput(COIN_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            reader.close();
            fis.close();
            return (line != null && !line.isEmpty()) ? Integer.parseInt(line) : 0;
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    public void setCoins(int amount) {
        try {
            FileOutputStream fos = context.openFileOutput(COIN_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(String.valueOf(amount).getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCoins(int amount) {
        int current = getCoins();
        setCoins(current + amount);
    }

    public void spendCoins(int amount) {
        int current = getCoins();
        if (current >= amount) {
            setCoins(current - amount);
        }
    }
}
