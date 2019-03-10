package game;

import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class LevelFour extends SuperLevel {

    LevelFour() { readFolder("data/Backgrounds/Dungeon/"); }

    LevelFour(ArrayList<String> availableLevels, Game game) {
        super(STATE.GAME, availableLevels, game);
        readFolder("data/Backgrounds/Dungeon/");
        this.generateMap("data/Levels/Level 4.txt");
    }

    @Override
    void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        g.drawImage(iconHashMap.get("Background").getImage(), 0, 0, (int) (width * 1.05f), height, view);

        view.backgroundTiling(g, iconHashMap.get("dungeonFar"), -400 + x * -2 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 8f);
        view.backgroundTiling(g, iconHashMap.get("dungeonMid"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 520 + (rand.nextInt(yBound) - yBound / 2), 4.5f);
        view.backgroundTiling(g, iconHashMap.get("dungeonClose"), -x * 11 + (rand.nextInt(xBound) - xBound / 2f), height - 500 + (rand.nextInt(yBound) - yBound / 2), 3.5f);
    }

    @Override
    void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {}

    @Override
    public Vec2 startPosition() {
        return new Vec2(-8.172528f, 20);
    }
}