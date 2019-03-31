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

/**
 * Handles everything relating to the world (e.g. reading levels/saves from files, keeping track of enemies states, timers, etc.)
 */
public abstract class SuperLevel extends World {
    private MainCharacter player;
    private ArrayList<Asteroid> asteroid = new ArrayList<>();
    private BackgroundPanel view;
    private MouseHandler mouseHandler;
    private StepHandler stepHandler;
    private ArrayList<Timer> timerList = new ArrayList<>();
    private ArrayList<String> availableLevels;
    private ArrayList<String> availableSaves;
    protected HashMap<String, ImageIcon> iconHashMap = new HashMap<>();
    private int[] playerStats;

    //Stores all the moving enemies and their respective paths together.
    protected HashMap<Enemy, float[]> enemyPath = new HashMap<>();
    protected ArrayList<Enemy> movingEnemies = new ArrayList<>();
    protected ArrayList<Enemy> enemies = new ArrayList<>();

    private STATE State;

    private Game game;
    //Used if loading user created levels.
    private SuperLevel tempWorld;

    /**
     * Constructor used when creating worlds with user created levels loaded.
     */
    public SuperLevel() {
    }

    /**
     * Seconds constructor used when creating worlds with default levels loaded (Level 1-6).
     * @param State The current enum STATE of the game (MENU, GAME, LEVEL_EDITOR).
     * @param levels An ArrayList of all the available levels in the data/Levels folder.
     * @param game Instance of Game.
     * @param saves An ArrayList of all the available saves in the data/Saves folder.
     */
    public SuperLevel(STATE State, ArrayList<String> levels, Game game, ArrayList<String> saves) {
        super(60);
        this.State = State;
        this.availableLevels = levels;
        this.availableSaves = saves;
        this.game = game;
    }

    //Read all the background/foreground images for that level and store their file names in a hash map.
    protected void readFolder(String backgroundPath) {
        iconHashMap.clear();
        File folder = new File(backgroundPath);
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            iconHashMap.put(fileEntry.getName().split("\\.")[0], new ImageIcon(fileEntry.toString()));
        }
    }

    /**
     * Used to initialise and setup the world (e.g. add key, step, and mouse listeners, create certain objects, etc.)
     * @param extraJumpsLimit The maximum amount of jumps the player is allowed to perform while airborne.
     */
    public void populate(int extraJumpsLimit) {
        if (State == STATE.GAME || State == STATE.LEVEL_EDITOR) {
            Shape playerShape = new BoxShape(0.3f, 1.1f);
            //Create the player.
            player = new MainCharacter(this, playerShape, playerStats[0], playerStats[1], playerStats[2], extraJumpsLimit, 6, game.getLevelNum());
            player.setPosition(startPosition());

            if (game.getLevelNum() == 1) {
                createAsteroids();
                getView().setCentre(new Vec2(-35, 0));
                getView().startShaking(0);
            }

            //Create a ground platform.
            if (game.getLevelNum() == 1 || State == STATE.LEVEL_EDITOR || game.getCurrentLevel().equals("Level 1")) {
                Shape shape = new BoxShape(2000, 0.5f);
                Platform ground = new Platform(this, shape, "Ground", "ground");
                //Makes ground invisible.
                ground.setLineColor(new Color(0, 0, 0, 0));
                ground.setFillColor(new Color(0, 0, 0, 0));
                ground.setPosition(new Vec2(0, -11.5f));
                ground.setName("Ground");
            }

            mouseHandler = new MouseHandler(this, view, player, game);

            stepHandler = new StepHandler(view, player, game);
            if (State == STATE.LEVEL_EDITOR || game.isSaveLoaded() || game.getLevelNum() > 1 || game.getLevelNum() == -1) {
                this.addStepListener(stepHandler);
                player.addCollisionListener(new CollisionHandler(game));
                view.addMouseListener(mouseHandler);
            }
            if (game.getLevelNum() > 4 || game.getLevelNum() == -1) {
                getView().addMouseMotionListener(mouseHandler);
            }

            frame.addKeyListener(new KeyHandler(player, this.lookAtCursor, stepHandler, this, game));
        } else if (State == STATE.MENU) { //Initialise the main menu...
            getView().setLayout(null);
            MainMenu menu = new MainMenu(game);
            getView().add(menu.getMainPanel(), BorderLayout.NORTH);
        }

        //JFrame debugView = new DebugViewer(this, 2000, 2000);
    }

    abstract void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound);

    abstract void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound);

    /**
     * @return an ArrayList of all the enemies in the current level.
     */
    public ArrayList<Enemy> getEnemies() { return enemies; }

    protected void findMovingEnemies(float[][] enemyStartingPos) {
        java.util.List<DynamicBody> dynamicBodies = this.getDynamicBodies();
        //Finds all the enemies in the level and stores them in the enemies ArrayList.
        for (DynamicBody body : dynamicBodies) {
            if (body instanceof Enemy) {
                enemies.add((Enemy) body);
            }
        }

        //Finds all the enemies in the level that are supposed to move and stores them in the movingEnemies ArrayList.
        for (Enemy enemy : enemies) {
            for (float[] pos : enemyStartingPos) {
                //It does this by matching the starting position of the enemy with the starting position of the path.
                if (enemy.getPosition().x == pos[0]) {
                    enemyPath.put(enemy, pos);
                    movingEnemies.add(enemy);
                    break;
                }
            }
        }
    }

    /**
     * Will be called to rotate the arm to point at the mouse cursor.
     */
    public ActionListener lookAtCursor = e -> {
        try {
            mouseHandler.calculateTheta(view.getMousePosition());
        } catch (NullPointerException ignored) {
        }
    };

    /**
     * Will be called to create 5 instances of the Asteroid class.
     */
    public void createAsteroids() {
        for (int i = 0; i < 4; i++) {
            asteroid.add(new Asteroid(this, new CircleShape(0.38f), 20, player, game));
            asteroid.get(i).spawn();
        }
    }

    /**
     * Will be used to remove all instances of Asteroid from the game.
     */
    public void destroyAsteroids() {
        for (int i = 0; i < 4; i++) {
            asteroid.get(i).destroy();
        }
        asteroid.removeAll(asteroid);
    }

    /**
     * Used to de-attach the MouseListener and MouseMotionListener from the view.
     */
    public void removeMouseHandler() {
        this.getView().removeMouseListener(mouseHandler);
        this.getView().removeMouseMotionListener(mouseHandler);
    }

    /**
     * @return an ArrayList of all the available levels in the data/Levels folder.
     */
    public ArrayList<String> getAvailableLevels() {
        return availableLevels;
    }

    /**
     * Adds a level name to an ArrayList whenever the player saves their level.
     * @param availableLevels The name of the level that the player has chosen.
     */
    public void addAvailableLevels(String availableLevels) {
        this.availableLevels.add(availableLevels);
    }

    /**
     * @return an ArrayList of all the available saves in the data/Saves folder.
     */
    public ArrayList<String> getAvailableSaves() {
        return availableSaves;
    }

    /**
     * Adds a save name to an ArrayList whenever the player saves their game.
     * @param availableSaves The name of the save that the player has chosen.
     */
    public void addAvailableSaves(String availableSaves) {
        this.availableSaves.add(availableSaves);
    }

    /**
     * @return the current player instance.
     */
    public MainCharacter getPlayer() {
        return player;
    }

    /**
     * @return The starting position of the player in the world as a Vec2.
     */
    public abstract Vec2 startPosition();

    /**
     * @return the current enum STATE of the world.
     */
    public STATE getState() {
        return State;
    }

    /**
     * Set the state of the world.
     * @param state The new enum STATE of the world.
     */
    public void setState(STATE state) {
        State = state;
    }

    /**
     * @return the instance of BackgroundPanel attached to the current world.
     */
    public BackgroundPanel getView() {
        return view;
    }

    /**
     * Attach the view to the current world.
     * @param newView The new view to attach to the world.
     */
    public void setView(BackgroundPanel newView) {
        this.view = newView;
    }

    /**
     * @return the current world/instance of SuperLevel.
     */
    public SuperLevel getWorld() {
        return this;
    }

    /**
     * @return the current instance of MouseHandler that is attached to the world.
     */
    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    /**
     * @return the current instance of StepHandler that is attached to the world.
     */
    public StepHandler getStepHandler() {
        return stepHandler;
    }

    /**
     * Adds a swing timer to an ArrayList in order to keep track of all the current running timers in the game.
     * @param timer A new instance of a Swing Timer.
     */
    public void addTimer(Timer timer) {
        timerList.add(timer);
    }

    /**
     * Stop all the currently running swing timers that were added to teh ArrayList.
     */
    public void stopTimers() {
        for (Timer timer : timerList) {
            timer.stop();
        }
    }

    /**
     * Set the player's current stats (health, armour, ammo).
     * @param playerStats An integer array with the player's stats in the order mentioned above.
     */
    public void setPlayerStats(int[] playerStats) {
        this.playerStats = playerStats;
    }

    /**
     * @return The temporary world instance created when loading a user created level.
     */
    public SuperLevel getTempWorld() {
        return tempWorld;
    }

    /**
     * Set the temporary instance of the world when loading a user level.
     * @param tempWorld The instance of SuperLevel.
     */
    public void setTempWorld(SuperLevel tempWorld) {
        this.tempWorld = tempWorld;
    }

    /**
     * Used to read from the level/save file and load the level/save into the current world.
     * @param levelPath The path to the level or save.
     */
    public void generateMap(String levelPath) {
        String line;
        BufferedReader reader = null;
        String[] text;
        String background = "";

        try {
            reader = new BufferedReader(new FileReader(levelPath));
            while ((line = reader.readLine()) != null) {
                if (!line.substring(0, 1).equals("#") && !line.substring(0, 1).equals("@")) {
                    text = line.split(",");
                    lines.add(line);

                    if (text[0].contains("crumblingPlatform")) {
                        createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/" + text[0] + ".png", 3, "crumblingPlatform", text[0]);
                    } else if (text[0].equals("exit")) {
                        createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/" + text[0] + ".png", 3, "exit", text[0]);
                    } else if (text[0].contains("pickup")) {
                        createItemPickup(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/ItemPickup/" + text[0].substring(6) + ".png", text[0].substring(6));
                    } else if (text[0].equals("enemy")) {
                        if (game.isSaveLoaded()) {
                            if (!text[5].equals(" ") && !text[6].equals(" ")) {
                                createEnemy(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Integer.parseInt(text[3]), Integer.parseInt(text[4]), Float.parseFloat(text[5]), Float.parseFloat(text[6]), true, "data/" + text[0] + ".png", 1.65f);
                            } else {
                                createEnemy(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Integer.parseInt(text[3]), Integer.parseInt(text[4]), 0, 0, false, "data/" + text[0] + ".png", 1.65f);
                            }
                        } else {
                            createEnemy(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Integer.parseInt(text[3]), Integer.parseInt(text[4]), 0, 0, false, "data/" + text[0] + ".png", 1.65f);
                        }
                    } else if (text[0].equals("player")) { //Used when loading player saves.
                        getPlayer().setPosition(new Vec2(Float.parseFloat(text[1]), Float.parseFloat(text[2])));
                        getPlayer().setHealth(Integer.parseInt(text[3]));
                        getPlayer().setArmour(Integer.parseInt(text[4]));
                        getPlayer().setAmmo(Integer.parseInt(text[5]));
                        getPlayer().setExtraJumpsLimit(Integer.parseInt(text[6]));
                        getPlayer().setAcquiredBoots(Boolean.parseBoolean(text[7]));
                        getPlayer().setAcquiredPistol(Boolean.parseBoolean(text[8]));
                        if (getPlayer().isAcquiredPistol()) {
                            game.getWorld().getView().addMouseMotionListener(game.getWorld().getMouseHandler());
                            game.getWorld().getPlayer().setArmImage("data/ArmPistol.png");
                        }
                    } else if (text[0].equals("score")) { //Used when loading player saves.
                        game.getScore().setPrevScore(Float.parseFloat(text[1]));
                        game.getScore().setHighScore(Float.parseFloat(text[2]));
                        game.getScore().setCurrScore(Float.parseFloat(text[3]));
                        game.getScore().setCurrTime(Float.parseFloat(text[4]));
                    } else if (!text[0].equals("ground")) {
                        if (background.equals("Mars Surface") || game.getCurrentLevel().equals("Level 1")) {
                            createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/Platform/" + text[0] + ".png", 15, "platform", text[0]);
                        } else {
                            createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/Platform2/" + text[0] + ".png", 15, "platform", text[0]);
                        }
                    }
                } else if (line.substring(0, 1).equals("#")) { //Reads the background path for that level.
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
    private void createPlatform(float x, float y, float width, float height, String imagePath, float scale, String type, String imageName) {
        Shape shape1 = new BoxShape(width, height);
        Platform platform = new Platform(this, shape1, type, imageName);
        platform.addImage(new BodyImage(imagePath, scale)).setOffset(new Vec2(0, 0.12f));
        platform.setPosition(new Vec2(x, y));
        platform.setName("Body" + lines.size());
        platform.setLineColor(Color.BLUE);
        if (LevelEditorUI.isOutlineEnabled) {
            platform.setAlwaysOutline(true);
        }
    }

    @SuppressWarnings("Duplicates")
    private void createEnemy(float x, float y, int health, int damage, float startPath, float endPath, boolean isMoving, String imagePath, float scale) {
        if (State == STATE.GAME) {
            PolygonShape shape1 = new PolygonShape(-1.186f, -0.816f, -1.186f, -0.1f, -0.251f, 0.825f, 0.251f, 0.825f, 1.181f, -0.105f, 1.186f, -0.821f, -1.186f, -0.821f);
            Enemy enemy = new Enemy(this, shape1, damage, game);
            enemy.setHealth(health);
            enemy.addImage(new BodyImage(imagePath, scale));
            enemy.setPosition(new Vec2(x, y));
            enemy.setName("Body" + lines.size());

            /* Checks the current position of the enemy when created and compares it with its path and moves the enemy accordingly.
            This is used when loading player saves otherwise the enemies won't move when created from the player save. */
            if (isMoving) {
                float[] path = {startPath, endPath};
                enemyPath.put(enemy, path);
                movingEnemies.add(enemy);
                if (enemyPath.get(enemy)[0] > enemyPath.get(enemy)[1]) {
                    enemy.startWalking(-5);
                } else {
                    enemy.startWalking(5);
                }
            }
            if (game.isSaveLoaded()) {
                enemies.add(enemy);
            }

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
}
