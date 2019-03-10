package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static game.MouseHandler.lines;

public class UIControls implements ActionListener {
    private JMenuBar menuBar;
    private boolean shaking = false;
    private SuperLevel world;
    private Game game;

    UIControls(SuperLevel world1, Game game) {
        this.world = world1;
        this.game = game;
        world.getView().setGridResolution(0);

        //Where the GUI is created:
        JMenu menu;
        JMenuItem menuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 56, 20);
        menuBar.setBorderPainted(false);

        //Build the first menu.
        menu = new JMenu("Options");
        menu.setBorderPainted(false);
        menuBar.add(menu);

        //A group of JMenuItems
        String[] items = {"Main Menu", "Pause", "Save", "Load", "Restart", "Help"};
        for (String item : items) {
            menuItem = new JMenuItem(item);
            menu.add(menuItem);
            menuItem.addActionListener(this);
            menuItem.setBorderPainted(false);
        }

        world.getView().add(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Main Menu":
                //Custom button text
                Object[] options = {"Yes, please",
                        "No, thanks",
                        "Cancel"};
                int n = JOptionPane.showOptionDialog(Game.frame,
                        "Loading the main menu will cause you to lose your progress. Do you want to save your progress?",
                        "WARNING",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[2]);

                if (n == 0 || n == 1) {
                    Game.frame.remove(menuBar);
                    world.getView().setGridResolution(0);
                    if (world.getView().getxBound() != 1) {
                        world.getView().stopShaking();
                    }
                    world.stopTimers();
                    game.setLevelNum(0);
                    world.getView().setState(STATE.MENU);
                    game.loadLevel();
                }
                break;
            case "Pause": {
                JMenuItem item = (JMenuItem) e.getSource();
                if (world.getView().getxBound() > 1) {
                    shaking = true;
                }
                world.getView().stopShaking();
                world.stop();
                item.setText("Resume");
                break;
            }
            case "Resume": {
                JMenuItem item = (JMenuItem) e.getSource();
                if (shaking) {
                    world.getView().startShaking(0);
                }
                world.start();
                item.setText("Pause");
                break;
            }
            case "Save":
                //Add saving functionality here.
                break;
            case "Load":
                Object[] possibilities = world.getAvailableLevels().toArray();
                String result = (String) JOptionPane.showInputDialog(
                        Game.frame,
                        "Please select a level to load...",
                        "Load Level...",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        possibilities, null);

                if (result != null) {
                    if (result.length() >= 5) {
                        if (result.substring(0, 5).equals("Level")) {
                            game.setLevelNum(Integer.parseInt(result.substring(6)));
                        } else {
                            game.setLevelNum(-1);
                            game.setCurrentLevel(result);
                        }
                    } else {
                        game.setLevelNum(-1);
                        game.setCurrentLevel(result);
                    }
                    world.getView().stopShaking();
                    world.stopTimers();
                    world.getPlayer().setStats(game.getDefaultPlayerStats());
                    game.loadLevel();
                }
                break;
            case "Restart":
                world.stopTimers();
                world.getPlayer().setStats(game.getDefaultPlayerStats());
                game.loadLevel();
                break;
            case "Help":
                JOptionPane.showMessageDialog(Game.frame, "Press Mouse 1 to fire.\n" +
                        "Use A to move left, and use D to move right.\n" +
                        "Press SPACE to jump.\n" +
                        "Use the mouse to aim your weapon.", "Controls", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }
}
