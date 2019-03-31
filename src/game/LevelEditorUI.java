package game;

import city.cs.engine.DynamicBody;
import city.cs.engine.StaticBody;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import static game.MouseHandler.lines;

/**
 * Handles with the UI controls in the top-left corner while in the level editor.
 */
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
    static boolean isLevelSaved = false;
    public static boolean isOutlineEnabled = false;

    /**
     * Constructor for LevelEditorUI.
     * @param world1 The current instance of the world.
     * @param game Instance of Game.
     */
    public LevelEditorUI(SuperLevel world1, Game game) {
        lines.clear();
        this.world = world1;
        this.game = game;
        world.getView().setGridResolution(0);
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
        String[] items = {"Main Menu", "Play", "Save Level", "Load Level", "Restart Editor", "Help"};
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

        world.getView().add(menuBar);

        //Add listeners to the checkboxes and drop-down menus.
        enableGridCheckBox.addItemListener(this);
        enableAsteroidsCheckBox.addItemListener(this);
        enableOutline.addItemListener(this);
        objectComboBox.addActionListener(this);
        backgroundComboBox.addActionListener(this);
        createItem1 = (String) objectComboBox.getSelectedItem();
        createItem2 = (String) backgroundComboBox.getSelectedItem();
        //Set the background of the level editor to the one selected in the backgrounds drop-down menu.
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

    /**
     * @return the JPanel attached to the UI.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Called whenever the user clicks on a level editor UI element.
     * @param e Stores the information about the current action event (i.e. UI button pressed).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Main Menu")) { //Load main menu...
            Object[] options = {"Yes, please",
                    "No, thanks",
                    "Cancel"};
            int n = JOptionPane.showOptionDialog(Game.frame,
                    "Loading the main menu will cause you to lose your level. Do you want to save your level?",
                    "WARNING",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[2]);

            if (n == 0) {
                saveLevel();
            }

            if (n == 0 || n == 1) {
                Game.frame.remove(menuBar);
                world.getView().setGridResolution(0);
                game.setLevelNum(0);
                lines.clear();
                game.loadLevel();
            }
        } else if (e.getActionCommand().equals("Play")) { //Play the current level (the user has to save first if they modified an existing level which they loaded or created a new one without saving it first).
            if (game.getCurrentLevel() != null || lines.size() != 0) {
                if (!isLevelSaved) {
                    saveLevel();
                }

                if (isLevelSaved) {
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
                        game.setLevelNum(-1);
                        game.loadLevel();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Game.frame, "ERROR: You cannot play an empty level!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Save Level")) { //Save the current level.
            saveLevel();
        } else if (e.getActionCommand().equals("Load Level")) { //Load a level...
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
                        if (result.startsWith("Level")) {
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

                //Set the background of the level editor to the background specified in the first line of the file.
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
                isLevelSaved = true;
            }
        } else if (e.getActionCommand().equals("Restart Editor")) { //Restart the level editor...
            lines.clear();
            game.setLevelNum(7);
            game.loadLevel();
        } else if (e.getActionCommand().equals("Help")) { //Show the controls screen.
            JOptionPane.showMessageDialog(Game.frame, "Press Middle-Click to create the object currently selected.\n" +
                    "Press Right-Click while the mouse is over an object to delete said object.\n" +
                    "Press any mouse button while over an object to drag and move it around the space.", "Controls", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getActionCommand().equals("comboBoxChanged")) { //Change the level editor background to the one it has been changed to.
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

    //Write all the objects from the static 'lines' variable imported from MouseHandler into a text file.
    private void saveLevel() {
        if (game.getCurrentLevel() != null || lines.size() != 0) {
            String result = (String) JOptionPane.showInputDialog(
                    Game.frame,
                    "Please enter a name for your level...",
                    "Save Level...",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null, null);

            if (result != null) {
                if (result.startsWith("Level")) {
                    JOptionPane.showMessageDialog(Game.frame,
                            "ERROR: Your name cannot being with \"Level\" as this is reserved for the main levels!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (!world.getAvailableLevels().contains(result)) {
                    world.addAvailableLevels(result);

                    BufferedWriter writer = null;

                    try {
                        writer = new BufferedWriter(new FileWriter("data/Levels/" + result + ".txt"));
                        //Write the background currently selected in the drop-down menu to the text file.
                        writer.write("#data/Backgrounds/" + createItem2 + "/\n");
                        //Write all the objects from 'lines' to the text file.
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
                    LevelEditorUI.isLevelSaved = true;
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
    }

    /**
     * Called whenever the user clicks on an option in one of the drop-down menus in the level editor.
     * @param e Stores the information about the current action event (i.e. drop-down option selected).
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == enableGridCheckBox) { //Enable/Disable the grid.
            if (e.getStateChange() == ItemEvent.SELECTED) {
                world.getView().setGridResolution(1);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                world.getView().setGridResolution(0);
            }
        } else if (e.getItem() == enableAsteroidsCheckBox) { //Enable/Disable asteroids.
            if (e.getStateChange() == ItemEvent.SELECTED) {
                world.createAsteroids();
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                world.destroyAsteroids();
            }
        } else if (e.getItem() == enableOutline) { //Enable/Disable outlines around the created objects (except items because they don't have a hit box but a sensor instead).
            if (enableOutline.isSelected()) {
                isOutlineEnabled = true;
            } else {
                isOutlineEnabled = false;
            }
            for (StaticBody staticBody : world.getStaticBodies()) {
                staticBody.setAlwaysOutline(e.getStateChange() == ItemEvent.SELECTED);
            }
        } else if (e.getItem() == cbMenuItem) { //Show/Hide the on-screen options.
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
