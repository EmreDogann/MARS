package game;

import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Menu extends SuperLevel {

    Menu(ArrayList<String> levels) {
        super(STATE.MENU, levels);
    }

    public void startMainMenu(JFrame frame1) {
        //Set layout manager - decides how to arrange the components on the frame.
        frame1.setLayout(new BorderLayout());

        //Create Swing components.
        JTextArea textArea = new JTextArea();
        JButton button = new JButton("Click me!");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales

        //Add swing components to content pane.
        Container c = frame1.getContentPane();
        c.add(textArea, BorderLayout.CENTER);
        c.add(button, BorderLayout.NORTH);

        //Add behaviour

    }

    @Override
    public Vec2 startPosition() {
        return new Vec2(0, 0);
    }

//    int width, height;
//
//    JButton play = new JButton("play");
//    JButton settings = new JButton("settings");
//    JButton exit = new JButton("exit");
//    JButton mainMenu = new JButton("main menu");
//
//    CardLayout layout = new CardLayout();
//
//    JPanel panel = new JPanel();
//    JPanel game = new JPanel();
//    JPanel menu = new JPanel();
//
//    public Menu(int width, int height, String title) {
//        super(title);
//        this.width = width;
//        this.height = height;
//
//        panel.setLayout(layout);
//        addButtons();
//
//        setSize(width, height);
//        setResizable(false);
//        setLocationRelativeTo(null);
//        setVisible(true);
//        setTitle("BUILD YOUR EMPIRE");
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        requestFocus();
//
//    }
//
//    private void addButtons() {
//
//        play.addActionListener(this);
//        settings.addActionListener(this);
//        exit.addActionListener(this);
//        mainMenu.addActionListener(this);
//
//        //menu buttons
//        menu.add(play);
//        menu.add(settings);
//        menu.add(exit);
//
//        //game buttons
//        game.add(mainMenu);
//
//        //background colors
//        game.setBackground(Color.MAGENTA);
//        menu.setBackground(Color.GREEN);
//
//        //adding children to parent Panel
//        panel.add(menu,"Menu");
//        panel.add(game,"Game");
//
//        add(panel);
//        layout.show(panel,"Menu");
//
//    }
//
//    public void actionPerformed(ActionEvent event) {
//
//        Object source = event.getSource();
//
//        if (source == exit) {
//            System.exit(0);
//        } else if (source == play) {
//            layout.show(panel, "Game");
//        } else if (source == settings){
//
//        } else if (source == mainMenu){
//            layout.show(panel, "Menu");
//        }
//    }
}
