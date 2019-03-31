package game;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Stores and handles with everything unique to level 3.
 */
public class LevelThree extends SuperLevel implements StepListener {

    /**
     * A constructor. Called when loading player saves.
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     * @param num A parameter used to overload constructors.
     */
    public LevelThree(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves, int num) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Mine/");
        //The path that each specified enemy has to follow.
        float[][] enemyStartingPos = {{-3.4768043f, 3.1f}, {82.22112f, 88.82362f}, {92.05706f, 98.37362f}, {111.566734f, 101.67362f}, {91.82831f, 98.52362f}, {128.41661f, 131.65659f}, {138.05182f, 134.89435f}};
        //Find all the enemies that are supposed to be moving.
        this.findMovingEnemies(enemyStartingPos);
        this.addStepListener(this);
    }

    /**
     * Secondary constructor called when user created levels are loaded in order retrieve the background for that user level.
     */
    public LevelThree() {
        readFolder("data/Backgrounds/Mine/");
    }

    /**
     * A third constructor. Called when loading the default levels (Level 1-6).
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     */
    public LevelThree(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Mine/");
        this.generateMap("data/Levels/Level 3.txt");
        //The path that each specified enemy has to follow.
        float[][] enemyStartingPos = {{-3.4768043f, 3.1f}, {82.22112f, 88.82362f}, {92.05706f, 98.37362f}, {111.566734f, 101.67362f}, {91.82831f, 98.52362f}, {128.41661f, 131.65659f}, {138.05182f, 134.89435f}};
        //Find all the enemies that are supposed to be moving.
        this.findMovingEnemies(enemyStartingPos);
        this.addStepListener(this);
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

        view.backgroundTiling(g, iconHashMap.get("mineFarTop"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 4f);
        view.backgroundTiling(g, iconHashMap.get("mineFarDown"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 220 + (rand.nextInt(yBound) - yBound / 2), 4f);
        view.backgroundTiling(g, iconHashMap.get("mineMidTop"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 3.5f);
        view.backgroundTiling(g, iconHashMap.get("mineMidDown"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 220 + (rand.nextInt(yBound) - yBound / 2), 3.5f);
        view.backgroundTiling(g, iconHashMap.get("mineClose"), -x * 10 + (rand.nextInt(xBound) - xBound / 2f), height - 310 + (rand.nextInt(yBound) - yBound / 2), 4.5f);
    }

    /**
     * Not used by this level.
     */
    @Override
    public void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {}

    /**
     * @return the starting position of the player as type Vec2.
     */
    @Override
    public Vec2 startPosition() {
        return new Vec2(-27.7f, 20);
    }

    /**
     * Called at the start of every step by the StepListener. Will handle the back and forth movement of enemies.
     * @param stepEvent Holds information about the current step.
     */
    @Override
    @SuppressWarnings("Duplicates")
    public void preStep(StepEvent stepEvent) {
        //Move each enemy in movingEnemies along their respective path.
        for (Enemy enemy : movingEnemies) {
            //If the enemy is moving from the left to right...
            if (enemyPath.get(enemy)[0] > enemyPath.get(enemy)[1]) {
                //If the enemy has moved outside of its path, make it move in the opposite direction.
                if (enemy.getPosition().x >= enemyPath.get(enemy)[0]) {
                    enemy.startWalking(-5);
                } else if (enemy.getPosition().x <= enemyPath.get(enemy)[1]) {
                    enemy.startWalking(5);
                }
            } else { //If the enemy is moving from the right to left...
                //If the enemy has moved outside of its path, make it move in the opposite direction.
                if (enemy.getPosition().x <= enemyPath.get(enemy)[0]) {
                    enemy.startWalking(5);
                } else if (enemy.getPosition().x >= enemyPath.get(enemy)[1]) {
                    enemy.startWalking(-5);
                }
            }
        }
    }

    /**
     * Not used. Included because of interface.
     */
    @Override
    public void postStep(StepEvent stepEvent) {
    }
}
