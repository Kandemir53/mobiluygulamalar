package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Fruit {
    private Bitmap bitmap;
    private int x, y, speed, point;

    public Fruit(Bitmap bitmap, int x, int y, int speed, int point) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.point = point;
    }

    public void update() {
        y += speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public boolean collidesWith(int characterX, int characterY, int characterWidth, int characterHeight) {
        return x < characterX + characterWidth &&
                x + bitmap.getWidth() > characterX &&
                y < characterY + characterHeight &&
                y + bitmap.getHeight() > characterY;
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight;
    }

    public int getPoint() {
        return point;
    }
}