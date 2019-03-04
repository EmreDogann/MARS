package game;

import city.cs.engine.DebugViewer;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Game {
    private static SuperLevel world;
    private static BackgroundPanel view;
    static final JFrame frame = new JFrame("Basic World");
    static int levelNum = 0;
    static String currentLevel;


    public Game() {
        File[] files = new File("data/Levels").listFiles();
        ArrayList<String> levels = new ArrayList<>();
        assert files != null;
        for (File file : files) {
            levels.add(file.getName().split("\\.")[0]);
        }

        setWorld(new Menu(levels));
        view = new BackgroundPanel(getWorld(), 800, 500);
        getWorld().setView(view);
        getWorld().populate();

        frame.add(getWorld().getView());
        SwingUtilities.invokeLater(() -> {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationByPlatform(true);
            frame.setResizable(true);
            frame.pack();
            frame.setVisible(true);
            frame.setFocusable(true);
            frame.requestFocusInWindow();
        });
    }

    static void loadLevel() {
        getWorld().stop();
        getWorld().getView().removeAll();
        //Removes the previous mouse listeners
        getWorld().removeMouseHandler();
        if (levelNum == -1) {
            class UserLevel extends SuperLevel {

                private UserLevel(ArrayList<String> levels) {
                    super(STATE.GAME, levels);
                    view.setState(STATE.GAME);
                }

                @Override
                public Vec2 startPosition() {
                    return new Vec2(0, -2);
                }
            }
            setWorld(new UserLevel(getWorld().getAvailableLevels()));
            getWorld().setView(view);
            getWorld().getView().setWorld(getWorld());
            getWorld().populate();
            getWorld().getView().player = getWorld().getPlayer();

            UIControls UIMenu = new UIControls();
            view.add(UIMenu.getMainPanel(), BorderLayout.NORTH);

            getWorld().generateMap("data/Levels/" + currentLevel + ".txt");
            getWorld().start();
        } else if (levelNum == 0) {
            setWorld(new Menu(getWorld().getAvailableLevels()));
            getWorld().setView(view);
            view.setState(STATE.MENU);
            getWorld().getView().setWorld(getWorld());
            getWorld().populate();
            getWorld().start();
        } else if (levelNum == 1) {
            setWorld(new LevelOne(getWorld().getAvailableLevels()));
            getWorld().setView(view);
            getWorld().getView().setWorld(getWorld());
            getWorld().populate();
            getWorld().getView().player = getWorld().getPlayer();

            UIControls UIMenu = new UIControls();
            view.add(UIMenu.getMainPanel(), BorderLayout.NORTH);

            getWorld().generateMap("data/Levels/Level 1.txt");
            getWorld().start();
        } else if (levelNum == 2) {
            setWorld(new LevelTwo(getWorld().getAvailableLevels()));
            getWorld().setView(view);
            getWorld().getView().setWorld(getWorld());
            getWorld().populate();
            getWorld().getView().player = getWorld().getPlayer();

            UIControls UIMenu = new UIControls();
            view.add(UIMenu.getMainPanel(), BorderLayout.NORTH);

            getWorld().generateMap("data/Levels/Level 2.txt");
            getWorld().start();
        } else if (levelNum == 3) {
            setWorld(new LevelOne(getWorld().getAvailableLevels()));
            getWorld().populate();
            getWorld().generateMap("data/Levels/Level 3.txt");
        } else if (levelNum == 4) {
            setWorld(new LevelOne(getWorld().getAvailableLevels()));
            getWorld().populate();
            getWorld().generateMap("data/Levels/Level 4.txt");
        } else if (levelNum == 5) {
            setWorld(new LevelOne(getWorld().getAvailableLevels()));
            getWorld().populate();
            getWorld().generateMap("data/Levels/Level 5.txt");
        } else if (levelNum == 6) {
            setWorld(new LevelEditor(getWorld().getAvailableLevels()));
            getWorld().setView(view);
            getWorld().getView().setWorld(getWorld());
            getWorld().populate();

            LevelEditorUI LevelEditorUI = new LevelEditorUI();
            view.add(LevelEditorUI.getMainPanel(), BorderLayout.NORTH);

            getWorld().start();
        }
        frame.revalidate();
    }

    public static void main(String[] args) {
        new Game();
    }

    public static SuperLevel getWorld() {
        return world;
    }

    private static void setWorld(SuperLevel world1) {
        world = world1;
    }
}
