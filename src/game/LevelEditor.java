package game;

import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class LevelEditor extends SuperLevel {

    LevelEditor(ArrayList<String> levels, Game game) {
        super(STATE.LEVEL_EDITOR, levels, game);
    }

    @Override
    @SuppressWarnings("Duplicates")
    void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        getTempWorld().generateBackground(g, x, width, height, rand, xBound, yBound);
    }

    @Override
    void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        getTempWorld().generateForeground(g, x, width, height, rand, xBound, yBound);
    }

    @Override
    public Vec2 startPosition() {
        return new Vec2(0, -9.5f);
    }
}
