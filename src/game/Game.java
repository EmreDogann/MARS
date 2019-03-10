package game;

import city.cs.engine.DebugViewer;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static game.MouseHandler.lines;

public class Game {
    private SuperLevel world;
    private BackgroundPanel view;
    static final JFrame frame = new JFrame("MARS");
    private int levelNum;
    private String currentLevel;
    private int[] defaultPlayerStats;
    private Score score;

    private Game() {
        File[] files = new File("data/Levels").listFiles();
        ArrayList<String> levels = new ArrayList<>();
        assert files != null;
        for (File file : files) {
            levels.add(file.getName().split("\\.")[0]);
        }
        world = new Menu(levels, this);
        view = new BackgroundPanel(world, 800, 500, this);
        view.setBackground(Color.BLACK);
        world.setView(view);
        world.populate(2);
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

    public static void main(String[] args) {
        new Game();
    }

    void gameWin() {
        String[] options = {"Main Menu","Quit"};
        int n = JOptionPane.showOptionDialog(Game.frame,
                "WINNER! You have completed the game!",
                "Winner Winner Chicken Dinner!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, options, null);
        lines.clear();
        if (n == 0) {
            setLevelNum(0);
            loadLevel();
        } else {
            System.exit(0);
        }
    }

    void gameOver() {
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
        if (n == 0) {
            setLevelNum(getLevelNum());
            world.stopTimers();
            world.getPlayer().setStats(getDefaultPlayerStats());
            loadLevel();
        } else if (n == 1) {
            setLevelNum(0);
            loadLevel();
        } else {
            System.exit(0);
        }
    }

    void loadLevel() {
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
        if (getLevelNum() == -1) {
            class UserLevel extends SuperLevel {

                private SuperLevel tempWorld;
                private String backgroundPath;

                private UserLevel(ArrayList<String> levels, Game game) {
                    super(STATE.GAME, levels, game);
                    view.setState(STATE.GAME);
                    extractBackground();
                }

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

                    System.out.println(backgroundPath);
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
            world = new UserLevel(world.getAvailableLevels(), this);
            setupLevel(playerStats);

            world.generateMap("data/Levels/" + getCurrentLevel() + ".txt");
        } else if (getLevelNum() == 0) {
            world = new Menu(world.getAvailableLevels(), this);
            view.setState(STATE.MENU);
            world.setView(view);
            world.getView().setSuperWorld(world);
            world.populate(0);
        } else if (getLevelNum() == 1) {
            world = new LevelOne(world.getAvailableLevels(), this);
            setupLevel(playerStats);
        } else if (getLevelNum() == 2) {
            world = new LevelTwo(world.getAvailableLevels(), this);
            currentLevel = "Level 2";
            setupLevel(playerStats);
        } else if (getLevelNum() == 3) {
            world = new LevelThree(world.getAvailableLevels(), this);
            currentLevel = "Level 3";
            setupLevel(playerStats);
        } else if (getLevelNum() == 4) {
            world = new LevelFour(world.getAvailableLevels(), this);
            currentLevel = "Level 4";
            setupLevel(playerStats);
        } else if (getLevelNum() == 5) {
            world = new LevelFive(world.getAvailableLevels(), this);
            currentLevel = "Level 5";
            setupLevel(playerStats);
        } else if (getLevelNum() == 6) {
            world = new LevelSix(world.getAvailableLevels(), this);
            currentLevel = "Level 6";
            setupLevel(playerStats);
        } else if (getLevelNum() == 7) {
            world = new LevelEditor(world.getAvailableLevels(), this);
            view.setState(STATE.LEVEL_EDITOR);
            world.setView(view);
            world.getView().setSuperWorld(world);
            world.setPlayerStats(defaultPlayerStats);
            world.populate(2);

            LevelEditorUI LevelEditorUI = new LevelEditorUI(world, this);
            view.add(LevelEditorUI.getMainPanel(), BorderLayout.NORTH);

        }
        world.start();
        frame.revalidate();
    }

    private void setupLevel(int[] playerStats) {
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

        if (!currentLevel.equals("Level 1")) {
            view.setState(STATE.GAME);
        } else {
            view.setState(STATE.MENU);
        }

        UIControls UIMenu = new UIControls(world, this);
    }

    public String getCurrentLevel() {
        return currentLevel;
    }
    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getLevelNum() {
        return levelNum;
    }
    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public SuperLevel getWorld() {
        return world;
    }
    public void setWorld(SuperLevel world) {
        this.world = world;
    }

    public int[] getDefaultPlayerStats() {
        return defaultPlayerStats;
    }
    public void setDefaultPlayerStats(int[] playerStats) {
        this.defaultPlayerStats = playerStats;
    }

    public Score getScore() {
        return score;
    }
}
