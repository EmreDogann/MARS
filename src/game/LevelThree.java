package game;

import city.cs.engine.DynamicBody;
import city.cs.engine.StaticBody;
import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LevelThree extends SuperLevel implements StepListener {

    LevelThree() {
        readFolder("data/Backgrounds/Mine/");
    }

    LevelThree(ArrayList<String> availableLevels, Game game) {
        super(STATE.GAME, availableLevels, game);
        readFolder("data/Backgrounds/Mine/");
        this.generateMap("data/Levels/Level 3.txt");
        float[][] enemyStartingPos = {{-3.4768043f, 3.1f}, {82.22112f, 88.82362f}, {92.05706f, 98.37362f}, {111.566734f, 101.67362f}, {91.82831f, 98.52362f}, {128.41661f, 131.65659f}, {138.05182f, 134.89435f}};
        this.findMovingEnemies(enemyStartingPos);
        this.addStepListener(this);
    }

    @Override
    void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        g.drawImage(iconHashMap.get("Background").getImage(), 0, 0, (int) (width * 1.05f), height, view);

        view.backgroundTiling(g, iconHashMap.get("mineFarTop"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 4f);
        view.backgroundTiling(g, iconHashMap.get("mineFarDown"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 220 + (rand.nextInt(yBound) - yBound / 2), 4f);
        view.backgroundTiling(g, iconHashMap.get("mineMidTop"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 3.5f);
        view.backgroundTiling(g, iconHashMap.get("mineMidDown"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 220 + (rand.nextInt(yBound) - yBound / 2), 3.5f);
        view.backgroundTiling(g, iconHashMap.get("mineClose"), -x * 10 + (rand.nextInt(xBound) - xBound / 2f), height - 310 + (rand.nextInt(yBound) - yBound / 2), 4.5f);
    }

    @Override
    void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {}

    @Override
    public Vec2 startPosition() {
        return new Vec2(-27.7f, 20);
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
