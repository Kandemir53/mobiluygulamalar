package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private Bitmap background, character, tas, gameOverScreen, bullet, restartButton;
    private int characterX, characterY;
    private int characterSpeed = 20;
    private boolean movingLeft = false, movingRight = false;
    private ArrayList<Rock> rocks = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private long lastRockTime = 0;
    private final int rockSpawnInterval = 2000;
    private Random random = new Random();
    private boolean isGameOver = false;

    private boolean isGameStarted = false;
    private Bitmap startScreen, playButton;

    private long lastClickTime = 0;
    private final long doubleClickTimeDelta = 300;
    private boolean isHolding = false;
    private long holdStartTime = 0;
    private final long holdThreshold = 80;

    private Bitmap apple, banana, cherries, kiwi, melon, orange, pineapple, strawberry;
    private int[] fruitPoints = {10, 15, 20, 25, 30, 35, 40, 45};
    private Bitmap[] fruitBitmaps;
    private ArrayList<Fruit> fruits = new ArrayList<>();
    private long lastFruitTime = 0;
    private final int fruitSpawnInterval = 3000;
    private int score = 0;
    private Paint scorePaint;

    // Koşma animasyonu için
    private Bitmap[] runFrames = new Bitmap[12];
    private int currentRunFrame = 0;
    private long lastRunFrameChangeTime = 0;
    private final long runFrameChangeInterval = 100; // ms

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.arka_plani);
        character = BitmapFactory.decodeResource(getResources(), R.drawable.karakter);
        tas = BitmapFactory.decodeResource(getResources(), R.drawable.tas1);
        gameOverScreen = BitmapFactory.decodeResource(getResources(), R.drawable.gameover_screen);
        bullet = BitmapFactory.decodeResource(getResources(), R.drawable.mermi);

        startScreen = BitmapFactory.decodeResource(getResources(), R.drawable.start_screenpane);
        playButton = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);
        restartButton = BitmapFactory.decodeResource(getResources(), R.drawable.restart_button);

        apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        banana = BitmapFactory.decodeResource(getResources(), R.drawable.bananas);
        cherries = BitmapFactory.decodeResource(getResources(), R.drawable.cherries);
        kiwi = BitmapFactory.decodeResource(getResources(), R.drawable.kiwi);
        melon = BitmapFactory.decodeResource(getResources(), R.drawable.melon);
        orange = BitmapFactory.decodeResource(getResources(), R.drawable.orange);
        pineapple = BitmapFactory.decodeResource(getResources(), R.drawable.pineapple);
        strawberry = BitmapFactory.decodeResource(getResources(), R.drawable.strawberry);

        fruitBitmaps = new Bitmap[]{apple, banana, cherries, kiwi, melon, orange, pineapple, strawberry};

        // 12 karelik koşma animasyon karelerini yükle
        for (int i = 0; i < 12; i++) {
            int resId = getResources().getIdentifier("run_" + (i + 1), "drawable", context.getPackageName());
            runFrames[i] = BitmapFactory.decodeResource(getResources(), resId);
        }

        gameThread = new GameThread(getHolder(), this);

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(65);
        scorePaint.setFakeBoldText(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        characterX = screenWidth / 2 - character.getWidth() / 2;
        characterY = screenHeight - character.getHeight() - 225;

        if (gameThread.getState() == Thread.State.NEW) {
            gameThread.start();
        }
    }

    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override public void surfaceDestroyed(SurfaceHolder holder) { gameThread.stopGame(); }

    public void update() {
        if (isGameOver) return;

        if (isHolding && System.currentTimeMillis() - holdStartTime >= holdThreshold) {
            if (movingLeft && characterX > 0) {
                characterX -= characterSpeed;
            }
            if (movingRight && characterX + character.getWidth() < getWidth()) {
                characterX += characterSpeed;
            }
        }

        // Animasyon karesi güncelle
        if ((movingLeft || movingRight) && System.currentTimeMillis() - lastRunFrameChangeTime >= runFrameChangeInterval) {
            currentRunFrame = (currentRunFrame + 1) % runFrames.length;
            lastRunFrameChangeTime = System.currentTimeMillis();
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRockTime > rockSpawnInterval) {
            int rockX = random.nextInt(getWidth() - tas.getWidth());
            rocks.add(new Rock(tas, rockX, 0, 15));
            lastRockTime = currentTime;
        }

        for (int i = 0; i < rocks.size(); i++) {
            rocks.get(i).update();

            if (rocks.get(i).collidesWith(characterX, characterY, character.getWidth(), character.getHeight())) {
                isGameOver = true;
                return;
            }

            if (rocks.get(i).isOffScreen(getHeight())) {
                rocks.remove(i);
                i--;
            }
        }

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();

            if (bullets.get(i).isOffScreen()) {
                bullets.remove(i);
                i--;
                continue;
            }

            for (int j = 0; j < rocks.size(); j++) {
                if (bullets.get(i).collidesWith(rocks.get(j))) {
                    bullets.remove(i);
                    rocks.remove(j);
                    score += 5;
                    i--;
                    break;
                }
            }
        }

        if (currentTime - lastFruitTime > fruitSpawnInterval) {
            spawnFruit();
            lastFruitTime = currentTime;
        }

        for (int i = 0; i < fruits.size(); i++) {
            fruits.get(i).update();

            if (fruits.get(i).collidesWith(characterX, characterY, character.getWidth(), character.getHeight())) {
                score += fruits.get(i).getPoint();
                fruits.remove(i);
                i--;
            } else if (fruits.get(i).isOffScreen(getHeight())) {
                fruits.remove(i);
                i--;
            }
        }
    }

    public void drawGame(Canvas canvas) {
        if (canvas != null) {
            if (!isGameStarted) {
                canvas.drawBitmap(startScreen, 0, 0, null);
                int buttonX = getWidth() / 2 - playButton.getWidth() / 2;
                int buttonY = getHeight() / 2 + 150 - playButton.getHeight() / 2 + 150;
                canvas.drawBitmap(playButton, buttonX, buttonY, null);
            } else {
                canvas.drawBitmap(background, 0, 0, null);

                // Karakter animasyonunu çiz
                if (movingLeft) {
                    Bitmap flipped = flipBitmap(runFrames[currentRunFrame]);
                    canvas.drawBitmap(flipped, characterX, characterY, null);
                } else if (movingRight) {
                    canvas.drawBitmap(runFrames[currentRunFrame], characterX, characterY, null);
                } else {
                    canvas.drawBitmap(character, characterX, characterY, null);
                }

                for (Rock rock : rocks) rock.draw(canvas);
                for (Bullet bullet : bullets) bullet.draw(canvas);
                for (Fruit fruit : fruits) fruit.draw(canvas);

                canvas.drawText("Puan: " + score, 50, 150, scorePaint);

                if (isGameOver) {
                    canvas.drawBitmap(gameOverScreen, 0, 0, null);
                    int restartButtonX = getWidth() / 2 - restartButton.getWidth() / 2;
                    int restartButtonY = getHeight() / 2 + 100;
                    canvas.drawBitmap(restartButton, restartButtonX, restartButtonY, null);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            float touchX = event.getX();
            float touchY = event.getY();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int restartButtonX = getWidth() / 2 - restartButton.getWidth() / 2;
                int restartButtonY = getHeight() / 2 + 100;
                if (touchX >= restartButtonX && touchX <= restartButtonX + restartButton.getWidth() &&
                        touchY >= restartButtonY && touchY <= restartButtonY + restartButton.getHeight()) {
                    resetGame();
                }
            }
        }

        if (isGameOver) return true;

        float touchX = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isGameStarted) {
                    int buttonX = getWidth() / 2 - playButton.getWidth() / 2;
                    int buttonY = getHeight() / 2 + 150 - playButton.getHeight() / 2 + 150;
                    if (touchX >= buttonX && touchX <= buttonX + playButton.getWidth() &&
                            event.getY() >= buttonY && event.getY() <= buttonY + playButton.getHeight()) {
                        isGameStarted = true;
                    }
                } else {
                    isHolding = true;
                    holdStartTime = System.currentTimeMillis();
                    if (System.currentTimeMillis() - lastClickTime < doubleClickTimeDelta) {
                        bullets.add(new Bullet(bullet, characterX + character.getWidth() / 2 - bullet.getWidth() / 2, characterY));
                    } else {
                        if (touchX < getWidth() / 2) {
                            movingLeft = true;
                            movingRight = false;
                        } else {
                            movingRight = true;
                            movingLeft = false;
                        }
                    }
                }
                lastClickTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_UP:
                isHolding = false;
                movingLeft = false;
                movingRight = false;
                break;
        }

        return true;
    }

    private void spawnFruit() {
        int fruitType = random.nextInt(fruitBitmaps.length);
        int fruitX = random.nextInt(getWidth() - fruitBitmaps[fruitType].getWidth());
        fruits.add(new Fruit(fruitBitmaps[fruitType], fruitX, 0, 10, fruitPoints[fruitType]));
    }

    private void resetGame() {
        isGameOver = false;
        score = 0;
        rocks.clear();
        bullets.clear();
        fruits.clear();
        isGameStarted = false;
    }

    private Bitmap flipBitmap(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
    }
}