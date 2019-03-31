package game;

import city.cs.engine.DynamicBody;
import city.cs.engine.StaticBody;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles with the UI controls in the top-left corner while in the game.
 */
public class UIControls implements ActionListener {
    private JMenuBar menuBar;
    private boolean shaking = false;
    private SuperLevel world;
    private Game game;

    /**
     * Constructor for UIControls.
     * @param world1 The current instance of the world.
     * @param game Instance of Game.
     */
    public UIControls(SuperLevel world1, Game game) {
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
        String[] items = {"Main Menu", "Pause", "Save Game", "Load Save", "Load Level", "Restart Level", "Help"};
        for (String item : items) {
            menuItem = new JMenuItem(item);
            menu.add(menuItem);
            menuItem.addActionListener(this);
            menuItem.setBorderPainted(false);
        }

        world.getView().add(menuBar);
    }

    /**
     * Called whenever the user clicks on a UI element.
     * @param e Stores the information about the current action event (i.e. UI button pressed).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Main Menu": //Go to the main menu.
                world.stop();
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

                if (n == 0) { //If the player decided the save their game before exiting to the main menu.
                    saveGame();
                }

                if (n == 0 || n == 1) { //If the player decided not to save.
                    Game.frame.remove(menuBar);
                    world.getView().setGridResolution(0);
                    if (world.getView().getxBound() != 1) {
                        world.getView().stopShaking();
                    }
                    world.stopTimers();
                    game.setLevelNum(0);
                    game.loadLevel();
                } else {
                    world.start();
                }
                break;
            case "Pause": { //Pause the game.
                JMenuItem item = (JMenuItem) e.getSource();
                if (world.getView().getxBound() > 1) {
                    shaking = true;
                }
                world.getView().stopShaking();
                world.stop();
                item.setText("Resume");
                break;
            }
            case "Resume": { //Resume the game (if paused).
                JMenuItem item = (JMenuItem) e.getSource();
                if (shaking) {
                    world.getView().startShaking(0);
                }
                world.start();
                item.setText("Pause");
                break;
            }
            case "Save Game": //Save the game.
                saveGame();
                break;
            case "Load Save": //Load a save game.
                Object[] saves = world.getAvailableSaves().toArray();
                String result = (String) JOptionPane.showInputDialog(
                        Game.frame,
                        "Please select a save to load...",
                        "Load Save...",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        saves, null);

                if (result != null) {
                    String line;
                    BufferedReader reader = null;
                    String[] text;
                    String background = "";

                    //Finds the save file and loads it.
                    try {
                        reader = new BufferedReader(new FileReader("data/Saves/" + result + ".txt"));
                        while ((line = reader.readLine()) != null) {
                            if (line.substring(0, 1).equals("@")) {
                                text = line.split("@");
                                game.setCurrentLevel(text[1]);
                                if (text[1].startsWith("Level")) {
                                    game.setLevelNum(Integer.parseInt(text[1].substring(6,7)));
                                    game.setSaveLoaded(true);
                                    game.getWorld().stop();
                                    game.getWorld().getView().removeAll();
                                    game.getWorld().removeMouseHandler();
                                    switch(game.getLevelNum()) {
                                        case 1:
                                            game.setWorld(new LevelOne(world.getAvailableLevels(), game, world.getAvailableSaves(), 1));
                                            break;
                                        case 2:
                                            game.setWorld(new LevelTwo(world.getAvailableLevels(), game, world.getAvailableSaves(), 1));
                                            break;
                                        case 3:
                                            game.setWorld(new LevelThree(world.getAvailableLevels(), game, world.getAvailableSaves(), 1));
                                            break;
                                        case 4:
                                            game.setWorld(new LevelFour(world.getAvailableLevels(), game, world.getAvailableSaves(), 1));
                                            break;
                                        case 5:
                                            game.setWorld(new LevelFive(world.getAvailableLevels(), game, world.getAvailableSaves(), 1));
                                            break;
                                        case 6:
                                            game.setWorld(new LevelSix(world.getAvailableLevels(), game, world.getAvailableSaves(), 1));
                                            break;
                                    }
                                    game.setupLevel(game.getDefaultPlayerStats());
                                } else {
                                    game.setLevelNum(-1);
                                }
                            }
                        }
                    } catch (IOException err) {
                        err.printStackTrace();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException err) {
                                err.printStackTrace();
                            }
                        }
                    }
                    game.getWorld().getView().stopShaking();
                    game.getWorld().stopTimers();
                    if (game.getLevelNum() == -1) {
                        game.loadLevel();
                    }
                    game.getWorld().generateMap("data/Saves/" + result + ".txt");
                    game.setSaveLoaded(false);
                }
                break;
            case "Load Level": //Load a level.
                Object[] possibilities = world.getAvailableLevels().toArray();
                String level = (String) JOptionPane.showInputDialog(
                        Game.frame,
                        "Please select a level to load...",
                        "Load Level...",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        possibilities, null);

                if (level != null) {
                    if (level.length() >= 5) {
                        if (level.substring(0, 5).equals("Level")) {
                            game.setLevelNum(Integer.parseInt(level.substring(6)));
                        } else {
                            game.setLevelNum(-1);
                            game.setCurrentLevel(level);
                        }
                    } else {
                        game.setLevelNum(-1);
                        game.setCurrentLevel(level);
                    }
                    world.getView().stopShaking();
                    world.stopTimers();
                    world.getPlayer().setStats(game.getDefaultPlayerStats());
                    game.loadLevel();
                }
                break;
            case "Restart Level": //Restart the current level.
                world.stopTimers();
                world.getView().stopShaking();
                world.getPlayer().setStats(game.getDefaultPlayerStats());
                game.loadLevel();
                break;
            case "Help": //Show the controls screen.
                JOptionPane.showMessageDialog(Game.frame, "Press Mouse 1 to fire.\n" +
                        "Use A to move left, and use D to move right.\n" +
                        "Press SPACE to jump.\n" +
                        "Use the mouse to aim your weapon.", "Controls", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    //Save the game by writing all the game states (player info, platforms, items picked up, etc.) to a file.
    private void saveGame() {
        if (game.getCurrentLevel() != null) {
            world.stop();
            String result = (String) JOptionPane.showInputDialog(
                    Game.frame,
                    "Please enter a name for your save...",
                    "Save Game...",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null, null);

            if (result != null) {
                File[] files = new File("data/Saves").listFiles();
                ArrayList<String> saves = new ArrayList<>();
                assert files != null;
                for (File file : files) {
                    saves.add(file.getName().split("\\.")[0]);
                }
                if (!saves.contains(result)) {
                    BufferedWriter writer = null;

                    //Write all the static and dynamic body states to a file.
                    try {
                        writer = new BufferedWriter(new FileWriter("data/Saves/" + result + ".txt"));
                        List<StaticBody> staticBodyList = world.getStaticBodies();
                        List<DynamicBody> dynamicBodyList = world.getDynamicBodies();

                        writer.write("@" + game.getCurrentLevel() + "\n");

                        for (StaticBody staticBody : staticBodyList) {
                            if (staticBody instanceof Platform) {
                                Platform platform = (Platform) staticBody;
                                if (!platform.getType().equals("exit")) {
                                    writer.write(platform.getImageName() + "," + platform.getPosition().x + "," + platform.getPosition().y + ",1.60,0.1" + "\n");
                                } else {
                                    writer.write(platform.getImageName() + "," + platform.getPosition().x + "," + platform.getPosition().y + ",1.30,0.60" + "\n");
                                }
                            } else if (staticBody instanceof ItemPickup) {
                                ItemPickup itemPickup = (ItemPickup) staticBody;
                                writer.write("pickup" + itemPickup.getType() + "," + itemPickup.getPosition().x + "," + itemPickup.getPosition().y + ",0.50,0.50," + "\n");
                            }
                        }

                        for (DynamicBody dynamicBody : dynamicBodyList) {
                            if (dynamicBody instanceof MainCharacter) {
                                MainCharacter player = (MainCharacter) dynamicBody;
                                writer.write("player," + player.getPosition().x + "," + player.getPosition().y + "," + player.getHealth() + "," + player.getArmour() + "," + player.getAmmo() + "," + player.getExtraJumpsLimit() + "," + player.isAcquiredBoots() + "," + player.isAcquiredPistol() + "\n");
                            } else if (dynamicBody instanceof Enemy) {
                                Enemy enemy = (Enemy) dynamicBody;
                                if (world.enemyPath.get(enemy) != null) {
                                    writer.write("enemy," + enemy.getPosition().x + "," + enemy.getPosition().y + "," + enemy.getHealth() + "," + enemy.getDamage() + "," + world.enemyPath.get(enemy)[0] + "," + world.enemyPath.get(enemy)[1] + "\n");
                                } else {
                                    writer.write("enemy," + enemy.getPosition().x + "," + enemy.getPosition().y + "," + enemy.getHealth() + "," + enemy.getDamage() + ", , " + "\n");
                                }
                            }
                        }

                        writer.write("score," + game.getScore().getPrevScore() + "," + game.getScore().getHighScore() + "," + game.getScore().getCurrScore() + "," + game.getScore().getCurrTime() + "\n");
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
                    world.addAvailableSaves(result);
                } else {
                    JOptionPane.showMessageDialog(Game.frame,
                            "ERROR: There is already a save with this name!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            world.start();
        }
    }
}
