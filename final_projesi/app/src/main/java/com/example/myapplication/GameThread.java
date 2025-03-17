package com.example.myapplication;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;

    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
        running = true;
    }

    public void stopGame() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    gameView.update(); // Karakter hareketini güncelle
                    gameView.drawGame(canvas); // Ekranı tekrar çiz
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                sleep(16); // 60 FPS için
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
