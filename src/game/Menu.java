package game;

import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Menu extends SuperLevel {

    Menu(ArrayList<String> levels, Game game) {
        super(STATE.MENU, levels, game);
        readFolder("data/Backgrounds/Mars Surface/");
    }

    @Override
    @SuppressWarnings("Duplicates")
    void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        g.drawImage(iconHashMap.get("Background").getImage(), 0, 0, (int) (width * 1.05f), height, view);
        view.drawImage(g, iconHashMap.get("greenPlanet"), (int) (575 + x * -0.2f), 150, 0.25f);
        view.drawImage(g, iconHashMap.get("yellowPlanet"), (int) (400 + x * -0.1f), 200, 1);
        view.drawImage(g, iconHashMap.get("redPlanet"), (int) (500 + x * -0.3f), 250, 0.85f);
        view.drawImage(g, iconHashMap.get("orangePlanet"), (int) (300 + x * -0.3f), 300, 0.5f);
        view.drawImage(g, iconHashMap.get("bluePlanet"), (int) (550 + x * -0.35f), 265, 0.35f);

        view.drawImage(g, iconHashMap.get("stars"), (int) (x * -0.05f), 0, 3f);
        view.drawImage(g, iconHashMap.get("farPlanets"), (int) (x * -0.25f), 0, 3f);
        view.drawImage(g, iconHashMap.get("ringPlanet"), (int) (x * -0.5f), 0, 3f);
        view.drawImage(g, iconHashMap.get("bigPlanet"), (int) (x * -0.75f), 0, 2f);

        view.backgroundTiling(g, iconHashMap.get("greyMountain"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 245 + (rand.nextInt(yBound) - yBound / 2), 2.5f);
        view.backgroundTiling(g, iconHashMap.get("pinkMountain"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 190 + (rand.nextInt(yBound) - yBound / 2), 2.25f);
        view.backgroundTiling(g, iconHashMap.get("orangeMountain"), -x * 11 + (rand.nextInt(xBound) - xBound / 2f), height - 160 + (rand.nextInt(yBound) - yBound / 2), 2);
    }

    @Override
    void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        getView().backgroundTiling(g, iconHashMap.get("ground"), -x * 20 + (rand.nextInt(xBound) - xBound / 2f), height - 45 + (rand.nextInt(yBound) - yBound / 2), 1.5f);
    }

    @Override
    public Vec2 startPosition() {
        return new Vec2(0, 0);
    }
}
