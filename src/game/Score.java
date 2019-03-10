package game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Score {
    private float prevScore;
    private float highScore;
    private float currScore;
    private float currTime;
    private float lowTimeTaken;
    private boolean scoreChanged = false;
    private float changeAmount;

    Score(Game game) {
        currScore = 0;
        currTime = 0;
        findScoreInfo(game);
    }

    private void findScoreInfo(Game game) {
        String line;
        String[] text;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/Score.txt"));
            while ((line = reader.readLine()) != null) {
                text = line.split(":");
                if (text[0].equals(game.getCurrentLevel())) {
                    text = text[1].split(",");
                    highScore = Float.parseFloat(text[0]);
                    prevScore = Float.parseFloat(text[1]);
                    lowTimeTaken = Float.parseFloat(text[2]);
                    System.out.println("High: " + highScore + ", Prev: " + prevScore + ", Time: " + lowTimeTaken);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void computeScore(int playerHealth, int playerArmour, float currTime) {
        currScore -= (currTime + (playerHealth*0.25f + playerArmour*0.12f));
    }

    public float getPrevScore() {
        return prevScore;
    }

    public void setHighScore(float highScore) {
        this.highScore = highScore;
    }

    public float getHighScore() {
        return highScore;
    }

    public float getCurrScore() {
        return currScore;
    }

    public void setCurrScore(float currScore) {
        this.currScore = currScore;
    }

    public void increaseScore(float currScore) {
        changeAmount += currScore;
        scoreChanged = true;
    }

    public float getLowTimeTaken() {
        return lowTimeTaken;
    }

    public float getCurrTime() {
        return currTime;
    }

    public void setCurrTime(float currTime) {
        this.currTime = currTime;
    }

    public boolean isScoreChanged() {
        return scoreChanged;
    }

    public void setScoreChanged(boolean scoreChanged) {
        this.scoreChanged = scoreChanged;
    }

    public float getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(float changeAmount) {
        this.changeAmount = changeAmount;
    }
}
