package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class LevelSix extends SuperLevel implements StepListener {

    LevelSix() { readFolder("data/Backgrounds/Lava/"); }

    LevelSix(ArrayList<String> availableLevels, Game game) {
        super(STATE.GAME, availableLevels, game);
        readFolder("data/Backgrounds/Lava/");
        this.generateMap("data/Levels/Level 6.txt");
        float[][] enemyStartingPos = {{232.80914f, 239.3536f}, {242.48878f, 248.9536f}, {255.62686f, 259.0003f}, {268.66754f, 271.9003f}, {275.3787f, 278.1884f}, {285.1598f, 281.94852f}};
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

    @Override
    void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        g.drawImage(iconHashMap.get("Background").getImage(), 0, 0, (int) (width * 1.05f), height, view);

        view.backgroundTiling(g, iconHashMap.get("lavaFar"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 510 + (rand.nextInt(yBound) - yBound / 2), 5.8f);
        view.backgroundTiling(g, iconHashMap.get("lavaMid"), 50 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 490 + (rand.nextInt(yBound) - yBound / 2), 5.3f);
        view.backgroundTiling(g, iconHashMap.get("lavaClose"), -x * 11 + (rand.nextInt(xBound) - xBound / 2f), height - 510 + (rand.nextInt(yBound) - yBound / 2), 5.8f);
    }

    @Override
    void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {}

    @Override
    public Vec2 startPosition() {
        return new Vec2(-11.922736f, 20);
    }

    @Override
    public void preStep(StepEvent stepEvent) {
        for (Enemy enemy : movingEnemies) {
            if (enemyPath.get(enemy)[0] > enemyPath.get(enemy)[1]) {
                if (enemy.getPosition().x >= enemyPath.get(enemy)[0]) {
                    enemy.startWalking(-5);
                } else if (enemy.getPosition().x <= enemyPath.get(enemy)[1]) {
                    enemy.startWalking(5);
                }
            } else {
                if (enemy.getPosition().x <= enemyPath.get(enemy)[0]) {
                    enemy.startWalking(5);
                } else if (enemy.getPosition().x >= enemyPath.get(enemy)[1]) {
                    enemy.startWalking(-5);
                }
            }
        }
    }

    @Override
    public void postStep(StepEvent stepEvent) {
    }
}