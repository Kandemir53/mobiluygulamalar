package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Rock {
    private Bitmap bitmap;
    private int x, y;
    private int speed;

    public Rock(Bitmap bitmap, int x, int y, int speed) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void update() {
        y += speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight;
    }

    public boolean collidesWith(int cx, int cy, int cWidth, int cHeight) {
        return x < cx + cWidth &&
                x + bitmap.getWidth() > cx &&
                y < cy + cHeight &&
                y + bitmap.getHeight() > cy;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return bitmap.getWidth(); }
    public int getHeight() { return bitmap.getHeight(); }
}
