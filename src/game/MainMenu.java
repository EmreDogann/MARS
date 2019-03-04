package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends Container {
    private JPanel mainPanel;
    private JLabel MARS;
    private JButton playButton;
    private JButton levelEditorButton;
    private JButton quitButton;

    MainMenu() {
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        mainPanel.setBounds(BackgroundPanel.WIDTH/2-50, BackgroundPanel.HEIGHT/2-75, 100, 150);
        playButton.addActionListener(e -> {
            Game.levelNum = 1;
            Game.loadLevel();
        });
        levelEditorButton.addActionListener(e -> {
            Game.levelNum = 6;
            Game.loadLevel();
        });
        quitButton.addActionListener(e -> System.exit(0));
    }

    JPanel getMainPanel() { return mainPanel; }

    public JLabel getMARS() {
        return MARS;
    }

    public JButton getPlayButton() {
        return playButton;
    }

    public JButton getLevelEditorButton() {
        return levelEditorButton;
    }

    public JButton getQuitButton() {
        return quitButton;
    }
}
