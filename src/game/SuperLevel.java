package game;

import city.cs.engine.*;
import city.cs.engine.Shape;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

import static game.Game.frame;
import static game.Game.getWorld;
import static game.MouseHandler.lines;

public abstract class SuperLevel extends World {
    private MainCharacter player;
    private ArrayList<Asteroid> asteroid = new ArrayList<>();
    private BackgroundPanel view;
    private MouseHandler mouseHandler;
    private StepHandler stepHandler;
    private ArrayList<Timer> timerList = new ArrayList<>();
    private ArrayList<String> availableLevels;

    private STATE State;

    SuperLevel(STATE State, ArrayList<String> levels) {
        super(60);
        this.State = State;
        this.availableLevels = levels;
    }

    void populate() {
        if (State == STATE.GAME || State == STATE.LEVEL_EDITOR) {
            Shape astronautShape = new BoxShape(0.3f, 1.1f);
            player = new MainCharacter(astronautShape, 100, 25, 10);
            player.setPosition(startPosition());

            Shape shape = new BoxShape(500, 0.5f);
            Platform ground = new Platform(getWorld(), shape, "Ground");
            /*Makes ground invisible.*/
            ground.setLineColor(new Color(0, 0, 0, 0));
            ground.setFillColor(new Color(0, 0, 0, 0));
            ground.setPosition(new Vec2(0, -11.5f));
            ground.setName("Ground");

            if (Game.levelNum == 1) {
                createAsteroids();
                getView().setCentre(new Vec2(-30, 0));
                getView().startShaking();
            }

            mouseHandler = new MouseHandler(view, player);
            getView().addMouseMotionListener(mouseHandler);

            stepHandler = new StepHandler(view, player);
            if (State == STATE.LEVEL_EDITOR || Game.levelNum > 1 || Game.levelNum == -1) {
                getWorld().addStepListener(stepHandler);
                player.addCollisionListener(new CollisionHandler());
                view.addMouseListener(mouseHandler);
            }

            frame.addKeyListener(new KeyHandler(player, getWorld().lookAtCursor, stepHandler, 6, getWorld()));
        } else if (State == STATE.MENU) {
            //getView().setLayout(new OverlayLayout(getView()));
            getView().setLayout(null);
            MainMenu menu = new MainMenu();
            getView().add(menu.getMainPanel(), BorderLayout.NORTH);
        }

        //JFrame debugView = new DebugViewer(getWorld(), 2000, 2000);
    }

    /*Will be called to rotate arm to look at the mouse cursor.*/
    ActionListener lookAtCursor = e -> {
        try {
            mouseHandler.calculateTheta(view.getMousePosition());
        } catch (NullPointerException ignored) {
        }
    };

    void createAsteroids() {
        for (int i = 0; i < 4; i++) {
            asteroid.add(new Asteroid(new CircleShape(0.38f), 40, player));
            asteroid.get(i).spawn();
        }
    }

    void destroyAsteroids() {
        for (int i = 0; i < 4; i++) {
            asteroid.get(i).destroy();
        }
        asteroid.removeAll(asteroid);
    }

    void removeMouseHandler() {
        this.getView().removeMouseListener(mouseHandler);
        this.getView().removeMouseMotionListener(mouseHandler);
    }

    ArrayList<String> getAvailableLevels() {
        return availableLevels;
    }

    void addAvailableLevels(String availableLevels) {
        this.availableLevels.add(availableLevels);
    }

    MainCharacter getPlayer() {
        return player;
    }

    public abstract Vec2 startPosition();

    STATE getState() {
        return State;
    }

    public void setState(STATE state) {
        State = state;
    }

    BackgroundPanel getView() {
        return view;
    }

    void setView(BackgroundPanel newView) {
        this.view = newView;
    }

    MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    StepHandler getStepHandler() {
        return stepHandler;
    }

    void addTimer(Timer timer) {
        timerList.add(timer);
    }

    void stopTimers() {
        for (Timer timer : timerList) {
            timer.stop();
        }
    }

    void generateMap(String levelPath) {
        String line;
        String cvsSplitBy = ",";
        BufferedReader reader = null;
        String[] text;

        try {
            reader = new BufferedReader(new FileReader(levelPath));
            while ((line = reader.readLine()) != null) {
                text = line.split(cvsSplitBy);
                lines.add(line);

                //System.out.println("Type: " + text[0] + ", x: " + text[1] + ", y: " + text[2] + ", width: " + text[3] + ", height: " + text[4]);
                if (text[0].contains("crumblingPlatform")) {
                    createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/" + text[0] + ".png", 3, "crumblingPlatform");
                } else if (text[0].equals("exit")) {
                    createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/" + text[0] + ".png", 3, "exit");
                } else if (text[0].contains("pickup")) {
                    createItemPickup(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/ItemPickup/" + text[0].substring(6) + ".png", text[0].substring(6));
                } else if (text[0].equals("enemy")) {
                    createEnemy(Float.parseFloat(text[1]), Float.parseFloat(text[2]), "data/" + text[0] + ".png", 1.65f);
                } else {
                    createPlatform(Float.parseFloat(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]), Float.parseFloat(text[4]), "data/Platform/" + text[0] + ".png", 15, "platform");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createPlatform(float x, float y, float width, float height, String imagePath, int scale, String type) {
        Shape shape1 = new BoxShape(width, height);
        Platform platform = new Platform(getWorld(), shape1, type);
        platform.addImage(new BodyImage(imagePath, scale)).setOffset(new Vec2(0, 0.12f));
        platform.setPosition(new Vec2(x, y));
        platform.setName("Body" + lines.size());
    }

    @SuppressWarnings("Duplicates")
    private void createEnemy(float x, float y, String imagePath, float scale) {
        if (State == STATE.GAME) {
            PolygonShape shape1 = new PolygonShape(-1.186f, -0.816f, -1.186f, -0.1f, -0.251f, 0.825f, 0.251f, 0.825f, 1.181f, -0.105f, 1.186f, -0.821f, -1.186f, -0.821f);
            Enemy enemy = new Enemy(getWorld(), shape1, 15);
            enemy.addImage(new BodyImage(imagePath, scale));
            enemy.setPosition(new Vec2(x, y));
            enemy.setName("Body" + lines.size());
        } else if (State == STATE.LEVEL_EDITOR) {
            PolygonShape shape1 = new PolygonShape(-1.186f, -0.816f, -1.186f, -0.1f, -0.251f, 0.825f, 0.251f, 0.825f, 1.181f, -0.105f, 1.186f, -0.821f, -1.186f, -0.821f);
            StaticBody enemy = new StaticBody(getWorld(), shape1);
            enemy.addImage(new BodyImage(imagePath, scale));
            enemy.setPosition(new Vec2(x, y));
            enemy.setName("Body" + lines.size());
        }
    }

    private void createItemPickup(float x, float y, float width, float height, String imagePath, String type) {
        Shape shape1 = new BoxShape(width, height);
        ItemPickup item = new ItemPickup(getWorld(), shape1, type);
        item.addImage(new BodyImage(imagePath));
        item.setPosition(new Vec2(x, y));
        item.setName("Body" + lines.size());
        item.setAlwaysOutline(true);
    }
}
