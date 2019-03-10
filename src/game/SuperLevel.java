package game;

import city.cs.engine.*;
import city.cs.engine.Shape;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import static game.Game.frame;
import static game.MouseHandler.lines;

public abstract class SuperLevel extends World {
    private MainCharacter player;
    private ArrayList<Asteroid> asteroid = new ArrayList<>();
    private BackgroundPanel view;
    private MouseHandler mouseHandler;
    private StepHandler stepHandler;
    private ArrayList<Timer> timerList = new ArrayList<>();
    private ArrayList<String> availableLevels;
    protected HashMap<String, ImageIcon> iconHashMap = new HashMap<>();
    private int[] playerStats;

    protected HashMap<Enemy, float[]> enemyPath = new HashMap<>();
    protected ArrayList<Enemy> movingEnemies = new ArrayList<>();
    protected ArrayList<Enemy> enemies = new ArrayList<>();


    private STATE State;

    private Game game;
    private SuperLevel tempWorld;

    SuperLevel() {
    }

    SuperLevel(STATE State, ArrayList<String> levels, Game game) {
        super(60);
        this.State = State;
        this.availableLevels = levels;
        this.game = game;
    }

    protected void readFolder(String backgroundPath) {
        iconHashMap.clear();
        File folder = new File(backgroundPath);
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            iconHashMap.put(fileEntry.getName().split("\\.")[0], new ImageIcon(fileEntry.toString()));
        }
    }

    void populate(int extraJumpsLimit) {
        if (State == STATE.GAME || State == STATE.LEVEL_EDITOR) {
            Shape astronautShape = new BoxShape(0.3f, 1.1f);
            player = new MainCharacter(this, astronautShape, playerStats[0], playerStats[1], playerStats[2], extraJumpsLimit, game.getLevelNum());
            player.setPosition(startPosition());

            if (game.getLevelNum() == 1) {
                createAsteroids();
                getView().setCentre(new Vec2(-30, 0));
                getView().startShaking(0);
            }

            if (game.getLevelNum() == 1 || State == STATE.LEVEL_EDITOR) {
                Shape shape = new BoxShape(1000, 0.5f);
                Platform ground = new Platform(this, shape, "Ground");
                /*Makes ground invisible.*/
                ground.setLineColor(new Color(0, 0, 0, 0));
                ground.setFillColor(new Color(0, 0, 0, 0));
                ground.setPosition(new Vec2(0, -11.5f));
                ground.setName("Ground");
            }

            mouseHandler = new MouseHandler(this, view, player, game);

            stepHandler = new StepHandler(view, player, game);
            if (State == STATE.LEVEL_EDITOR || game.getLevelNum() > 1 || game.getLevelNum() == -1) {
                this.addStepListener(stepHandler);
                player.addCollisionListener(new CollisionHandler(game));
                view.addMouseListener(mouseHandler);
            }
            if (game.getLevelNum() > 4 || game.getLevelNum() == -1) {
                getView().addMouseMotionListener(mouseHandler);
            }

            frame.addKeyListener(new KeyHandler(player, this.lookAtCursor, stepHandler, 6, this, game));
        } else if (State == STATE.MENU) {
            //getView().setLayout(new OverlayLayout(getView()));
            getView().setLayout(null);
            MainMenu menu = new MainMenu(game);
            getView().add(menu.getMainPanel(), BorderLayout.NORTH);
        }

        //JFrame debugView = new DebugViewer(this, 2000, 2000);
    }

    abstract void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound);

    abstract void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound);

    public ArrayList<Enemy> getEnemies() { return enemies; }

    protected void findMovingEnemies(float[][] enemyStartingPos) {
        java.util.List<DynamicBody> dynamicBodies = this.getDynamicBodies();
        for (DynamicBody body : dynamicBodies) {
            if (body instanceof Enemy) {
                enemies.add((Enemy) body);
            }
        }

        for (Enemy enemy : enemies) {
            for (float[] pos : enemyStartingPos) {
                if (enemy.getPosition().x == pos[0]) {
                    enemyPath.put(enemy, pos);
                    movingEnemies.add(enemy);
                    break;
                }
            }
        }
    }

    /*Will be called to rotate arm to look at the mouse cursor.*/
    ActionListener lookAtCursor = e -> {
        try {
            mouseHandler.calculateTheta(view.getMousePosition());
        } catch (NullPointerException ignored) {
        }
    };

    void createAsteroids() {
        for (int i = 0; i < 4; i++) {
            asteroid.add(new Asteroid(this, new CircleShape(0.38f), 20, player, game));
            asteroid.get(i).spawn();
        }
    }

    void destroyAsteroids() {
        for (int i = 0; i < 4; i++) {
            asteroid.get(i).destroy();
        }
        asteroid.removeAll(asteroid);
    }

    void removeMouseHandler() {
        this.getView().removeMouseListener(mouseHandler);
        this.getView().removeMouseMotionListener(mouseHandler);
    }

    ArrayList<String> getAvailableLevels() {
        return availableLevels;
    }

    void addAvailableLevels(String availableLevels) {
        this.availableLevels.add(availableLevels);
    }

    MainCharacter getPlayer() {
        return player;
    }

    public abstract Vec2 startPosition();

    STATE getState() {
        return State;
    }

    public void setState(STATE state) {
        State = state;
    }

    BackgroundPanel getView() {
        return view;
    }

    void setView(BackgroundPanel newView) {
        this.view = newView;
    }

    public SuperLevel getWorld() {
        return this;
    }

    MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    StepHandler getStepHandler() {
        return stepHandler;
    }

    void addTimer(Timer timer) {
        timerList.add(timer);
    }

    void stopTimers() {
        for (Timer timer : timerList) {
            timer.stop();
        }
    }

    void generateMap(String levelPath) {
        String line;
        BufferedReader reader = null;
        String[] text;
        String background = "";

        try {
            reader = new BufferedReader(new FileReader(levelPath));
            while ((line = reader.readLine()) != null) {
                if (!line.substring(0, 1).equals("#")) {
                    text = line.split(",");
                    lines.add(line);

                    //System.out.println("Type: " + text[0] + ", x: " + text[1] + ", y: " + text[2] + ", width: " + text[3] + ", height: " + text[4]);
                    if (text[0].contains("crumblingPlatform")) {
                        createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/" + text[0] + ".png", 3, "crumblingPlatform");
                    } else if (text[0].equals("exit")) {
                        createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/" + text[0] + ".png", 3, "exit");
                    } else if (text[0].contains("pickup")) {
                        createItemPickup(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/ItemPickup/" + text[0].substring(6) + ".png", text[0].substring(6));
                    } else if (text[0].equals("enemy")) {
                        createEnemy(Float.parseFloat(text[1]), Float.parseFloat(text[2]), "data/" + text[0] + ".png", 1.65f);
                    } else {
                        if (background.equals("Mars Surface")) {
                            createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/Platform/" + text[0] + ".png", 15, "platform");
                        } else {
                            createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/Platform2/" + text[0] + ".png", 15, "platform");
                        }
                    }
                } else {
                    background = line.split("#")[1].split("/")[2].split("\\.")[0];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private void createPlatform(float x, float y, float width, float height, String imagePath, float scale, String type) {
        Shape shape1 = new BoxShape(width, height);
        Platform platform = new Platform(this, shape1, type);
        platform.addImage(new BodyImage(imagePath, scale)).setOffset(new Vec2(0, 0.12f));
        platform.setPosition(new Vec2(x, y));
        platform.setName("Body" + lines.size());
        platform.setLineColor(Color.BLUE);
        if (LevelEditorUI.isOutlineEnabled) {
            platform.setAlwaysOutline(true);
        }
    }

    @SuppressWarnings("Duplicates")
    private void createEnemy(float x, float y, String imagePath, float scale) {
        if (State == STATE.GAME) {
            PolygonShape shape1 = new PolygonShape(-1.186f, -0.816f, -1.186f, -0.1f, -0.251f, 0.825f, 0.251f, 0.825f, 1.181f, -0.105f, 1.186f, -0.821f, -1.186f, -0.821f);
            Enemy enemy = new Enemy(this, shape1, 35, game);
            enemy.addImage(new BodyImage(imagePath, scale));
            enemy.setPosition(new Vec2(x, y));
            enemy.setName("Body" + lines.size());
        } else if (State == STATE.LEVEL_EDITOR) {
            PolygonShape shape1 = new PolygonShape(-1.186f, -0.816f, -1.186f, -0.1f, -0.251f, 0.825f, 0.251f, 0.825f, 1.181f, -0.105f, 1.186f, -0.821f, -1.186f, -0.821f);
            StaticBody enemy = new StaticBody(this, shape1);
            enemy.addImage(new BodyImage(imagePath, scale));
            enemy.setPosition(new Vec2(x, y));
            enemy.setName("Body" + lines.size());
            enemy.setLineColor(Color.BLUE);
            if (LevelEditorUI.isOutlineEnabled) {
                enemy.setAlwaysOutline(true);
            }
        }
    }

    private void createItemPickup(float x, float y, float width, float height, String imagePath, String type) {
        Shape shape1 = new BoxShape(width, height);
        ItemPickup item = new ItemPickup(this, shape1, type, game);
        if (type.equals("DoubleJump") || type.equals("DashBoots")) {
            item.addImage(new BodyImage(imagePath, 2.5f));
        } else if (type.equals("Pistol")) {
            item.addImage(new BodyImage(imagePath, 1f));
        } else {
            item.addImage(new BodyImage(imagePath, 1.3f));
        }
        item.setPosition(new Vec2(x, y));
        item.setName("Body" + lines.size());
    }

    public void setPlayerStats(int[] playerStats) {
        this.playerStats = playerStats;
    }

    public int[] getPlayerStats() {
        return this.playerStats;
    }

    public SuperLevel getTempWorld() {
        return tempWorld;
    }

    public void setTempWorld(SuperLevel tempWorld) {
        this.tempWorld = tempWorld;
    }
}
