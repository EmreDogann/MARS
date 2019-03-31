package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Stores and handles with everything unique to level 6.
 */
public class LevelSix extends SuperLevel implements StepListener {

    /**
     * A constructor. Called when loading player saves.
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     * @param num A parameter used to overload constructors.
     */
    public LevelSix(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves, int num) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Lava/");
        //The path that each specified enemy has to follow.
        float[][] enemyStartingPos = {{232.80914f, 239.3536f}, {242.48878f, 248.9536f}, {255.62686f, 259.0003f}, {268.66754f, 271.9003f}, {275.3787f, 278.1884f}, {285.1598f, 281.94852f}};
        //Find all the enemies that are supposed to be moving.
        findMovingEnemies(enemyStartingPos);
        this.addStepListener(this);
        createSensor(game);
    }

    /**
     * Secondary constructor called when user created levels are loaded in order retrieve the background for that user level.
     */
    public LevelSix() { readFolder("data/Backgrounds/Lava/"); }

    /**
     * A third constructor. Called when loading the default levels (Level 1-6).
     * @param availableLevels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     */
    public LevelSix(ArrayList<String> availableLevels, Game game, ArrayList<String> availableSaves) {
        super(STATE.GAME, availableLevels, game, availableSaves);
        readFolder("data/Backgrounds/Lava/");
        this.generateMap("data/Levels/Level 6.txt");
        //The path that each specified enemy has to follow.
        float[][] enemyStartingPos = {{232.80914f, 239.3536f}, {242.48878f, 248.9536f}, {255.62686f, 259.0003f}, {268.66754f, 271.9003f}, {275.3787f, 278.1884f}, {285.1598f, 281.94852f}};
        //Find all the enemies that are supposed to be moving.
        findMovingEnemies(enemyStartingPos);
        this.addStepListener(this);
        createSensor(game);
    }

    private void createSensor(Game game) {
        BoxShape shape = new BoxShape(2, 50);
        StaticBody body = new StaticBody(this, shape);
        Sensor sensor = new Sensor(body, shape);
        body.setFillColor(new Color(0, 0, 0, 0));
        body.setLineColor(new Color(0, 0, 0, 0));
        body.setPosition(new Vec2(78, -3f));
        sensor.addSensorListener(new CollisionHandler(game));

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

        view.backgroundTiling(g, iconHashMap.get("lavaFar"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 510 + (rand.nextInt(yBound) - yBound / 2), 5.8f);
        view.backgroundTiling(g, iconHashMap.get("lavaMid"), 50 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 490 + (rand.nextInt(yBound) - yBound / 2), 5.3f);
        view.backgroundTiling(g, iconHashMap.get("lavaClose"), -x * 11 + (rand.nextInt(xBound) - xBound / 2f), height - 510 + (rand.nextInt(yBound) - yBound / 2), 5.8f);
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
    @SuppressWarnings("Duplicates")
    public void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
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
        return new Vec2(-11.922736f, 20);
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