package game;

import city.cs.engine.*;
import city.cs.engine.Shape;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Stores and handles with everything unique to level 5.
 */
public class LevelFive extends SuperLevel implements StepListener {

    /**
     * A constructor. Called when loading player saves.
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     * @param num A parameter used to overload constructors.
     */
    public LevelFive(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves, int num) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Crystal/");
        this.addStepListener(this);
    }

    /**
     * Secondary constructor called when user created levels are loaded in order retrieve the background for that user level.
     */
    public LevelFive() { readFolder("data/Backgrounds/Crystal/"); }

    /**
     * A third constructor. Called when loading the default levels (Level 1-6).
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     */
    public LevelFive(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Crystal/");
        this.generateMap("data/Levels/Level 5.txt");
        //The path that each specified enemy has to follow.
        float[][] enemyStartingPos = {{-24.676655f, 85.5732f}, {-21.426655f, 85.5732f}, {-18.126656f, 85.5732f}, {-14.950026f, 85.5732f}, {-11.56385f, 85.5732f}, {-8.320067f, 85.5732f}, {-5.1464286f, 85.5732f},
                {-1.9980118f, 85.5732f}, {75.83998f, -1.9390122f}, {72.72107f, -1.9390122f}, {69.34975f, -1.9390122f}, {66.19206f, -1.9390122f}, {62.88207f, -1.9390122f}, {79.0232f, -1.9390122f}, {82.3732f, -1.9390122f},
                {85.4732f, -1.9390122f}};
        //Find all the enemies that are supposed to be moving.
        findMovingEnemies(enemyStartingPos);
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

        view.backgroundTiling(g, iconHashMap.get("crystalFar"), -400 + x * -3 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 7.85f);
        view.backgroundTiling(g, iconHashMap.get("crystalMid"), 350 - x * 5 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 7.85f);
        view.backgroundTiling(g, iconHashMap.get("crystalClose"), -350 + -x * 11 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 7.85f);
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
    @SuppressWarnings("Duplicates")
    public void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        //Draw the enemy health bars.
        for (Enemy enemy : enemies) {
            ImageIcon healthFull = view.getHealthFull();
            ImageIcon barEmpty = view.getBarEmpty();
            Point2D.Float enemyCoordinates = view.worldToView(enemy.getPosition());
            g.drawImage(barEmpty.getImage(), (int)(enemyCoordinates.x-25), (int)(enemyCoordinates.y-25), (int) (barEmpty.getIconWidth() * 0.5f), (int) (barEmpty.getIconHeight() * 0.5f), view);
            g.drawImage(healthFull.getImage(), (int)(enemyCoordinates.x-25), (int)(enemyCoordinates.y-25), (int) (healthFull.getIconWidth() * 0.5f * enemy.getHealth()) / 100, (int) (healthFull.getIconHeight() * 0.5f), view);
        }
    }

    /**
     * @return the starting position of the player as type Vec2.
     */
    @Override
    public Vec2 startPosition() {
        return new Vec2(5, 20);
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