package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.*;
import android.text.InputType;
import android.view.*;
import android.widget.EditText;

import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.widget.Toast;
import android.content.SharedPreferences;

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

    private Bitmap[] fruitBitmaps;
    private int[] fruitPoints = {10, 15, 20, 25, 30, 35, 40, 45};
    private ArrayList<Fruit> fruits = new ArrayList<>();
    private long lastFruitTime = 0;
    private final int fruitSpawnInterval = 3000;
    private int score = 0;
    private Paint scorePaint;

    private Bitmap[] runFrames = new Bitmap[12]; // Ana karakter
    private int currentRunFrame = 0;
    private long lastRunFrameChangeTime = 0;
    private final long runFrameChangeInterval = 100;

    private ScoreManager scoreManager;
    private List<ScoreEntry> topScores = new ArrayList<>();

    private Bitmap leaderboardButton;
    private boolean isInLeaderboardScreen = false;
    private Bitmap leaderboardBackground;
    private Bitmap backButton;
    private float scrollOffset = 0;
    private float lastTouchY = 0;
    private final int SCORE_LINE_HEIGHT = 50;

    private boolean scoreSaved = false;
    private CoinManager coinManager;
    private Bitmap coinIcon;
    private Bitmap marketButton;
    private boolean isInMarketScreen = false;
    private Bitmap marketBackground;

    // Karakter Seçim Sistemi
    private Bitmap[] altCharacterFrames = new Bitmap[12]; // Maskdude
    private boolean isAltCharacterEquipped = false;
    private boolean isAltCharacterPurchased = false;
    private Bitmap altCharacterIcon;
    private Rect altCharacterButtonRect;

    private Bitmap[] pinkywinkyRunFrames = new Bitmap[12]; // PinkyWinky
    private Bitmap pinkywinkyIdle;
    private Bitmap pinkywinkyIcon;
    private boolean isPinkywinkyEquipped = false;
    private boolean isPinkywinkyPurchased = false;
    private Rect pinkywinkyButtonRect;

    private Bitmap mainCharacterIcon;
    private Bitmap maskdudeIcon;
    private Rect mainCharacterButtonRect;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // Rhino karakteri
    private boolean isRhinoPurchased = false;
    private boolean isRhinoEquipped = false;
    private Bitmap[] rhinoRunFrames;
    private Bitmap rhinoIcon;
    private Rect rhinoButtonRect;




    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        coinManager = new CoinManager(getContext());
        rhinoIcon = BitmapFactory.decodeResource(getResources(), R.drawable.rhino_idle1);
        maskdudeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.maskdude_idle);
        altCharacterIcon = BitmapFactory.decodeResource(getResources(), R.drawable.maskdude_char);
        coinIcon = BitmapFactory.decodeResource(getResources(), R.drawable.coin_icon);
        marketBackground = BitmapFactory.decodeResource(getResources(), R.drawable.market_bck);
        character = BitmapFactory.decodeResource(getResources(), R.drawable.karakter);
        tas = BitmapFactory.decodeResource(getResources(), R.drawable.tas1);
        gameOverScreen = BitmapFactory.decodeResource(getResources(), R.drawable.gameover_panel);
        bullet = BitmapFactory.decodeResource(getResources(), R.drawable.mermi);
        startScreen = BitmapFactory.decodeResource(getResources(), R.drawable.start_screenpane);
        playButton = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);
        restartButton = BitmapFactory.decodeResource(getResources(), R.drawable.restart_button);
        leaderboardButton = BitmapFactory.decodeResource(getResources(), R.drawable.leaderboard_button);
        backButton = BitmapFactory.decodeResource(getResources(), R.drawable.back_button);
        leaderboardBackground = BitmapFactory.decodeResource(getResources(), R.drawable.leader_bck);
        marketButton = BitmapFactory.decodeResource(getResources(), R.drawable.market_button);
        mainCharacterIcon = BitmapFactory.decodeResource(getResources(), R.drawable.karakter);
        pinkywinkyIcon = BitmapFactory.decodeResource(getResources(), R.drawable.pinkywinky_idle);

        pinkywinkyIdle = BitmapFactory.decodeResource(getResources(), R.drawable.pinkywinky_idle);

        fruitBitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.apple),
                BitmapFactory.decodeResource(getResources(), R.drawable.bananas),
                BitmapFactory.decodeResource(getResources(), R.drawable.cherries),
                BitmapFactory.decodeResource(getResources(), R.drawable.kiwi),
                BitmapFactory.decodeResource(getResources(), R.drawable.melon),
                BitmapFactory.decodeResource(getResources(), R.drawable.orange),
                BitmapFactory.decodeResource(getResources(), R.drawable.pineapple),
                BitmapFactory.decodeResource(getResources(), R.drawable.strawberry)
        };

        for (int i = 0; i < 12; i++) {
            int resId = getResources().getIdentifier("maskdude_" + (i + 1), "drawable", context.getPackageName());
            altCharacterFrames[i] = BitmapFactory.decodeResource(getResources(), resId);
        }

        for (int i = 0; i < 12; i++) {
            int resId = getResources().getIdentifier("run_" + (i + 1), "drawable", context.getPackageName());
            runFrames[i] = BitmapFactory.decodeResource(getResources(), resId);
        }

        for (int i = 0; i < 12; i++) {
            int resId = getResources().getIdentifier("pinkywinky_" + (i + 1), "drawable", context.getPackageName());
            pinkywinkyRunFrames[i] = BitmapFactory.decodeResource(getResources(), resId);
        }
        rhinoRunFrames = new Bitmap[6];
        for (int i = 0; i < 6; i++) {
            int resId = getResources().getIdentifier("rhino_0" + (i + 1), "drawable", getContext().getPackageName());
            rhinoRunFrames[i] = BitmapFactory.decodeResource(getResources(), resId);
        }

        gameThread = new GameThread(getHolder(), this);

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(65);
        scorePaint.setFakeBoldText(true);

        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/PressStart2P-Regular.ttf");
        scorePaint.setTypeface(customFont);

        scoreManager = new ScoreManager(context);
        prefs = context.getSharedPreferences("game_data", Context.MODE_PRIVATE);
        editor = prefs.edit();


        isAltCharacterPurchased = prefs.getBoolean("altCharacterPurchased", false);
        isAltCharacterEquipped = prefs.getBoolean("altCharacterEquipped", false);

        isPinkywinkyPurchased = prefs.getBoolean("pinkywinkyPurchased", false);
        isPinkywinkyEquipped = prefs.getBoolean("pinkywinkyEquipped", false);

        isRhinoPurchased = prefs.getBoolean("isRhinoPurchased", false);
        isRhinoEquipped = prefs.getBoolean("isRhinoEquipped", false);


    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        characterX = screenWidth / 2 - character.getWidth() / 2;
        characterY = screenHeight - character.getHeight() - 225;

        background = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.arka_plani),
                screenWidth, screenHeight, false);

        startScreen = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.start_screenpane),
                screenWidth, screenHeight, false);

        gameOverScreen = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.gameover_panel),
                screenWidth, screenHeight, false);

        leaderboardBackground = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.leader_bck),
                screenWidth, screenHeight, false);

        if (gameThread.getState() == Thread.State.NEW) {
            gameThread.start();
        }
    }




    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override public void surfaceDestroyed(SurfaceHolder holder) { gameThread.stopGame(); }

    public void update() {
        // Oyun başlamadıysa veya game over olduysa hiçbir şey yapma
        if (!isGameStarted || isGameOver) return;

        if (isHolding && System.currentTimeMillis() - holdStartTime >= holdThreshold) {
            if (movingLeft && characterX > 0) {
                characterX -= characterSpeed;
            }
            if (movingRight && characterX + character.getWidth() < getWidth()) {
                characterX += characterSpeed;
            }
        }

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
            Rock r = rocks.get(i);
            r.update();

            if (r.collidesWith(characterX, characterY, character.getWidth(), character.getHeight())) {
                isGameOver = true;
                showNameInputDialog();
                return;
            }

            if (r.isOffScreen(getHeight())) {
                rocks.remove(i--);
            }
        }

        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            b.update();

            if (b.isOffScreen()) {
                bullets.remove(i--);
                continue;
            }

            for (int j = 0; j < rocks.size(); j++) {
                if (b.collidesWith(rocks.get(j))) {
                    bullets.remove(i--);
                    rocks.remove(j);
                    score += 5;
                    break;
                }
            }
        }

        if (currentTime - lastFruitTime > fruitSpawnInterval) {
            spawnFruit();
            lastFruitTime = currentTime;
        }

        for (int i = 0; i < fruits.size(); i++) {
            Fruit f = fruits.get(i);
            f.update();

            if (f.collidesWith(characterX, characterY, character.getWidth(), character.getHeight())) {
                score += f.getPoint();
                fruits.remove(i--);
            } else if (f.isOffScreen(getHeight())) {
                fruits.remove(i--);
            }
        }
    }

    private void showCostumePurchaseDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Kostüm Satın Al")
                .setMessage("Bu kostüm 500 jeton. Satın almak istiyor musunuz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    if (coinManager.getCoins() >= 500) {
                        coinManager.spendCoins(500);
                        isAltCharacterPurchased = true;
                        isAltCharacterEquipped = true;
                    } else {
                        Toast.makeText(getContext(), "Yeterli jeton yok!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hayır", null)
                .show();
    }


    // Skorları dosyadan yüklemek için eklendi
    private void loadAllScoresFromFile() {
        topScores.clear();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getContext().openFileInput("scores.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    topScores.add(new ScoreEntry(name, score));
                }
            }
            reader.close();
            Collections.sort(topScores, (a, b) -> Integer.compare(b.getScore(), a.getScore()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawGame(Canvas canvas) {
        if (canvas == null) return;

        if (isInLeaderboardScreen) {
            canvas.drawBitmap(leaderboardBackground, 0, 0, null);
            int backButtonX = 50;
            int backButtonY = 50;
            canvas.drawBitmap(backButton, backButtonX, backButtonY, null);

            int yOffset = (int) (150 + scrollOffset);
            scorePaint.setTextSize(50);
            canvas.drawText("Tüm Skorlar", getWidth() / 2 - 180, yOffset, scorePaint);
            yOffset += 70;

            int xRank = 50;
            int xName = 200;
            int xScore = getWidth() - 220;
            int rank = 1;
            scorePaint.setTextSize(40);
            for (ScoreEntry s : topScores) {
                String name = s.getName();
                if (scorePaint.measureText(name) > (xScore - xName - 20)) {
                    while (scorePaint.measureText(name + "...") > (xScore - xName - 20) && name.length() > 1) {
                        name = name.substring(0, name.length() - 1);
                    }
                    name += "...";
                }
                canvas.drawText(String.valueOf(rank), xRank, yOffset, scorePaint);
                canvas.drawText(name, xName, yOffset, scorePaint);
                canvas.drawText(String.valueOf(s.getScore()), xScore, yOffset, scorePaint);
                yOffset += 50;
                rank++;
            }
            return;
        }

        if (!isGameStarted) {
            canvas.drawBitmap(startScreen, 0, 0, null);
            int buttonX = getWidth() / 2 - playButton.getWidth() / 2;
            int buttonY = getHeight() / 2 + 150 - playButton.getHeight() / 2 + 150;
            canvas.drawBitmap(playButton, buttonX, buttonY, null);

            int marketButtonX = buttonX;
            int marketButtonY = buttonY + playButton.getHeight() + 50;
            canvas.drawBitmap(marketButton, marketButtonX, marketButtonY, null);
        } else {
            canvas.drawBitmap(background, 0, 0, null);

            Bitmap characterToDraw;
            if (isRhinoEquipped) {
                characterToDraw = rhinoRunFrames[currentRunFrame % rhinoRunFrames.length];
            } else if (isPinkywinkyEquipped) {
                characterToDraw = pinkywinkyRunFrames[currentRunFrame];
            } else if (isAltCharacterEquipped) {
                characterToDraw = altCharacterFrames[currentRunFrame];
            } else {
                characterToDraw = runFrames[currentRunFrame];
            }

            if (movingLeft) {
                canvas.drawBitmap(flipBitmap(characterToDraw), characterX, characterY, null);
            } else if (movingRight) {
                canvas.drawBitmap(characterToDraw, characterX, characterY, null);
            } else {
                if (isRhinoEquipped) {
                    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rhino_idle1), characterX, characterY, null);
                } else if (isPinkywinkyEquipped) {
                    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pinkywinky_idle), characterX, characterY, null);
                } else if (isAltCharacterEquipped) {
                    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.maskdude_idle), characterX, characterY, null);
                } else {
                    canvas.drawBitmap(character, characterX, characterY, null);
                }
            }

            for (Rock r : rocks) r.draw(canvas);
            for (Bullet b : bullets) b.draw(canvas);
            for (Fruit f : fruits) f.draw(canvas);

            scorePaint.setTextSize(55);
            canvas.drawText("Puan: " + score, 50, 150, scorePaint);

            if (isGameOver) {
                canvas.drawBitmap(gameOverScreen, 0, 0, null);
                int restartButtonX = getWidth() / 2 - restartButton.getWidth() / 2;
                int restartButtonY = getHeight() / 2 + 100;
                canvas.drawBitmap(restartButton, restartButtonX, restartButtonY, null);

                int leaderboardButtonX = getWidth() / 2 - leaderboardButton.getWidth() / 2;
                int leaderboardButtonY = restartButtonY + restartButton.getHeight() + 40;
                canvas.drawBitmap(leaderboardButton, leaderboardButtonX, leaderboardButtonY, null);

                int yOffset = getHeight() / 2 - 700;
                scorePaint.setTextSize(40);
                canvas.drawText("En Yüksek Skorlar", 50, yOffset, scorePaint);
                yOffset += 70;

                int xRank = 30;
                int xName = 200;
                int xScore = getWidth() - 220;

                scorePaint.setTextSize(35);
                canvas.drawText("Sıra", xRank, yOffset, scorePaint);
                scorePaint.setTextSize(40);
                canvas.drawText("İsim", xName, yOffset, scorePaint);
                canvas.drawText("Puan", xScore, yOffset, scorePaint);
                yOffset += 60;

                int rank = 1;
                for (ScoreEntry s : topScores) {
                    if (rank > 10) break;
                    String name = s.getName();
                    if (scorePaint.measureText(name) > (xScore - xName - 20)) {
                        while (scorePaint.measureText(name + "...") > (xScore - xName - 20) && name.length() > 1) {
                            name = name.substring(0, name.length() - 1);
                        }
                        name += "...";
                    }
                    canvas.drawText(String.valueOf(rank), xRank, yOffset, scorePaint);
                    canvas.drawText(name, xName, yOffset, scorePaint);
                    canvas.drawText(String.valueOf(s.getScore()), xScore, yOffset, scorePaint);
                    yOffset += 50;
                    rank++;
                }

                if (!scoreSaved) {
                    coinManager.addCoins(score);
                    scoreSaved = true;
                }

                scorePaint.setTextSize(55);
            }
        }

        if (isInMarketScreen) {
            int screenWidth = getWidth();
            int screenHeight = getHeight();

            Bitmap scaledMarketBackground = Bitmap.createScaledBitmap(marketBackground, screenWidth, screenHeight, true);
            canvas.drawBitmap(scaledMarketBackground, 0, 0, null);

            int backX = 50;
            int backY = 50;
            canvas.drawBitmap(backButton, backX, backY, null);

            scorePaint.setTextSize(40);
            scorePaint.setColor(Color.YELLOW);
            int iconSize = 60;
            Bitmap scaledIcon = Bitmap.createScaledBitmap(coinIcon, iconSize, iconSize, true);
            int iconX = backX + backButton.getWidth() + 20;
            int iconY = backY + (backButton.getHeight() - iconSize) / 2;
            canvas.drawBitmap(scaledIcon, iconX, iconY, null);
            canvas.drawText("Jeton: " + coinManager.getCoins(), iconX + iconSize + 10, iconY + iconSize - 10, scorePaint);

            int iconWidth = screenWidth / 4;
            int iconHeight = iconWidth;
            int verticalSpacing = 100;
            int row1Y = screenHeight / 3;
            int row2Y = row1Y + iconHeight + verticalSpacing;

            int col1X = screenWidth / 4 - iconWidth / 2;
            int col2X = 3 * screenWidth / 4 - iconWidth / 2;

            // Ana karakter
            Bitmap scaledMainIcon = Bitmap.createScaledBitmap(mainCharacterIcon, iconWidth, iconHeight, false);
            canvas.drawBitmap(scaledMainIcon, col1X, row1Y, null);
            mainCharacterButtonRect = new Rect(col1X, row1Y, col1X + iconWidth, row1Y + iconHeight);
            scorePaint.setColor(Color.WHITE);
            scorePaint.setTextSize(35);
            canvas.drawText("Ana Karakter", col1X + iconWidth / 2 - scorePaint.measureText("Ana Karakter") / 2,
                    row1Y + iconHeight + 40, scorePaint);

            // MaskDude
            Bitmap scaledAltIcon = Bitmap.createScaledBitmap(altCharacterIcon, iconWidth, iconHeight, false);
            canvas.drawBitmap(scaledAltIcon, col2X, row1Y, null);
            altCharacterButtonRect = new Rect(col2X, row1Y, col2X + iconWidth, row1Y + iconHeight);
            String altLabel = isAltCharacterEquipped ? "Kuşanıldı" : (isAltCharacterPurchased ? "Kuşan" : "500 Jeton");
            canvas.drawText(altLabel, col2X + iconWidth / 2 - scorePaint.measureText(altLabel) / 2,
                    row1Y + iconHeight + 40, scorePaint);

            // PinkyWinky
            Bitmap scaledPinkyIcon = Bitmap.createScaledBitmap(pinkywinkyIcon, iconWidth, iconHeight, false);
            canvas.drawBitmap(scaledPinkyIcon, col1X, row2Y, null);
            pinkywinkyButtonRect = new Rect(col1X, row2Y, col1X + iconWidth, row2Y + iconHeight);
            String pinkyLabel = isPinkywinkyEquipped ? "Kuşanıldı" : (isPinkywinkyPurchased ? "Kuşan" : "100000 Jeton");
            canvas.drawText(pinkyLabel, col1X + iconWidth / 2 - scorePaint.measureText(pinkyLabel) / 2,
                    row2Y + iconHeight + 40, scorePaint);

            // Rhino (sağ alt)
            Bitmap scaledRhinoIcon = Bitmap.createScaledBitmap(rhinoIcon, iconWidth, iconHeight, false);
            canvas.drawBitmap(scaledRhinoIcon, col2X, row2Y, null);
            rhinoButtonRect = new Rect(col2X, row2Y, col2X + iconWidth, row2Y + iconHeight);
            String rhinoLabel = isRhinoEquipped ? "Kuşanıldı" : (isRhinoPurchased ? "Kuşan" : "75000 Jeton");
            canvas.drawText(rhinoLabel, col2X + iconWidth / 2 - scorePaint.measureText(rhinoLabel) / 2,
                    row2Y + iconHeight + 40, scorePaint);

            return;
        }
    }












    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        // >>> Liderlik Ekranı <<<
        if (isInLeaderboardScreen) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchY = y;

                    int backButtonX = 50;
                    int backButtonY = 50;
                    if (x >= backButtonX && x <= backButtonX + backButton.getWidth() &&
                            y >= backButtonY && y <= backButtonY + backButton.getHeight()) {
                        isInLeaderboardScreen = false;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    float dy = y - lastTouchY;
                    lastTouchY = y;
                    scrollOffset += dy;

                    int totalScoreHeight = topScores.size() * SCORE_LINE_HEIGHT;
                    int visibleHeight = getHeight() - 300;
                    int maxScroll = 0;
                    int minScroll = Math.min(0, visibleHeight - totalScoreHeight);

                    scrollOffset = Math.max(minScroll, Math.min(maxScroll, scrollOffset));
                    break;
            }
            return true;
        }

        // >>> Game Over Ekranı <<<
        if (isGameOver) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int restartButtonX = getWidth() / 2 - restartButton.getWidth() / 2;
                int restartButtonY = getHeight() / 2 + 100;

                if (x >= restartButtonX && x <= restartButtonX + restartButton.getWidth() &&
                        y >= restartButtonY && y <= restartButtonY + restartButton.getHeight()) {
                    resetGame();
                    return true;
                }

                int leaderboardButtonX = getWidth() / 2 - leaderboardButton.getWidth() / 2;
                int leaderboardButtonY = restartButtonY + restartButton.getHeight() + 40;

                if (x >= leaderboardButtonX && x <= leaderboardButtonX + leaderboardButton.getWidth() &&
                        y >= leaderboardButtonY && y <= leaderboardButtonY + leaderboardButton.getHeight()) {
                    loadAllScoresFromFile();
                    isInLeaderboardScreen = true;
                    return true;
                }
            }
            return true;
        }

        // >>> Market Ekranı <<<
        if (isInMarketScreen) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int backX = 50;
                int backY = 50;

                if (x >= backX && x <= backX + backButton.getWidth() &&
                        y >= backY && y <= backY + backButton.getHeight()) {
                    isInMarketScreen = false;
                    return true;
                }

                // Alt karakter (maskdude)
                if (altCharacterButtonRect != null && altCharacterButtonRect.contains((int) x, (int) y)) {
                    if (!isAltCharacterPurchased) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Kostüm Satın Al")
                                .setMessage("Bu kostümü 500 jetona satın almak istiyor musun?")
                                .setPositiveButton("Evet", (dialog, which) -> {
                                    if (coinManager.getCoins() >= 500) {
                                        coinManager.spendCoins(500);
                                        isAltCharacterPurchased = true;
                                        isAltCharacterEquipped = true;
                                        isPinkywinkyEquipped = false;
                                        isRhinoEquipped = false;

                                        editor.putBoolean("altCharacterPurchased", true);
                                        editor.putBoolean("altCharacterEquipped", true);
                                        editor.putBoolean("pinkywinkyEquipped", false);
                                        editor.putBoolean("rhinoEquipped", false);
                                        editor.apply();

                                        Toast.makeText(getContext(), "Satın alındı ve kuşanıldı!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Yeterli jeton yok!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Hayır", null)
                                .show();
                    } else {
                        isAltCharacterEquipped = true;
                        isPinkywinkyEquipped = false;
                        isRhinoEquipped = false;

                        editor.putBoolean("altCharacterEquipped", true);
                        editor.putBoolean("pinkywinkyEquipped", false);
                        editor.putBoolean("rhinoEquipped", false);
                        editor.apply();

                        Toast.makeText(getContext(), "Kostüm kuşanıldı!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

                // PinkyWinky karakteri
                if (pinkywinkyButtonRect != null && pinkywinkyButtonRect.contains((int) x, (int) y)) {
                    if (!isPinkywinkyPurchased) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Kostüm Satın Al")
                                .setMessage("Bu kostümü 100000 jetona satın almak istiyor musun?")
                                .setPositiveButton("Evet", (dialog, which) -> {
                                    if (coinManager.getCoins() >= 100000) {
                                        coinManager.spendCoins(100000);
                                        isPinkywinkyPurchased = true;
                                        isPinkywinkyEquipped = true;
                                        isAltCharacterEquipped = false;
                                        isRhinoEquipped = false;

                                        editor.putBoolean("pinkywinkyPurchased", true);
                                        editor.putBoolean("pinkywinkyEquipped", true);
                                        editor.putBoolean("altCharacterEquipped", false);
                                        editor.putBoolean("rhinoEquipped", false);
                                        editor.apply();

                                        Toast.makeText(getContext(), "PinkyWinky satın alındı ve kuşanıldı!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Yeterli jeton yok!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Hayır", null)
                                .show();
                    } else {
                        isPinkywinkyEquipped = true;
                        isAltCharacterEquipped = false;
                        isRhinoEquipped = false;

                        editor.putBoolean("pinkywinkyEquipped", true);
                        editor.putBoolean("altCharacterEquipped", false);
                        editor.putBoolean("rhinoEquipped", false);
                        editor.apply();

                        Toast.makeText(getContext(), "PinkyWinky kuşanıldı!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

                // Rhino karakteri
                if (rhinoButtonRect != null && rhinoButtonRect.contains((int) x, (int) y)) {
                    if (!isRhinoPurchased) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Kostüm Satın Al")
                                .setMessage("Bu kostümü 75000 jetona satın almak istiyor musun?")
                                .setPositiveButton("Evet", (dialog, which) -> {
                                    if (coinManager.getCoins() >= 75000) {
                                        coinManager.spendCoins(75000);
                                        isRhinoPurchased = true;
                                        isRhinoEquipped = true;
                                        isAltCharacterEquipped = false;
                                        isPinkywinkyEquipped = false;

                                        editor.putBoolean("rhinoPurchased", true);
                                        editor.putBoolean("rhinoEquipped", true);
                                        editor.putBoolean("altCharacterEquipped", false);
                                        editor.putBoolean("pinkywinkyEquipped", false);
                                        editor.apply();

                                        Toast.makeText(getContext(), "Rhino satın alındı ve kuşanıldı!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Yeterli jeton yok!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Hayır", null)
                                .show();
                    } else {
                        isRhinoEquipped = true;
                        isAltCharacterEquipped = false;
                        isPinkywinkyEquipped = false;

                        editor.putBoolean("rhinoEquipped", true);
                        editor.putBoolean("altCharacterEquipped", false);
                        editor.putBoolean("pinkywinkyEquipped", false);
                        editor.apply();

                        Toast.makeText(getContext(), "Rhino kuşanıldı!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }


                // Ana karakter
                if (mainCharacterButtonRect != null && mainCharacterButtonRect.contains((int) x, (int) y)) {
                    isAltCharacterEquipped = false;
                    isPinkywinkyEquipped = false;
                    isRhinoEquipped = false;

                    editor.putBoolean("altCharacterEquipped", false);
                    editor.putBoolean("pinkywinkyEquipped", false);
                    editor.putBoolean("rhinoEquipped", false);
                    editor.apply();

                    Toast.makeText(getContext(), "Ana karakter kuşanıldı!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return true;
        }

        

        // >>> Başlangıç Ekranı <<<
        if (!isGameStarted && event.getAction() == MotionEvent.ACTION_DOWN) {
            int marketButtonX = getWidth() / 2 - marketButton.getWidth() / 2;
            int marketButtonY = getHeight() / 2 + 150 - playButton.getHeight() / 2 + 150 + playButton.getHeight() + 50;

            if (x >= marketButtonX && x <= marketButtonX + marketButton.getWidth() &&
                    y >= marketButtonY && y <= marketButtonY + marketButton.getHeight()) {
                isInMarketScreen = true;
                return true;
            }

            int buttonX = getWidth() / 2 - playButton.getWidth() / 2;
            int buttonY = getHeight() / 2 + 150 - playButton.getHeight() / 2 + 150;

            if (x >= buttonX && x <= buttonX + playButton.getWidth() &&
                    y >= buttonY && y <= buttonY + playButton.getHeight()) {
                isGameStarted = true;
                return true;
            }
        }

        // >>> Oyun içi kontroller <<<
        if (!isGameStarted || isGameOver) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isHolding = true;
                holdStartTime = System.currentTimeMillis();

                if (System.currentTimeMillis() - lastClickTime < doubleClickTimeDelta) {
                    bullets.add(new Bullet(bullet,
                            characterX + character.getWidth() / 2 - bullet.getWidth() / 2,
                            characterY));
                } else {
                    movingLeft = x < getWidth() / 2;
                    movingRight = !movingLeft;
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
        int type = random.nextInt(fruitBitmaps.length);
        int x = random.nextInt(getWidth() - fruitBitmaps[type].getWidth());
        fruits.add(new Fruit(fruitBitmaps[type], x, 0, 10, fruitPoints[type]));
    }

    private void resetGame() {
        isGameOver = false;
        isGameStarted = false;
        score = 0;
        scoreSaved = false;  // <-- BU SATIRI EKLE!
        characterX = getWidth() / 2;
        characterY = getHeight() - character.getHeight() - 200;

        rocks.clear();
        fruits.clear();
        bullets.clear();

        movingLeft = false;
        movingRight = false;
    }


    private Bitmap flipBitmap(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1f, 1f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
    }

    private void showNameInputDialog() {
        ((MainActivity) getContext()).runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("İsmini Gir:");

            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setCancelable(false);
            builder.setPositiveButton("Tamam", (dialog, which) -> {
                String name = input.getText().toString().trim();
                if (name.isEmpty()) name = "Bilinmeyen";
                scoreManager.addScore(name, score);
                topScores = scoreManager.getTopScores(10);
            });

            builder.show();
        });
    }
}
