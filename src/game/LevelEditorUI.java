package game;

import city.cs.engine.DynamicBody;
import city.cs.engine.StaticBody;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import static game.MouseHandler.lines;

public class LevelEditorUI implements ActionListener, ItemListener {

    private JPanel mainPanel;
    private JCheckBox enableGridCheckBox;
    private JCheckBox enableAsteroidsCheckBox;
    private JComboBox objectComboBox;
    private JLabel objectSelectionText;
    private JComboBox backgroundComboBox;
    private JLabel bgSelectionText;
    private JCheckBox enableOutline;
    private JMenuBar menuBar;
    private JCheckBoxMenuItem cbMenuItem;
    static String createItem1;
    static String createItem2;
    private SuperLevel world;
    private Game game;
    public static boolean isOutlineEnabled = false;

    LevelEditorUI(SuperLevel world1, Game game) {
        lines.clear();
        this.world = world1;
        this.game = game;
        world.getView().setGridResolution(0);
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

        world.getView().add(menuBar);

        enableGridCheckBox.addItemListener(this);
        enableAsteroidsCheckBox.addItemListener(this);
        enableOutline.addItemListener(this);
        objectComboBox.addActionListener(this);
        backgroundComboBox.addActionListener(this);
        createItem1 = (String) objectComboBox.getSelectedItem();
        createItem2 = (String) backgroundComboBox.getSelectedItem();
        switch (createItem2) {
            case "Mars Surface":
                world.setTempWorld(new LevelOne());
                break;
            case "Industrial":
                world.setTempWorld(new LevelTwo());
                break;
            case "Mine":
                world.setTempWorld(new LevelThree());
                break;
            case "Dungeon":
                world.setTempWorld(new LevelFour());
                break;
            case "Crystal":
                world.setTempWorld(new LevelFive());
                break;
            case "Lava":
                world.setTempWorld(new LevelSix());
                break;
        }
        world.getTempWorld().setView(world.getView());
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
                world.getView().setGridResolution(0);
                game.setLevelNum(0);
                world.getView().setState(STATE.MENU);
                lines.clear();
                game.loadLevel();
            }
        } else if (e.getActionCommand().equals("Play")) {
            if (game.getCurrentLevel() != null) {
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
                    world.getView().setGridResolution(0);
                    world.getPlayer().setStats(game.getDefaultPlayerStats());
                    game.setLevelNum(game.getLevelNum());
                    game.loadLevel();
                }
            } else {
                JOptionPane.showMessageDialog(Game.frame, "ERROR: You cannot play an empty level!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Save")) {
            if (game.getCurrentLevel() != null || lines.size() != 0) {
                String result = (String) JOptionPane.showInputDialog(
                        Game.frame,
                        "Please enter a name for your level...",
                        "Save Level...",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null, null);

                if (result != null) {
                    if (!result.substring(0, 5).equals("Level")) {
                        JOptionPane.showMessageDialog(Game.frame,
                                "ERROR: Your name cannot being with \"Level\" as this is reserved for the main levels!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else if (world.getAvailableLevels().contains(result)) {
                        world.addAvailableLevels(result);

                        BufferedWriter writer = null;

                        try {
                            writer = new BufferedWriter(new FileWriter("data/Levels/" + result + ".txt"));
                            writer.write("#data/Backgrounds/" + createItem2 + "/\n");
                            for (String line : lines) {
                                writer.write(line + "\n");
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
                        game.setCurrentLevel(result);
                    } else {
                        JOptionPane.showMessageDialog(Game.frame,
                                "ERROR: There is already a level with this name!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Game.frame, "ERROR: You cannot save an empty level!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Load")) {
            Object[] possibilities = world.getAvailableLevels().toArray();
            String result = (String) JOptionPane.showInputDialog(
                    Game.frame,
                    "Please select a level to load...",
                    "Load Level...",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities, null);

            if (result != null) {
                for (DynamicBody body : world.getDynamicBodies()) {
                    if (body.getName() != "Player") {
                        body.destroy();
                    }
                }
                for (StaticBody body : world.getStaticBodies()) {
                    if (body.getName() != "Ground") {
                        body.destroy();
                    }
                }
                world.getDynamicBodies().clear();
                world.getStaticBodies().clear();
                lines.clear();
                ArrayList<String> levels = world.getAvailableLevels();
                for (String level : levels) {
                    if (result.equals(level)) {
                        game.setCurrentLevel(result);
                        if (result.substring(0, 5).equals("Level")) {
                            game.setLevelNum(Integer.parseInt(result.substring(6, 7)));
                        } else {
                            game.setLevelNum(-1);
                        }
                        world.getPlayer().setAcquiredBoots(true);
                        world.generateMap("data/Levels/" + result + ".txt");
                    }
                }

                String line;
                BufferedReader reader = null;
                String[] text;

                try {
                    reader = new BufferedReader(new FileReader("data/Levels/" + result + ".txt"));
                    if ((line = reader.readLine()) != null) {
                        if (line.substring(0, 1).equals("#")) {
                            text = line.split("#");
                            text = text[1].split("/");
                            backgroundComboBox.setSelectedItem(text[2].split("\\.")[0]);
                        }
                    }
                } catch (IOException err) {
                    err.printStackTrace();
                }

                switch (backgroundComboBox.getSelectedItem().toString()) {
                    case "Mars Surface":
                        world.setTempWorld(new LevelOne());
                        break;
                    case "Industrial":
                        world.setTempWorld(new LevelTwo());
                        break;
                    case "Mine":
                        world.setTempWorld(new LevelThree());
                        break;
                    case "Dungeon":
                        world.setTempWorld(new LevelFour());
                        break;
                    case "Crystal":
                        world.setTempWorld(new LevelFive());
                        break;
                    case "Lava":
                        world.setTempWorld(new LevelSix());
                        break;
                }
                world.getTempWorld().setView(world.getView());
            }
        } else if (e.getActionCommand().equals("Restart")) {
            lines.clear();
            game.setLevelNum(7);
            game.loadLevel();
        } else if (e.getActionCommand().equals("Help")) {
            JOptionPane.showMessageDialog(Game.frame, "Press Middle-Click to create the object currently selected.\n" +
                    "Press Right-Click while the mouse is over an object to delete said object.\n" +
                    "Press any mouse button while over an object to drag and move it around the space.", "Controls", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getActionCommand().equals("comboBoxChanged")) {
            createItem1 = (String) objectComboBox.getSelectedItem();
            createItem2 = (String) backgroundComboBox.getSelectedItem();
            assert createItem2 != null;
            switch (createItem2) {
                case "Mars Surface":
                    world.setTempWorld(new LevelOne());
                    break;
                case "Industrial":
                    world.setTempWorld(new LevelTwo());
                    break;
                case "Mine":
                    world.setTempWorld(new LevelThree());
                    break;
                case "Dungeon":
                    world.setTempWorld(new LevelFour());
                    break;
                case "Crystal":
                    world.setTempWorld(new LevelFive());
                    break;
                case "Lava":
                    world.setTempWorld(new LevelSix());
                    break;
            }
            world.getTempWorld().setView(world.getView());
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == enableGridCheckBox) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                world.getView().setGridResolution(1);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                world.getView().setGridResolution(0);
            }
        } else if (e.getItem() == enableAsteroidsCheckBox) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                world.createAsteroids();
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                world.destroyAsteroids();
            }
        } else if (e.getItem() == enableOutline) {
            if (enableOutline.isSelected()) {
                isOutlineEnabled = true;
            } else {
                isOutlineEnabled = false;
            }
            for (StaticBody staticBody : world.getStaticBodies()) {
                staticBody.setAlwaysOutline(e.getStateChange() == ItemEvent.SELECTED);
            }
        } else if (e.getItem() == cbMenuItem) {
            boolean isSelected = true;
            if (e.getStateChange() == ItemEvent.SELECTED) {
                isSelected = true;
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                isSelected = false;
            }
            enableGridCheckBox.setVisible(isSelected);
            enableAsteroidsCheckBox.setVisible(isSelected);
            enableOutline.setVisible(isSelected);
            objectSelectionText.setVisible(isSelected);
            objectComboBox.setVisible(isSelected);
            bgSelectionText.setVisible(isSelected);
            backgroundComboBox.setVisible(isSelected);
        }
    }
}
