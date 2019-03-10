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

public class LevelFive extends SuperLevel implements StepListener {

    LevelFive() { readFolder("data/Backgrounds/Crystal/"); }

    LevelFive(ArrayList<String> availableLevels, Game game) {
        super(STATE.GAME, availableLevels, game);
        readFolder("data/Backgrounds/Crystal/");
        this.generateMap("data/Levels/Level 5.txt");
        float[][] enemyStartingPos = {{-24.676655f, 85.5732f}, {-21.426655f, 85.5732f}, {-18.126656f, 85.5732f}, {-14.950026f, 85.5732f}, {-11.56385f, 85.5732f}, {-8.320067f, 85.5732f}, {-5.1464286f, 85.5732f},
                {-1.9980118f, 85.5732f}, {75.83998f, -1.9390122f}, {72.72107f, -1.9390122f}, {69.34975f, -1.9390122f}, {66.19206f, -1.9390122f}, {62.88207f, -1.9390122f}, {79.0232f, -1.9390122f}, {82.3732f, -1.9390122f},
                {85.4732f, -1.9390122f}};
        findMovingEnemies(enemyStartingPos);
        this.addStepListener(this);
    }

    @Override
    void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        g.drawImage(iconHashMap.get("Background").getImage(), 0, 0, (int) (width * 1.05f), height, view);

        view.backgroundTiling(g, iconHashMap.get("crystalFar"), -400 + x * -3 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 7.85f);
        view.backgroundTiling(g, iconHashMap.get("crystalMid"), 350 - x * 5 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 7.85f);
        view.backgroundTiling(g, iconHashMap.get("crystalClose"), -350 + -x * 11 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 7.85f);
    }

    @Override
    void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        for (Enemy enemy : enemies) {
            ImageIcon healthFull = view.getHealthFull();
            ImageIcon barEmpty = view.getBarEmpty();
            Point2D.Float enemyCoordinates = view.worldToView(enemy.getPosition());
            g.drawImage(barEmpty.getImage(), (int)(enemyCoordinates.x-25), (int)(enemyCoordinates.y-25), (int) (barEmpty.getIconWidth() * 0.5f), (int) (barEmpty.getIconHeight() * 0.5f), view);
            g.drawImage(healthFull.getImage(), (int)(enemyCoordinates.x-25), (int)(enemyCoordinates.y-25), (int) (healthFull.getIconWidth() * 0.5f * enemy.getHealth()) / 100, (int) (healthFull.getIconHeight() * 0.5f), view);
        }
    }

    @Override
    public Vec2 startPosition() {
        return new Vec2(5, 20);
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