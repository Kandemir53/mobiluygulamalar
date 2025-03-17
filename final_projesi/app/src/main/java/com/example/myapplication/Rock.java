package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Rock {
    private Bitmap bitmap;
    private int x, y;
    private int speed;

    public Rock(Bitmap bitmap, int startX, int startY, int speed) {
        this.bitmap = bitmap;
        this.x = startX;
        this.y = startY;
        this.speed = speed;
    }

    public void update() {
        y += speed; // Taşın aşağı hareket etmesini sağla
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight; // Ekranın dışına çıkmış mı kontrol et
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }
}

