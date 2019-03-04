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
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private boolean shaking = false;

    UIControls() {

        Game.getWorld().getView().setGridResolution(0);
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        mainPanel.setBounds(0, 25, 790, 200);

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

        Game.getWorld().getView().add(menuBar);
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
                    Game.getWorld().getView().setGridResolution(0);
                    if (Game.getWorld().getView().getxBound() != 1) {
                        Game.getWorld().getView().stopShaking();
                    }
                    Game.getWorld().stopTimers();
                    Game.levelNum = 0;
                    Game.getWorld().getView().setState(STATE.MENU);
                    Game.loadLevel();
                }
                break;
            case "Pause": {
                JMenuItem item = (JMenuItem) e.getSource();
                if (Game.getWorld().getView().getxBound() > 1) {
                    shaking = true;
                }
                Game.getWorld().getView().stopShaking();
                Game.getWorld().stop();
                item.setText("Resume");
                break;
            }
            case "Resume": {
                JMenuItem item = (JMenuItem) e.getSource();
                if (shaking) {
                    Game.getWorld().getView().startShaking();
                }
                Game.getWorld().start();
                item.setText("Pause");
                break;
            }
            case "Save":
//            //Add saving functionality here.
//            String result = (String) JOptionPane.showInputDialog(
//                    Game.frame,
//                    "Please enter a name for your level...",
//                    "Save Level...",
//                    JOptionPane.PLAIN_MESSAGE,
//                    null,
//                    null, null);
//
//            if (result != null) {
//                Game.getWorld().addAvailableLevels(result);
//
//                BufferedWriter writer = null;
//
//                try {
//                    writer = new BufferedWriter(new FileWriter("data/Levels/" + result + ".txt"));
//                    for (Object line : lines) {
//                        writer.write(line.toString() + "\n");
//                    }
//                    writer.flush();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                } finally {
//                    if (writer != null) {
//                        try {
//                            writer.close();
//                        } catch (IOException err) {
//                            err.printStackTrace();
//                        }
//                    }
//                }
//                Game.currentLevel = result;
//            }
                break;
            case "Load":
                Object[] possibilities = Game.getWorld().getAvailableLevels().toArray();
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
                            Game.levelNum = Integer.parseInt(result.substring(6));
                        } else {
                            Game.levelNum = -1;
                            Game.currentLevel = result;
                        }
                    } else {
                        Game.levelNum = -1;
                        Game.currentLevel = result;
                    }
                    Game.getWorld().getView().stopShaking();
                    Game.getWorld().stopTimers();
                    Game.loadLevel();
                }
                break;
            case "Restart":
                Game.getWorld().stopTimers();
                Game.loadLevel();
                break;
            case "Help":
                JOptionPane.showMessageDialog(Game.frame, "Press Mouse 1 to fire.\n" +
                        "Use A to move left, and use D to move right.\n" +
                        "Press SPACE to jump.\n" +
                        "Use the mouse to aim your weapon.", "Controls", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

//    public UIControls() {
//        mainPanel.setBackground(new Color(0, 0, 0, 0));
//        mainPanel.setBounds(BackgroundPanel.WIDTH-180, 5, 175, 65);
//        restartButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                Game.loadLevel();
//            }
//        });
//        pauseButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (pauseStatus == 0) {
//                    pauseButton.setText("Resume");
//                    Game.getWorld().stop();
//                    pauseStatus++;
//                } else {
//                    pauseButton.setText("Pause");
//                    Game.getWorld().start();
//                    pauseStatus--;
//                }
//                //Game.frame.requestFocusInWindow();
//            }
//        });
//        mainMenuButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //Custom button text
//                Object[] options = {"Yes, please",
//                        "No, thanks",
//                        "Cancel"};
//                int n = JOptionPane.showOptionDialog(Game.frame,
//                        "Loading the main menu will cause you to lose your progress. Do you want to save your progress?",
//                        "WARNING",
//                        JOptionPane.YES_NO_CANCEL_OPTION,
//                        JOptionPane.WARNING_MESSAGE,
//                        null,
//                        options,
//                        options[2]);
//
//                if (n == 0 || n == 1) {
//                    if (Game.levelNum == 1) {
//                        Game.getWorld().getView().stopShaking();
//                    }
//                    Game.levelNum = 0;
//                    Game.getWorld().stopTimers();
//                    Game.getWorld().getView().setState(STATE.MENU);
//                    Game.loadLevel();
//                }
//            }
//        });
//    }

    JPanel getMainPanel() { return mainPanel; }
}
