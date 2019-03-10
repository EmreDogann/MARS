package game;

import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class LevelTwo extends SuperLevel {

    LevelTwo() { readFolder("data/Backgrounds/Industrial/"); }

    LevelTwo(ArrayList<String> availableLevels, Game game) {
        super(STATE.GAME, availableLevels, game);
        readFolder("data/Backgrounds/Industrial/");
        this.generateMap("data/Levels/Level 2.txt");
    }

    @Override
    void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        g.drawImage(iconHashMap.get("Background").getImage(), 0, 0, (int) (width * 1.05f), height, view);

        view.backgroundTiling(g, iconHashMap.get("industrialFar"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 450 + (rand.nextInt(yBound) - yBound / 2), 3f);
        view.backgroundTiling(g, iconHashMap.get("industrialMid"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 380 + (rand.nextInt(yBound) - yBound / 2), 2.5f);
        view.backgroundTiling(g, iconHashMap.get("industrialClose"), -400 + x * -10 + (rand.nextInt(xBound) - xBound / 2f), height - 360 + (rand.nextInt(yBound) - yBound / 2), 3.5f);
    }

    @Override
    void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {}

    @Override
    public Vec2 startPosition() {
        return new Vec2(3, 20);
    }
}
