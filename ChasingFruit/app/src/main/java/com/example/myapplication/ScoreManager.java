package com.example.myapplication;

import android.content.Context;

import java.io.*;
import java.util.*;

public class ScoreManager {
    private File file;

    public ScoreManager(Context context) {
        file = new File(context.getFilesDir(), "scores.txt");
    }

    public void addScore(String name, int score) {
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(name + "," + score + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ScoreEntry> getTopScores(int count) {
        List<ScoreEntry> scores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scores.add(new ScoreEntry(name, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        scores.sort((a, b) -> b.getScore() - a.getScore());
        return scores.subList(0, Math.min(count, scores.size()));
    }
}
