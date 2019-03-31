package game;

import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Stores and handles with everything unique to level 4.
 */
public class LevelFour extends SuperLevel {

    /**
     * A constructor Called when loading player saves.
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     * @param num A parameter used to overload constructors.
     */
    public LevelFour(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves, int num) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Dungeon/");
    }

    /**
     * Secondary constructor called when user created levels are loaded in order retrieve the background for that user level.
     */
    public LevelFour() { readFolder("data/Backgrounds/Dungeon/"); }

    /**
     * A third constructor. Called when loading the default levels (Level 1-6).
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     */
    public LevelFour(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Dungeon/");
        this.generateMap("data/Levels/Level 4.txt");
    }

    /**
     * Will draw the backgrounds corresponding with this level.
     * @param g Instance of Graphics2D.
     * @param x The new x position of the background.
     * @param width The width of the background.
     * @param height The height of the background/
     * @param rand Instance of Random.
     * @param xBound The current screen shake amount in the x direction.
     * @param yBound The current screen shake amount in the y direction.
     */
    @Override
    public void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        g.drawImage(iconHashMap.get("Background").getImage(), 0, 0, (int) (width * 1.05f), height, view);

        view.backgroundTiling(g, iconHashMap.get("dungeonFar"), -400 + x * -2 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 8f);
        view.backgroundTiling(g, iconHashMap.get("dungeonMid"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 520 + (rand.nextInt(yBound) - yBound / 2), 4.5f);
        view.backgroundTiling(g, iconHashMap.get("dungeonClose"), -x * 11 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 3.5f);
    }

    /**
     * Will draw the foregrounds related to this level.
     * @param g Instance of Graphics2D.
     * @param x The new x position of the background.
     * @param width The width of the background.
     * @param height The height of the background.
     * @param rand Instance of Random.
     * @param xBound The current screen shake amount in the x direction.
     * @param yBound The current screen shake amount in the y direction.
     */
    @Override
    public void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {}

    /**
     * @return the starting position of the player as type Vec2.
     */
    @Override
    public Vec2 startPosition() {
        return new Vec2(-8.172528f, 20);
    }
}