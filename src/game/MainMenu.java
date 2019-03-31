package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles with the UI controls in the main menu.
 */
public class MainMenu extends Container {
    private JPanel mainPanel;
    private JButton playButton;
    private JButton levelEditorButton;
    private JButton quitButton;

    /**
     * Constructor for MainMenu.
     * @param game Instance of Game.
     */
    public MainMenu(Game game) {
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        mainPanel.setBounds(BackgroundPanel.WIDTH/2-50, BackgroundPanel.HEIGHT/2-75, 100, 150);
        playButton.addActionListener(e -> { //Start the game from level 1...
            game.setLevelNum(1);
            game.loadLevel();
        });
        levelEditorButton.addActionListener(e -> { //Start the level editor...
            game.setLevelNum(7);
            game.loadLevel();
        });
        //Exit the game...
        quitButton.addActionListener(e -> System.exit(0));
    }

    /**
     * @return the JPanel attached to the UI.
     */
    public JPanel getMainPanel() { return mainPanel; }
}
