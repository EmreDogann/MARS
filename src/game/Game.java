package game;

import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static game.MouseHandler.lines;

/**
 * Class that will hold the current different states of the game and handle different tasks such as loading new levels.
 */
public class Game {
    private SuperLevel world;
    private BackgroundPanel view;
    /**
     * The frame/window which the game will be shown in.
     */
    static final JFrame frame = new JFrame("MARS");
    private int levelNum;
    private String currentLevel;
    private int[] defaultPlayerStats;
    private Score score;
    private boolean isSaveLoaded = false;

    private Game() {
        //Read and store all the file name from data/Levels.
        File[] files = new File("data/Levels").listFiles();
        ArrayList<String> levels = new ArrayList<>();
        assert files != null;
        for (File file : files) {
            levels.add(file.getName().split("\\.")[0]);
        }

        //Read and store all the file name from data/Saves.
        files = new File("data/Saves").listFiles();
        ArrayList<String> saves = new ArrayList<>();
        for (File file : files) {
            saves.add(file.getName().split("\\.")[0]);
        }

        world = new Menu(levels, this, saves);
        view = new BackgroundPanel(world, 889, 500, this);
        view.setBackground(Color.BLACK);
        world.setView(view);
        world.populate(2);
        //Set the default stats for the player.
        defaultPlayerStats = new int[]{100, 25, 50};

        frame.add(world.getView());
        SwingUtilities.invokeLater(() -> {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationByPlatform(true);
            frame.setResizable(false);
            frame.pack();
            frame.setVisible(true);
            frame.setFocusable(true);
            frame.requestFocusInWindow();
        });
    }

    /**
     * Starts the game by calling the constructor for the Game class.
     * @param args String array.
     */
    public static void main(String[] args) { new Game(); }

    /**
     * Called when the player finishes the game (i.e reaches the end of level 6).
     */
    public void gameWin() {
        String[] options = {"Main Menu","Quit"};
        int n = JOptionPane.showOptionDialog(Game.frame,
                "WINNER! You have completed the game!",
                "Winner Winner Chicken Dinner!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, options, null);
        lines.clear();
        //If the player chose to go to the main menu...
        if (n == 0) {
            world.stopTimers();
            world.getView().stopShaking();
            setLevelNum(0);
            loadLevel();
        } else { //If the player chose to quit...
            System.exit(0);
        }
    }

    /**
     * Called when the player dies in any way (runs out of health or falls of the level).
     */
    public void gameOver() {
        world.stopTimers();
        String[] options = {"Restart Level",
                "Main Menu",
                "Quit"};
        int n = JOptionPane.showOptionDialog(Game.frame,
                "GAME OVER! Please select one of the options below to continue...",
                "GAME OVER",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[2]);
        lines.clear();
        //If the player chose to restart the level...
        if (n == 0) {
            setLevelNum(getLevelNum());
            world.getView().stopShaking();
            world.getPlayer().setStats(getDefaultPlayerStats());
            loadLevel();
        } else if (n == 1) { //If the player chose to go to the main menu...
            setLevelNum(0);
            loadLevel();
        } else { //If the player chose to quit...
            System.exit(0);
        }
    }

    /**
     * Used to load the next/desired level. Also used to load the main menu or level editor.
     */
    public void loadLevel() {
        //Pause the game.
        world.stop();
        world.getView().removeAll();
        //Removes the previous mouse listeners
        world.removeMouseHandler();
        int[] playerStats;

        if (world.getPlayer() != null) {
            playerStats = new int[] {world.getPlayer().getHealth(), world.getPlayer().getArmour(), world.getPlayer().getAmmo()};
        } else {
            playerStats = defaultPlayerStats;
        }

        //Load user created level.
        if (getLevelNum() == -1) {
            class UserLevel extends SuperLevel {

                private SuperLevel tempWorld;
                private String backgroundPath;

                private UserLevel(ArrayList<String> levels, Game game, ArrayList<String> availableSaves) {
                    super(STATE.GAME, levels, game, availableSaves);
                    extractBackground();
                }

                //Find out which background to load for the user created level.
                private void extractBackground() {
                    String line;
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader("data/Levels/" + getCurrentLevel() + ".txt"));
                        if ((line = reader.readLine()) != null && line.substring(0, 1).equals("#")) {
                            backgroundPath = line.split("#")[1];
                            reader.close();
                            readFolder(backgroundPath);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (backgroundPath.contains("Mars Surface")) {
                        tempWorld = new LevelOne();
                    } else if (backgroundPath.contains("Industrial")) {
                        tempWorld = new LevelTwo();
                    } else if (backgroundPath.contains("Mine")) {
                        tempWorld = new LevelThree();
                    } else if (backgroundPath.contains("Dungeon")) {
                        tempWorld = new LevelFour();
                    } else if (backgroundPath.contains("Crystal")) {
                        tempWorld = new LevelFive();
                    } else {
                        tempWorld = new LevelSix();
                    }
                    tempWorld.setView(view);
                }

                @Override
                void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
                    tempWorld.generateBackground(g, x, width, height, rand, xBound, yBound);
                }

                @Override
                void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
                    tempWorld.generateForeground(g, x, width, height, rand, xBound, yBound);
                }

                @Override
                public Vec2 startPosition() {
                    Vec2 position;
                    if (tempWorld != null && !(tempWorld instanceof LevelOne)) {
                        position = tempWorld.startPosition();
                    } else {
                        position = new Vec2(0, 0);
                    }
                    return position;
                }
            }
            world = new UserLevel(world.getAvailableLevels(), this, world.getAvailableSaves());
            setupLevel(playerStats);

            //Load the user level.
            world.generateMap("data/Levels/" + getCurrentLevel() + ".txt");
        } else if (getLevelNum() == 0) { //Load the main menu...
            world = new Menu(world.getAvailableLevels(), this, world.getAvailableSaves());

            world.setView(view);
            world.getView().setSuperWorld(world);
            world.populate(0);
        } else if (getLevelNum() == 1) { //Load level 1...
            world = new LevelOne(world.getAvailableLevels(), this, world.getAvailableSaves());
            setupLevel(playerStats);
        } else if (getLevelNum() == 2) { //Load level 2...
            currentLevel = "Level 2";
            world = new LevelTwo(world.getAvailableLevels(), this, world.getAvailableSaves());
            setupLevel(playerStats);
        } else if (getLevelNum() == 3) { //Load level 3...
            currentLevel = "Level 3";
            world = new LevelThree(world.getAvailableLevels(), this, world.getAvailableSaves());
            setupLevel(playerStats);
        } else if (getLevelNum() == 4) { //Load level 4...
            currentLevel = "Level 4";
            world = new LevelFour(world.getAvailableLevels(), this, world.getAvailableSaves());
            setupLevel(playerStats);
        } else if (getLevelNum() == 5) { //Load level 5...
            currentLevel = "Level 5";
            world = new LevelFive(world.getAvailableLevels(), this, world.getAvailableSaves());
            setupLevel(playerStats);
        } else if (getLevelNum() == 6) { //Load level 6...
            currentLevel = "Level 6";
            world = new LevelSix(world.getAvailableLevels(), this, world.getAvailableSaves());
            setupLevel(playerStats);
        } else if (getLevelNum() == 7) { //Load level editor.
            world = new LevelEditor(world.getAvailableLevels(), this, world.getAvailableSaves());
            world.setView(view);
            world.getView().setSuperWorld(world);
            world.setPlayerStats(defaultPlayerStats);
            world.populate(2);

            this.setCurrentLevel(null);

            LevelEditorUI LevelEditorUI = new LevelEditorUI(world, this);
            view.add(LevelEditorUI.getMainPanel(), BorderLayout.NORTH);
        }
        world.start();
        //Revalidate to show newly added UI elements.
        frame.revalidate();
    }

    //Initialise all the component for the level.
    void setupLevel(int[] playerStats) {
        //Add score.
        score = new Score(this);
        world.setView(view);
        world.getView().setSuperWorld(world);
        world.setPlayerStats(playerStats);
        if (levelNum > 2) {
            world.populate(2);
        } else {
            world.populate(1);
        }

        world.getView().player = world.getPlayer();

        //Add Menu bar.
        if (!currentLevel.equals("Level 1") || isSaveLoaded || levelNum == -1) {
            UIControls UIMenu = new UIControls(world, this);
        }

        //Required here otherwise loading user saves won't work.
        world.start();
        frame.revalidate();
    }

    /**
     * @return the current level string.
     */
    public String getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Sets the current level to the one specified in the currentLevel parameter. Used to determine the current level that is loaded.
     * @param currentLevel String which identifies the level's name as a String.
     */
    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }

    /**
     * @return the current level mode.
     */
    public int getLevelNum() {
        return levelNum;
    }

    /**
     * Sets the level number to the one specified in the levelNum parameter. Used to determine the current level mode (Level 1-6, or Main Menu, or Level Editor, or a user created level).
     * @param levelNum Int which specifies the current level mode.
     */
    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    /**
     * @return the current instance of SuperLevel (current world).
     */
    public SuperLevel getWorld() {
        return world;
    }

    /**
     * Updates the stored instance of the world to the new world specified in the world parameter.
     * @param world Specifies the current world of type SuperLevel.
     */
    public void setWorld(SuperLevel world) {
        this.world = world;
    }

    /**
     * @return the view attached to the world.
     */
    public BackgroundPanel getView() {
        return view;
    }

    /**
     * Updates the attached to the world with a new one.
     * @param view Instance of type BackgroundPanel.
     */
    public void setView(BackgroundPanel view) {
        this.view = view;
    }

    /**
     * @return the default player stats (health, armour, ammo) as an integer array.
     */
    public int[] getDefaultPlayerStats() {
        return defaultPlayerStats;
    }

    /**
     * @return the instance of the Score class.
     */
    public Score getScore() {
        return score;
    }

    /**
     * @return the boolean value of if the current level that is loaded has been saved to the 'Levels' folder. This is automatically true if the current level is any of the non-user created ones (Level 1-6).
     */
    public boolean isSaveLoaded() {
        return isSaveLoaded;
    }

    /**
     * Sets the state of if the current level that is loaded has been saved to the 'Levels' folder or not.
     * @param saveLoaded Boolean value stating whether or not the current level has been saved.
     */
    public void setSaveLoaded(boolean saveLoaded) {
        isSaveLoaded = saveLoaded;
    }
}
