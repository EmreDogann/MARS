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


public class BackgroundPanel extends UserView {

//    private ImageIcon ground = new ImageIcon("data/Level1Background/ground.png");
//    private ImageIcon orangeBG = new ImageIcon("data/Level1Background/orangeMountain.png");
//    private ImageIcon pinkBG = new ImageIcon("data/Level1Background/pinkMountain.png");
//    private ImageIcon greyBG = new ImageIcon("data/Level1Background/greyMountain.png");
//
//    private ImageIcon background = new ImageIcon("data/Level1Background/Background.png");
//    private ImageIcon stars = new ImageIcon("data/Level1Background/stars.png");
//    private ImageIcon farPlanets = new ImageIcon("data/Level1Background/farPlanets.png");
//    private ImageIcon ringPlanet = new ImageIcon("data/Level1Background/ringPlanet.png");
//    private ImageIcon bigPlanet = new ImageIcon("data/Level1Background/bigPlanet.png");
//
//    private ImageIcon greenPlanet = new ImageIcon("data/Level1Background/greenPlanet.gif");
//    private ImageIcon yellowPlanet = new ImageIcon("data/Level1Background/yellowPlanet.gif");
//    private ImageIcon redPlanet = new ImageIcon("data/Level1Background/redPlanet.gif");
//    private ImageIcon orangePlanet = new ImageIcon("data/Level1Background/orangePlanet.gif");
//    private ImageIcon bluePlanet = new ImageIcon("data/Level1Background/bluePlanet.gif");

    private ImageIcon healthFull = new ImageIcon("data/LifeBar/Health Full.png");
    private ImageIcon healthSymbol = new ImageIcon("data/LifeBar/Health Symbol.png");
    private ImageIcon shieldFull = new ImageIcon("data/LifeBar/Shield Full.png");
    private ImageIcon shieldSymbol = new ImageIcon("data/LifeBar/Shield Symbol.png");
    private ImageIcon barEmpty = new ImageIcon("data/LifeBar/Bar Empty.png");
    private ImageIcon ammoSymbol = new ImageIcon("data/LifeBar/Ammo Symbol.png");
    private ImageIcon scoreCurr = new ImageIcon("data/LifeBar/CurrentScore.png");
    private ImageIcon scorePrev = new ImageIcon("data/LifeBar/ScorePreviousBest.png");
    private ImageIcon scoreHigh = new ImageIcon("data/LifeBar/HighScore.png");

    private ImageIcon[] imageIcons;
    private HashMap<ImageIcon, Float[]> imageIconHashMap = new HashMap<>();

    private Random rand = new Random();
    private int xBound = 1, yBound = 1;

    private Timer timer;
    MainCharacter player;
    private STATE State;
    static int WIDTH;
    static int HEIGHT;
    private SuperLevel world;
    private Game game;
    private int shakeLimit;

    private float x;

    BackgroundPanel(SuperLevel w, int width, int height, Game game) {
        super(w, width, height);
        this.world = w;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.game = game;
        this.timer = new Timer(500, increaseShake);
        this.setDoubleBuffered(true);
    }

    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void paintBackground(Graphics2D g) {
        world.generateBackground(g, x, getWidth(), getHeight(), rand, xBound, yBound);
    }

    @Override
    public void paintForeground(Graphics2D g) {
        world.generateForeground(g, x, getWidth(), getHeight(), rand, xBound, yBound);
        if (State == STATE.GAME) {
            g.drawImage(healthSymbol.getImage(), 20, 20, (int) (healthSymbol.getIconWidth() * 2.3f), (int) (healthSymbol.getIconHeight() * 2.3f), this);
            g.drawImage(barEmpty.getImage(), 20 + (int) (healthSymbol.getIconWidth() * 2.3f), 20, (int) (barEmpty.getIconWidth() * 2.3f), (int) (barEmpty.getIconHeight() * 2.3f), this);
            if (player.getHealth() > 0) {
                g.drawImage(healthFull.getImage(), 20 + (int) (healthSymbol.getIconWidth() * 2.3f), 20, (int) (healthFull.getIconWidth() * 2.3f * player.getHealth()) / 100, (int) (healthFull.getIconHeight() * 2.3f), this);
            }

            g.drawImage(shieldSymbol.getImage(), 20, 42, (int) (shieldSymbol.getIconWidth() * 2.3f), (int) (shieldSymbol.getIconHeight() * 2.3f), this);
            g.drawImage(barEmpty.getImage(), 20 + (int) (shieldSymbol.getIconWidth() * 2.3f), 42, (int) (barEmpty.getIconWidth() * 2.3f), (int) (barEmpty.getIconHeight() * 2.3f), this);
            if (player.getArmour() > 0) {
                g.drawImage(shieldFull.getImage(), 20 + (int) (shieldSymbol.getIconWidth() * 2.3f), 42, (int) (shieldFull.getIconWidth() * 2.3f * player.getArmour()) / 25, (int) (shieldFull.getIconHeight() * 2.3f), this);
            }

            g.drawImage(barEmpty.getImage(), 530, 20, (int) (barEmpty.getIconWidth() * 2.5f), (int) (barEmpty.getIconHeight() * 6f), this);
            g.drawImage(scorePrev.getImage(), 530, 20, (int) (scorePrev.getIconWidth() * 2f * game.getScore().getPrevScore()) / 3000, (int) (scorePrev.getIconHeight() * 2.3f) - 3, this);
            g.drawImage(scoreHigh.getImage(), 530, 36, (int) (scoreHigh.getIconWidth() * 2f * game.getScore().getHighScore()) / 3000, (int) (scoreHigh.getIconHeight() * 2.3f) - 3, this);
            g.drawImage(scoreCurr.getImage(), 530, 52, (int) (scoreCurr.getIconWidth() * 2f * game.getScore().getCurrScore()) / 3000, (int) (scoreCurr.getIconHeight() * 2.3f) - 3, this);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.BOLD, 18));
            g.drawString("" + player.getHealth(), 42, 35);
            g.drawString("" + player.getArmour(), 42, 57);

            g.drawString("Prev:", 470, 33);
            g.drawString("High:", 470, 49);
            g.drawString("Curr:", 470, 65);

            g.drawString("" + game.getScore().getPrevScore(), 535 + (scorePrev.getIconWidth() * 2f * game.getScore().getPrevScore()) / 3000, 33);
            g.drawString("" + game.getScore().getHighScore(), 535 + (scorePrev.getIconWidth() * 2f * game.getScore().getHighScore()) / 3000, 49);
            g.drawString("" + game.getScore().getCurrScore(), 535 + (scorePrev.getIconWidth() * 2f * game.getScore().getCurrScore()) / 3000, 65);

            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);
            nf.setRoundingMode(RoundingMode.HALF_UP);
            g.drawString("Time: " + nf.format(game.getScore().getCurrTime()), 530, 95);

            if (player.getArmImage().equals("data/ArmPistol.png")) {
                g.drawImage(ammoSymbol.getImage(), 24, 65, ammoSymbol.getIconWidth() / 8, ammoSymbol.getIconHeight() / 8, this);
                g.drawString("Ammo:" + player.getAmmo(), 43, 85);
            }
        }
    }

    void backgroundTiling(Graphics2D g, ImageIcon icon, float x, int y, float scale) {
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

    void drawImage(Graphics2D g, ImageIcon icon, int x, int y, float scale) {
        g.drawImage(icon.getImage(), x + (rand.nextInt(xBound) - xBound / 2), y + (rand.nextInt(yBound) - yBound / 2), (int) (icon.getIconWidth() * scale), (int) (icon.getIconHeight() * scale), this);
    }

    void startShaking(int shakeLimit) {
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

    public void setImageIcons(ImageIcon[] imageIcons) {
        this.imageIcons = imageIcons;
    }

    public void setImageIconHashMap(HashMap<ImageIcon, Float[]> imageIconHashMap) {
        this.imageIconHashMap = imageIconHashMap;
    }

    public ImageIcon getHealthFull() {
        return healthFull;
    }
    public ImageIcon getBarEmpty() {
        return barEmpty;
    }

    @Override
    public SuperLevel getWorld() {
        return world;
    }

    public void setSuperWorld(SuperLevel world) {
        this.world = world;
        setWorld(world);
    }
}
