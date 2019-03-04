package game;

import city.cs.engine.UserView;
import city.cs.engine.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;


public class BackgroundPanel extends UserView {

    private ImageIcon ground = new ImageIcon("data/Level1Background/Ground.png");
    private ImageIcon orangeBG = new ImageIcon("data/Level1Background/OrangeMountains.png");
    private ImageIcon pinkBG = new ImageIcon("data/Level1Background/PinkMountains.png");
    private ImageIcon greyBG = new ImageIcon("data/Level1Background/GreyMountains.png");
    private ImageIcon background = new ImageIcon("data/Level1Background/Background.png");
    private ImageIcon stars = new ImageIcon("data/Level1Background/stars.png");
    private ImageIcon farPlanets = new ImageIcon("data/Level1Background/far-planets.png");
    private ImageIcon ringPlanet = new ImageIcon("data/Level1Background/ring-planet.png");
    private ImageIcon bigPlanet = new ImageIcon("data/Level1Background/big-planet.png");
    private ImageIcon greenPlanet = new ImageIcon("data/Level1Background/greenPlanet.gif");
    private ImageIcon yellowPlanet = new ImageIcon("data/Level1Background/yellowPlanet.gif");
    private ImageIcon redPlanet = new ImageIcon("data/Level1Background/redPlanet.gif");
    private ImageIcon orangePlanet = new ImageIcon("data/Level1Background/orangePlanet.gif");
    private ImageIcon bluePlanet = new ImageIcon("data/Level1Background/bluePlanet.gif");
    private ImageIcon healthFull = new ImageIcon("data/LifeBar/Health Full.png");
    private ImageIcon healthSymbol = new ImageIcon("data/LifeBar/Health Symbol.png");
    private ImageIcon shieldFull = new ImageIcon("data/LifeBar/Shield Full.png");
    private ImageIcon shieldSymbol = new ImageIcon("data/LifeBar/Shield Symbol.png");
    private ImageIcon BarEmpty = new ImageIcon("data/LifeBar/Bar Empty.png");
    private ImageIcon ammoSymbol = new ImageIcon("data/LifeBar/Ammo Symbol.png");

    private Random rand = new Random();
    private int xBound = 1, yBound = 1;

    private Timer timer;
    MainCharacter player;
    private STATE State;
    static int WIDTH;
    static int HEIGHT;

    private float x;

    BackgroundPanel(World w, int width, int height) {
        super(w, width, height);
        WIDTH = width;
        HEIGHT = height;
        this.setDoubleBuffered(true);
    }

    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void paintBackground(Graphics2D g) {
        g.drawImage(background.getImage(), 0, 0, (int) (getWidth() * 1.05f), getHeight(), this);
        drawImage(g, greenPlanet, (int) (575 + x * -0.2f), 150, 0.25f);
        drawImage(g, yellowPlanet, (int) (400 + x * -0.1f), 200, 1);
        drawImage(g, redPlanet, (int) (500 + x * -0.3f), 250, 0.85f);
        drawImage(g, orangePlanet, (int) (300 + x * -0.3f), 300, 0.5f);
        drawImage(g, bluePlanet, (int) (550 + x * -0.35f), 265, 0.35f);

        drawImage(g, stars, (int) (x * -0.05f), 0, 3f);
        drawImage(g, farPlanets, (int) (x * -0.25f), 0, 3f);
        drawImage(g, ringPlanet, (int) (x * -0.5f), 0, 3f);
        drawImage(g, bigPlanet, (int) (x * -0.75f), 0, 2f);

//        g.drawImage(greenPlanet.getImage(), (int) (575 + x * -0.2f) + (rand.nextInt(xBound) - xBound / 2), 150 + (rand.nextInt(yBound) - yBound / 2), (int) (greenPlanet.getIconWidth() * 0.25f), (int) (greenPlanet.getIconHeight() * 0.25f), this);
//        g.drawImage(yellowPlanet.getImage(), (int) (400 + x * -0.1f) + (rand.nextInt(xBound) - xBound / 2), 200 + (rand.nextInt(yBound) - yBound / 2), yellowPlanet.getIconWidth(), yellowPlanet.getIconHeight(), this);
//        g.drawImage(redPlanet.getImage(), (int) (500 + x * -0.3f) + (rand.nextInt(xBound) - xBound / 2), 250 + (rand.nextInt(yBound) - yBound / 2), (int) (redPlanet.getIconWidth() * 0.85f), (int) (redPlanet.getIconHeight() * 0.85f), this);
//        g.drawImage(orangePlanet.getImage(), (int) (300 + x * -0.3f) + (rand.nextInt(xBound) - xBound / 2), 300 + (rand.nextInt(yBound) - yBound / 2), (int) (orangePlanet.getIconWidth() * 0.5f), (int) (orangePlanet.getIconHeight() * 0.5f), this);
//        g.drawImage(bluePlanet.getImage(), (int) (550 + x * -0.35f) + (rand.nextInt(xBound) - xBound / 2), 265 + (rand.nextInt(yBound) - yBound / 2), (int) (bluePlanet.getIconWidth() * 0.35f), (int) (bluePlanet.getIconHeight() * 0.35f), this);
//
//        g.drawImage(stars.getImage(), (int) (x * -0.05f) + (rand.nextInt(xBound) - xBound / 2), (rand.nextInt(yBound) - yBound / 2), stars.getIconWidth() * 3, stars.getIconHeight() * 3, this);
//        g.drawImage(farPlanets.getImage(), (int) (x * -0.25f) + (rand.nextInt(xBound) - xBound / 2), (rand.nextInt(yBound) - yBound / 2), farPlanets.getIconWidth() * 3, farPlanets.getIconHeight() * 3, this);
//        g.drawImage(ringPlanet.getImage(), (int) (x * -0.5f) + (rand.nextInt(xBound) - xBound / 2), (rand.nextInt(yBound) - yBound / 2), ringPlanet.getIconWidth() * 3, ringPlanet.getIconHeight() * 3, this);
//        g.drawImage(bigPlanet.getImage(), (int) (x * -0.75f) + (rand.nextInt(xBound) - xBound / 2), (rand.nextInt(yBound) - yBound / 2), bigPlanet.getIconWidth() * 2, bigPlanet.getIconHeight() * 2, this);

        backgroundTiling(g, greyBG, -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), this.getHeight() - 245 + (rand.nextInt(yBound) - yBound / 2), 2.5f);
        backgroundTiling(g, pinkBG, 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), this.getHeight() - 190 + (rand.nextInt(yBound) - yBound / 2), 2.25f);
        backgroundTiling(g, orangeBG, -x * 11 + (rand.nextInt(xBound) - xBound / 2f), this.getHeight() - 160 + (rand.nextInt(yBound) - yBound / 2), 2);
    }

    @Override
    public void paintForeground(Graphics2D g) {
        backgroundTiling(g, ground, -x * 20 + (rand.nextInt(xBound) - xBound / 2f), this.getHeight() - 45 + (rand.nextInt(yBound) - yBound / 2), 1.5f);
        if (State == STATE.GAME) {
            g.drawImage(healthSymbol.getImage(), 20, 20, (int) (healthSymbol.getIconWidth() * 2.3f), (int) (healthSymbol.getIconHeight() * 2.3f), this);
            g.drawImage(BarEmpty.getImage(), 20 + (int) (healthSymbol.getIconWidth() * 2.3f), 20, (int) (BarEmpty.getIconWidth() * 2.3f), (int) (BarEmpty.getIconHeight() * 2.3f), this);
            if (player.getHealth() > 0) {
                g.drawImage(healthFull.getImage(), 20 + (int) (healthSymbol.getIconWidth() * 2.3f), 20, (int) (healthFull.getIconWidth() * 2.3f * player.getHealth()) / 100, (int) (healthFull.getIconHeight() * 2.3f), this);
            }

            g.drawImage(shieldSymbol.getImage(), 20, 42, (int) (shieldSymbol.getIconWidth() * 2.3f), (int) (shieldSymbol.getIconHeight() * 2.3f), this);
            g.drawImage(BarEmpty.getImage(), 20 + (int) (shieldSymbol.getIconWidth() * 2.3f), 42, (int) (BarEmpty.getIconWidth() * 2.3f), (int) (BarEmpty.getIconHeight() * 2.3f), this);
            if (player.getArmour() > 0) {
                g.drawImage(shieldFull.getImage(), 20 + (int) (shieldSymbol.getIconWidth() * 2.3f), 42, (int) (shieldFull.getIconWidth() * 2.3f * player.getArmour()) / 25, (int) (shieldFull.getIconHeight() * 2.3f), this);
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.BOLD, 18));
            g.drawString("" + player.getHealth(), 42, 35);
            g.drawString("" + player.getArmour(), 42, 57);

            g.drawImage(ammoSymbol.getImage(), 24, 65, ammoSymbol.getIconWidth() / 8, ammoSymbol.getIconHeight() / 8, this);
            g.drawString("Ammo:" + player.getAmmo(), 43, 85);
        }
    }

    private void backgroundTiling(Graphics2D g, ImageIcon icon, float x, int y, float scale) {
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

    private void drawImage(Graphics2D g, ImageIcon icon, int x, int y, float scale) {
        g.drawImage(icon.getImage(), x + (rand.nextInt(xBound) - xBound / 2), y + (rand.nextInt(yBound) - yBound / 2), (int) (icon.getIconWidth() * scale), (int) (icon.getIconHeight() * scale), this);
    }

    void startShaking() {
        timer = new Timer(500, increaseShake);
        Game.getWorld().addTimer(timer);
        timer.start();
        xBound = 2;
        yBound = 1;
    }

    private ActionListener increaseShake = e -> {
        xBound++;
        yBound++;
    };

    void stopShaking() {
        timer.stop();
        xBound = 1;
        yBound = 1;
    }

    public void setState(STATE state) {
        State = state;
    }

    public STATE getState() {
        return State;
    }

    int getxBound() { return xBound; }
}
