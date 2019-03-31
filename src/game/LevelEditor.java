package game;

import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Acts as a 'middle-man' between SuperLevel which holds all the information that is specific to the level editor.
 */
public class LevelEditor extends SuperLevel {

    /**
     * Constructor for LevelEditor.
     * @param levels An ArrayList of all the levels in data/Levels.
     * @param game Instance of Game.
     * @param availableSaves An ArrayList of all the saves in data/Saves.
     */
    public LevelEditor(ArrayList<String> levels, Game game, ArrayList<String> availableSaves) {
        super(STATE.LEVEL_EDITOR, levels, game, availableSaves);
    }

    /**
     * Will draw the backgrounds currently picked by the player in the level editor.
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
    public void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        //Load the background the user has chosen.
        getTempWorld().generateBackground(g, x, width, height, rand, xBound, yBound);
    }

    /**
     * Will draw the foregrounds related to the background currently picked by the player in the level editor.
     * @param g Instance of Graphics2D.
     * @param x The new x position of the background.
     * @param width The width of the background.
     * @param height The height of the background.
     * @param rand Instance of Random.
     * @param xBound The current screen shake amount in the x direction.
     * @param yBound The current screen shake amount in the y direction.
     */
    @Override
    public void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        //Load the foreground that is associated with the background the user has chosen.
        getTempWorld().generateForeground(g, x, width, height, rand, xBound, yBound);
    }

    /**
     * @return the starting position of the player as type Vec2.
     */
    @Override
    public Vec2 startPosition() {
        return new Vec2(0, -9.5f);
    }
}
