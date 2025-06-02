package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bullet {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 20;

    public Bullet(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public boolean isOffScreen() {
        return y + bitmap.getHeight() < 0;
    }

    public boolean collidesWith(Rock rock) {
        return x < rock.getX() + rock.getWidth() &&
                x + bitmap.getWidth() > rock.getX() &&
                y < rock.getY() + rock.getHeight() &&
                y + bitmap.getHeight() > rock.getY();
    }
}
