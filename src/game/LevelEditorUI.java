package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static game.MouseHandler.lines;

public class LevelEditorUI implements ActionListener, ItemListener {

    private JPanel mainPanel;
    private JCheckBox enableGridCheckBox;
    private JCheckBox enableAsteroidsCheckBox;
    private JComboBox comboBox;
    private JLabel selectionText;
    private JMenuBar menuBar;
    private JCheckBoxMenuItem cbMenuItem;
    static String createItem;

    LevelEditorUI() {
        Game.getWorld().getView().setGridResolution(0);
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        mainPanel.setBounds(0, 25, 790, 200);

        //Where the GUI is created:
        JMenu menu, submenu;
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
        String[] items = {"Main Menu", "Play", "Save", "Load", "Restart", "Help"};
        for (String item : items) {
            menuItem = new JMenuItem(item);
            menu.add(menuItem);
            menuItem.addActionListener(this);
            menuItem.setBorderPainted(false);
        }

        //A group of check box menu items
        menu.addSeparator();
        cbMenuItem = new JCheckBoxMenuItem("Enable On-Screen Options");
        cbMenuItem.setSelected(true);
        menu.add(cbMenuItem);
        cbMenuItem.addItemListener(this);

        //A submenu
        menu.addSeparator();
        submenu = new JMenu("A submenu");
        submenu.setMnemonic(KeyEvent.VK_S);

        menuItem = new JMenuItem("An item in the submenu");
        submenu.add(menuItem);

        menuItem = new JMenuItem("Another item");
        submenu.add(menuItem);
        menu.add(submenu);

        Game.getWorld().getView().add(menuBar);

        enableGridCheckBox.addItemListener(this);
        enableAsteroidsCheckBox.addItemListener(this);
        comboBox.addActionListener(this);
        createItem = (String) comboBox.getSelectedItem();
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Main Menu")) {
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
                Game.levelNum = 0;
                Game.getWorld().getView().setState(STATE.MENU);
                Game.loadLevel();
            }
        } else if (e.getActionCommand().equals("Play")) {
            if (Game.currentLevel != null) {
                Object[] options = {"Yes", "No"};
                int n = JOptionPane.showOptionDialog(Game.frame,
                        "Would you like to play the current level?",
                        "Play",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[1]);
                if (n == 0) {
                    Game.frame.remove(menuBar);
                    Game.getWorld().getView().setGridResolution(0);
                    Game.levelNum = -1;
                    Game.loadLevel();
                }
            } else {
                JOptionPane.showMessageDialog(Game.frame, "ERROR: You cannot play an empty level!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Save")) {
            if (Game.currentLevel != null || lines.size() != 0) {
                String result = (String) JOptionPane.showInputDialog(
                        Game.frame,
                        "Please enter a name for your level...",
                        "Save Level...",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null, null);

                if (result != null) {
                    Game.getWorld().addAvailableLevels(result);

                    BufferedWriter writer = null;

                    try {
                        writer = new BufferedWriter(new FileWriter("data/Levels/" + result + ".txt"));
                        for (Object line : lines) {
                            writer.write(line.toString() + "\n");
                        }
                        writer.flush();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } finally {
                        if (writer != null) {
                            try {
                                writer.close();
                            } catch (IOException err) {
                                err.printStackTrace();
                            }
                        }
                    }
                    Game.currentLevel = result;
                }
            } else {
                JOptionPane.showMessageDialog(Game.frame, "ERROR: You cannot save an empty level!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Load")) {
            Object[] possibilities = Game.getWorld().getAvailableLevels().toArray();
            String result = (String) JOptionPane.showInputDialog(
                    Game.frame,
                    "Please select a level to load...",
                    "Load Level...",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities, null);

            if (result != null) {
                Game.levelNum = 6;
                Game.loadLevel();
                ArrayList<String> levels = Game.getWorld().getAvailableLevels();
                for (String level : levels) {
                    if (result.equals(level)) {
                        Game.getWorld().generateMap("data/Levels/" + result + ".txt");
                        Game.currentLevel = result;
                    }
                }
            }
        } else if (e.getActionCommand().equals("Restart")) {
            Game.levelNum = 6;
            Game.loadLevel();
        } else if (e.getActionCommand().equals("Help")) {
            JOptionPane.showMessageDialog(Game.frame, "Press Middle-Click to create the object currently selected.\n" +
                    "Press Right-Click while the mouse is over an object to delete said object.\n" +
                    "Press any mouse button while over an object to drag and move it around the space.", "Controls", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getActionCommand().equals("comboBoxChanged")) {
            createItem = (String) comboBox.getSelectedItem();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == enableGridCheckBox) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Game.getWorld().getView().setGridResolution(1);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                Game.getWorld().getView().setGridResolution(0);
            }
        } else if (e.getItem() == enableAsteroidsCheckBox) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Game.getWorld().createAsteroids();
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                Game.getWorld().destroyAsteroids();
            }
        } else if (e.getItem() == cbMenuItem) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableGridCheckBox.setVisible(true);
                enableAsteroidsCheckBox.setVisible(true);
                selectionText.setVisible(true);
                comboBox.setVisible(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                enableGridCheckBox.setVisible(false);
                enableAsteroidsCheckBox.setVisible(false);
                selectionText.setVisible(false);
                comboBox.setVisible(false);
            }
        }
    }
}
