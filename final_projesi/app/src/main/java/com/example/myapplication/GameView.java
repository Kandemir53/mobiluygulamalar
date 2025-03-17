package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private Bitmap background, character, tas, startScreen, playButton, gameOverScreen;
    private int characterX, characterY;
    private int characterSpeed = 20;
    private boolean movingLeft = false, movingRight = false;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private int playButtonX, playButtonY;
    private ArrayList<Rock> rocks = new ArrayList<>();
    private long lastRockTime = 0;
    private final int rockSpawnInterval = 2000;
    private Random random = new Random();

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        // Assetleri yükle
        startScreen = BitmapFactory.decodeResource(getResources(), R.drawable.start_screen);
        playButton = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.arka_plan);
        character = BitmapFactory.decodeResource(getResources(), R.drawable.karakter);
        tas = BitmapFactory.decodeResource(getResources(), R.drawable.tas1);
        gameOverScreen = BitmapFactory.decodeResource(getResources(), R.drawable.gameover_screen);

        gameThread = new GameThread(getHolder(), this);
    }

    public void update() {
        if (!gameStarted || gameOver) return;

        if (movingLeft && characterX > 0) {
            characterX -= characterSpeed;
        }
        if (movingRight && characterX + character.getWidth() < getWidth()) {
            characterX += characterSpeed;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRockTime > rockSpawnInterval) {
            int rockX = random.nextInt(getWidth() - tas.getWidth());
            rocks.add(new Rock(tas, rockX, 0, 15));
            lastRockTime = currentTime;
        }

        for (int i = 0; i < rocks.size(); i++) {
            rocks.get(i).update();

            // Çarpışma kontrolü
            if (checkCollision(rocks.get(i))) {
                gameOver = true; // Oyun bitti
                return;
            }

            if (rocks.get(i).isOffScreen(getHeight())) {
                rocks.remove(i);
                i--;
            }
        }
    }

    public void drawGame(Canvas canvas) {
        if (canvas == null) return;

        if (!gameStarted) {
            canvas.drawBitmap(startScreen, 0, 0, null);
            playButtonX = getWidth() / 2 - playButton.getWidth() / 2;
            playButtonY = getHeight() / 2 + 200;
            canvas.drawBitmap(playButton, playButtonX, playButtonY, null);
        } else if (gameOver) {
            canvas.drawBitmap(gameOverScreen, 0, 0, null); // Game Over ekranını çiz
        } else {
            canvas.drawBitmap(background, 0, 0, null);
            canvas.drawBitmap(character, characterX, characterY, null);
            for (Rock rock : rocks) {
                rock.draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameStarted) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                float touchY = event.getY();
                if (touchX >= playButtonX && touchX <= playButtonX + playButton.getWidth() &&
                        touchY >= playButtonY && touchY <= playButtonY + playButton.getHeight()) {
                    gameStarted = true;
                }
            }
            return true;
        }

        if (gameOver) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            if (touchX < getWidth() / 2) {
                movingLeft = true;
                movingRight = false;
            } else {
                movingRight = true;
                movingLeft = false;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            movingLeft = false;
            movingRight = false;
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        characterX = getWidth() / 2 - character.getWidth() / 2;
        characterY = getHeight() - character.getHeight() - 50;

        if (gameThread.getState() == Thread.State.NEW) {
            gameThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameThread.stopGame();
    }

    private boolean checkCollision(Rock rock) {
        int rockX = rock.getX();
        int rockY = rock.getY();
        int rockWidth = tas.getWidth();
        int rockHeight = tas.getHeight();

        return characterX < rockX + rockWidth &&
                characterX + character.getWidth() > rockX &&
                characterY < rockY + rockHeight &&
                characterY + character.getHeight() > rockY;
    }
}
