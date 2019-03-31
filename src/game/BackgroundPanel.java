package game;

import city.cs.engine.UserView;
import city.cs.engine.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Random;

/**
 * Handles tasks such as shaking the background during scripted sequences, drawing background parallax images and HUD elements such as health, score, etc.
 * @author Emre, Dogan, emre.dogan@city.ac.uk
 */
public class BackgroundPanel extends UserView {

    private ImageIcon healthFull = new ImageIcon("data/LifeBar/Health Full.png");
    private ImageIcon healthSymbol = new ImageIcon("data/LifeBar/Health Symbol.png");
    private ImageIcon shieldFull = new ImageIcon("data/LifeBar/Shield Full.png");
    private ImageIcon shieldSymbol = new ImageIcon("data/LifeBar/Shield Symbol.png");
    private ImageIcon barEmpty = new ImageIcon("data/LifeBar/Bar Empty.png");
    private ImageIcon ammoSymbol = new ImageIcon("data/LifeBar/Ammo Symbol.png");
    private ImageIcon scoreCurr = new ImageIcon("data/LifeBar/CurrentScore.png");
    private ImageIcon scorePrev = new ImageIcon("data/LifeBar/ScorePreviousBest.png");
    private ImageIcon scoreHigh = new ImageIcon("data/LifeBar/HighScore.png");

    private Random rand = new Random();
    private int xBound = 1, yBound = 1;

    private Timer timer;
    public MainCharacter player;
    static int WIDTH;
    static int HEIGHT;
    private SuperLevel world;
    private Game game;
    private int shakeLimit;

    private float x;

    /**
     * Constructor for BackgroundPanel.
     * @param world A copy of the current instance of the world.
     * @param width The width of the window in pixels.
     * @param height The height of window in pixels.
     * @param game Instance of the game class.
     */
    public BackgroundPanel(SuperLevel world, int width, int height, Game game) {
        super(world, width, height);
        this.world = world;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.game = game;
        this.timer = new Timer(500, increaseShake);
        this.setDoubleBuffered(true);
    }

    /**
     * Sets the x position to be used when drawing background and foreground elements.
     * @param x The current x-position of the player.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Will call the generateBackground method of the current level which will draw the background elements specific to that level.
     * @param g Instance of Graphics2D.
     */
    @Override
    public void paintBackground(Graphics2D g) {
        world.generateBackground(g, x, getWidth(), getHeight(), rand, xBound, yBound);
    }

    /**
     * Will call the generateForeground method of the current level which will draw the foreground elements specific to that level.
     * In addition to that, it will draw the all the HUD elements if the current state of the world from the enum STATE is GAME.
     * @param g Instance of Graphics2D.
     */
    @Override
    public void paintForeground(Graphics2D g) {
        world.generateForeground(g, x, getWidth(), getHeight(), rand, xBound, yBound);
        if (world.getState() == STATE.GAME) {
            //Health bar
            g.drawImage(healthSymbol.getImage(), 20, 20, (int) (healthSymbol.getIconWidth() * 2.3f), (int) (healthSymbol.getIconHeight() * 2.3f), this);
            g.drawImage(barEmpty.getImage(), 20 + (int) (healthSymbol.getIconWidth() * 2.3f), 20, (int) (barEmpty.getIconWidth() * 2.3f), (int) (barEmpty.getIconHeight() * 2.3f), this);
            if (player.getHealth() > 0) {
                g.drawImage(healthFull.getImage(), 20 + (int) (healthSymbol.getIconWidth() * 2.3f), 20, (int) (healthFull.getIconWidth() * 2.3f * player.getHealth()) / 100, (int) (healthFull.getIconHeight() * 2.3f), this);
            }

            //Armour Bar
            g.drawImage(shieldSymbol.getImage(), 20, 42, (int) (shieldSymbol.getIconWidth() * 2.3f), (int) (shieldSymbol.getIconHeight() * 2.3f), this);
            g.drawImage(barEmpty.getImage(), 20 + (int) (shieldSymbol.getIconWidth() * 2.3f), 42, (int) (barEmpty.getIconWidth() * 2.3f), (int) (barEmpty.getIconHeight() * 2.3f), this);
            if (player.getArmour() > 0) {
                g.drawImage(shieldFull.getImage(), 20 + (int) (shieldSymbol.getIconWidth() * 2.3f), 42, (int) (shieldFull.getIconWidth() * 2.3f * player.getArmour()) / 25, (int) (shieldFull.getIconHeight() * 2.3f), this);
            }

            //Score
            g.drawImage(barEmpty.getImage(), 619, 20, (int) (barEmpty.getIconWidth() * 2.5f), (int) (barEmpty.getIconHeight() * 6f), this);
            g.drawImage(scorePrev.getImage(), 619, 20, (int) (scorePrev.getIconWidth() * 2f * game.getScore().getPrevScore()) / 3000, (int) (scorePrev.getIconHeight() * 2.3f) - 3, this);
            g.drawImage(scoreHigh.getImage(), 619, 36, (int) (scoreHigh.getIconWidth() * 2f * game.getScore().getHighScore()) / 3000, (int) (scoreHigh.getIconHeight() * 2.3f) - 3, this);
            g.drawImage(scoreCurr.getImage(), 619, 52, (int) (scoreCurr.getIconWidth() * 2f * game.getScore().getCurrScore()) / 3000, (int) (scoreCurr.getIconHeight() * 2.3f) - 3, this);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.BOLD, 18));
            g.drawString("" + player.getHealth(), 42, 35);
            g.drawString("" + player.getArmour(), 42, 57);

            g.drawString("Prev:", 559, 33);
            g.drawString("High:", 559, 49);
            g.drawString("Curr:", 559, 65);

            g.drawString("" + game.getScore().getPrevScore(), 624 + (scorePrev.getIconWidth() * 2f * game.getScore().getPrevScore()) / 3000, 33);
            g.drawString("" + game.getScore().getHighScore(), 624 + (scorePrev.getIconWidth() * 2f * game.getScore().getHighScore()) / 3000, 49);
            g.drawString("" + game.getScore().getCurrScore(), 624 + (scorePrev.getIconWidth() * 2f * game.getScore().getCurrScore()) / 3000, 65);

            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);
            nf.setRoundingMode(RoundingMode.HALF_UP);
            g.drawString("Time: " + nf.format(game.getScore().getCurrTime()), 619, 95);

            //Ammo
            if (player.isAcquiredPistol()) {
                g.drawImage(ammoSymbol.getImage(), 24, 65, ammoSymbol.getIconWidth() / 8, ammoSymbol.getIconHeight() / 8, this);
                g.drawString("Ammo:" + player.getAmmo(), 43, 85);
            }
        }
    }

    /**
     * Draws background and foreground elements and is used to ensure that those elements repeat/tile continuously.
     * @param g Instance of Graphics2D.
     * @param icon The background icon to tile.
     * @param x The current x-position of the background/foreground element.
     * @param y The current y-position of the background/foreground element.
     * @param scale Number to scale the image size by the specified amount.
     */
    public void backgroundTiling(Graphics2D g, ImageIcon icon, float x, int y, float scale) {
        int iw = (int) (icon.getIconWidth() * scale);
        int ih = (int) (icon.getIconHeight() * scale);

        for (float i = 0; i < getWidth() - x; i += iw) {
            g.drawImage(icon.getImage(), (int) (x + i), y, iw, ih, this);
        }

        for (float i = 0; i < x; ) {
            i += iw;
            g.drawImage(icon.getImage(), (int) (x - i), y, iw, ih, this);
        }
    }

    /**
     * Used to draw background/foreground elements that do not require tiling.
     * @param g Instance of Graphics2D.
     * @param icon The background icon to tile.
     * @param x The current x-position of the background/foreground element.
     * @param y The current y-position of the background/foreground element.
     * @param scale Number to scale the image size by the specified amount.
     */
    public void drawImage(Graphics2D g, ImageIcon icon, int x, int y, float scale) {
        g.drawImage(icon.getImage(), x + (rand.nextInt(xBound) - xBound / 2), y + (rand.nextInt(yBound) - yBound / 2), (int) (icon.getIconWidth() * scale), (int) (icon.getIconHeight() * scale), this);
    }

    /**
     * Initiates the screen shake effect.
     * @param shakeLimit Number which defines the maximum amount that the screen is allowed to shake.
     */
    public void startShaking(int shakeLimit) {
        world.addTimer(timer);
        timer.start();
        this.shakeLimit = shakeLimit;
        xBound = 2;
        yBound = 1;
    }

    private ActionListener increaseShake = e -> {
        if (shakeLimit == 0) {
            xBound++;
            yBound++;
        } else if (xBound < shakeLimit && yBound < shakeLimit) {
            xBound++;
            yBound++;
        }
    };

    /**
     * Stops the screen shake effect.
     */
    public void stopShaking() {
        timer.stop();
        xBound = 1;
        yBound = 1;
    }

    /**
     * @return the current amount that the screen is shaking in the x direction as an int.
     */
    public int getxBound() { return xBound; }

    /**
     * @return the ImageIcon HUD element corresponding to a full health bar.
     */
    public ImageIcon getHealthFull() {
        return healthFull;
    }

    /**
     * @return the ImageIcon HUD element corresponding to an empty bar.
     */
    public ImageIcon getBarEmpty() {
        return barEmpty;
    }

    /**
     * @return the current world as type SuperLevel.
     */
    @Override
    public SuperLevel getWorld() {
        return world;
    }

    /**
     * Updates the world in UserView and BackgroundPanel with that of the current world.
     * @param world Current world.
     */
    public void setSuperWorld(SuperLevel world) {
        this.world = world;
        setWorld(world);
    }
}
