package game;

import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Stores and handles with everything unique to level 2.
 */
public class LevelTwo extends SuperLevel {

    /**
     * A constructor. Called when loading player saves.
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     * @param num A parameter used to overload constructors.
     */
    public LevelTwo(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves, int num) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Industrial/");
    }

    /**
     * Secondary constructor called when user created levels are loaded in order retrieve the background for that user level.
     */
    public LevelTwo() { readFolder("data/Backgrounds/Industrial/"); }

    /**
     * A third constructor. Called when loading the default levels (Level 1-6).
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     */
    public LevelTwo(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Industrial/");
        this.generateMap("data/Levels/Level 2.txt");
    }

    /**
     * Will draw the backgrounds corresponding with this level.
     * @param g Instance of Graphics2D.
     * @param x The new x position of the background.
     * @param width The width of the background.
     * @param height The height of the background.
     * @param rand Instance of Random.
     * @param xBound The current screen shake amount in the x direction.
     * @param yBound The current screen shake amount in the y direction.
     */
    @Override
    public void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        g.drawImage(iconHashMap.get("Background").getImage(), 0, 0, (int) (width * 1.05f), height, view);

        view.backgroundTiling(g, iconHashMap.get("industrialFar"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 450 + (rand.nextInt(yBound) - yBound / 2), 3f);
        view.backgroundTiling(g, iconHashMap.get("industrialMid"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 380 + (rand.nextInt(yBound) - yBound / 2), 2.5f);
        view.backgroundTiling(g, iconHashMap.get("industrialClose"), -400 + x * -10 + (rand.nextInt(xBound) - xBound / 2f), height - 360 + (rand.nextInt(yBound) - yBound / 2), 3.5f);
    }

    /**
     * Will draw the foregrounds related to this level.
     * @param g Instance of Graphics2D.
     * @param x The new x position of the background.
     * @param width The width of the background.
     * @param height The height of the background/
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
        return new Vec2(3, 20);
    }
}
