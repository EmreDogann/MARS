package game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * The high, previous, and current score that is displayed in the game HUD.
 */
public class Score {
    private float prevScore;
    private float highScore;
    private float currScore;
    private float currTime;
    //The shortest time taken to complete a level.
    private float lowTimeTaken;
    private boolean scoreChanged = false;
    //The amount to change the current score by.
    private float changeAmount;

    /**
     * Constructor for Score.
     * @param game Instance of Game.
     */
    public Score(Game game) {
        currScore = 0;
        currTime = 0;
        findScoreInfo(game);
    }

    private void findScoreInfo(Game game) {
        //Read the score information (previous, high, current score and shortest time) from data/Score.txt
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

    /**
     * Calculates the final score for that level once the player reaches the end of the level.
     * @param playerHealth The player's health at the end of the level.
     * @param playerArmour The player's armour at the end of the level.
     * @param currTime The amount of time the player took to complete the level.
     */
    public void computeScore(int playerHealth, int playerArmour, float currTime) {
        currScore -= (currTime + (playerHealth*0.45f + playerArmour*0.35f));
    }

    /**
     * @return the previous score.
     */
    public float getPrevScore() {
        return prevScore;
    }

    /**
     * Update the previous score with a new one.
     * @param prevScore The new previous score.
     */
    public void setPrevScore(float prevScore) {
        this.prevScore = prevScore;
    }

    /**
     * Update the high score with a new one.
     * @param highScore The new high score.
     */
    public void setHighScore(float highScore) {
        this.highScore = highScore;
    }

    /**
     * @return the current high score.
     */
    public float getHighScore() {
        return highScore;
    }

    /**
     * @return the current score.
     */
    public float getCurrScore() {
        return currScore;
    }

    /**
     * Update the current score with a new one.
     * @param currScore The current score.
     */
    public void setCurrScore(float currScore) {
        this.currScore = currScore;
    }

    /**
     * Increase the current score by a specified amount.
     * @param score The amount to add to the score.
     */
    public void increaseScore(float score) {
        changeAmount += score;
        scoreChanged = true;
    }

    /**
     * @return the current time.
     */
    public float getCurrTime() {
        return currTime;
    }

    /**
     * Updates the current time with a new one.
     * @param currTime the new time.
     */
    public void setCurrTime(float currTime) {
        this.currTime = currTime;
    }

    /**
     * @return Whether or not the current score has been changed.
     */
    public boolean isScoreChanged() {
        return scoreChanged;
    }

    /**
     * @param scoreChanged New boolean on whether or not the current score has been changed.
     */
    public void setScoreChanged(boolean scoreChanged) {
        this.scoreChanged = scoreChanged;
    }

    /**
     * @return the amount to increase the current score by.
     */
    public float getChangeAmount() {
        return changeAmount;
    }

    /**
     * @param changeAmount How much to increase the current score by over time.
     */
    public void setChangeAmount(float changeAmount) {
        this.changeAmount = changeAmount;
    }
}
