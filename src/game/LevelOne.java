package game;

import city.cs.engine.*;
import city.cs.engine.Shape;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class LevelOne extends SuperLevel {
    private DynamicBody spaceship;
    private Timer timer1;
    private Game game;

    LevelOne() { readFolder("data/Backgrounds/Mars Surface/"); }

    LevelOne(ArrayList<String> availableLevels, Game game) {
        super(STATE.GAME, availableLevels, game);
        this.game = game;
        setupLevel1();
    }

    private void setupLevel1() {
        timer1 = new Timer(0000, spawnSpaceship);
        addTimer(timer1);
        timer1.start();
        readFolder("data/Backgrounds/Mars Surface/");
        game.setCurrentLevel("Level 1");
        this.generateMap("data/Levels/Level 1.txt");
    }

    @Override
    @SuppressWarnings("Duplicates")
    void generateBackground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        BackgroundPanel view = getView();
        g.drawImage(iconHashMap.get("Background").getImage(), 0, 0, (int) (width * 1.05f), height, view);
        view.drawImage(g, iconHashMap.get("greenPlanet"), (int) (575 + x * -0.2f), 150, 0.25f);
        view.drawImage(g, iconHashMap.get("yellowPlanet"), (int) (400 + x * -0.1f), 200, 1);
        view.drawImage(g, iconHashMap.get("redPlanet"), (int) (500 + x * -0.3f), 250, 0.85f);
        view.drawImage(g, iconHashMap.get("orangePlanet"), (int) (300 + x * -0.3f), 300, 0.5f);
        view.drawImage(g, iconHashMap.get("bluePlanet"), (int) (550 + x * -0.35f), 265, 0.35f);

        view.drawImage(g, iconHashMap.get("stars"), (int) (x * -0.05f), 0, 3f);
        view.drawImage(g, iconHashMap.get("farPlanets"), (int) (x * -0.25f), 0, 3f);
        view.drawImage(g, iconHashMap.get("ringPlanet"), (int) (x * -0.5f), 0, 3f);
        view.drawImage(g, iconHashMap.get("bigPlanet"), (int) (x * -0.75f), 0, 2f);

        view.backgroundTiling(g, iconHashMap.get("greyMountain"), -400 + x * -4 + (rand.nextInt(xBound) - xBound / 2f), height - 245 + (rand.nextInt(yBound) - yBound / 2), 2.5f);
        view.backgroundTiling(g, iconHashMap.get("pinkMountain"), 350 - x * 6 + (rand.nextInt(xBound) - xBound / 2f), height - 190 + (rand.nextInt(yBound) - yBound / 2), 2.25f);
        view.backgroundTiling(g, iconHashMap.get("orangeMountain"), -x * 11 + (rand.nextInt(xBound) - xBound / 2f), height - 160 + (rand.nextInt(yBound) - yBound / 2), 2);
    }

    @Override
    void generateForeground(Graphics2D g, float x, int width, int height, Random rand, int xBound, int yBound) {
        getView().backgroundTiling(g, iconHashMap.get("ground1"), -x * 20 + (rand.nextInt(xBound) - xBound / 2f), height - 45 + (rand.nextInt(yBound) - yBound / 2), 1.5f);
    }

    public Vec2 startPosition() { return new Vec2(-100, -2); }

    /*Will be called after the spaceship collides with the ground.*/
    private ActionListener spawnPlayer = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            BackgroundPanel view = getView();
            MouseHandler mouseHandler = getMouseHandler();

            getWorld().addStepListener(getStepHandler());
            view.addMouseListener(mouseHandler);
            view.stopShaking();
            view.setState(STATE.GAME);

            getPlayer().setPosition(new Vec2(spaceship.getPosition().x + 2, spaceship.getPosition().y));
            getPlayer().applyForce(new Vec2(17000, 0));
            getPlayer().changeImages("Idle.png");
            getPlayer().addCollisionListener(new CollisionHandler(game));

            PolygonShape polygonShape = new PolygonShape(-5.43f,-2.05f, -2.9f,3.08f, 2.71f,-2.05f, -5.43f,-2.05f);
            StaticBody spaceshipStatic = new StaticBody(getWorld(), polygonShape);
            spaceshipStatic.setPosition(new Vec2(spaceship.getPosition().x, spaceship.getPosition().y));
            spaceshipStatic.setName("Spaceship");
            spaceship.destroy();

            new AttachedImage(spaceshipStatic, new BodyImage("data/spaceshipDestroyedV3.gif", 12), 1, 0, new Vec2(0, 3.75f));
            new AttachedImage(spaceshipStatic, new BodyImage("data/giphy1.gif", 20), 1.5f, 0, new Vec2(-2, 10));

            lookAtCursor.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        }
    };

    private ActionListener spawnSpaceship = e -> {
        if (getState() == STATE.GAME) {
            Shape spaceshipShape = new BoxShape(2, 2);
            spaceship = new DynamicBody(getWorld(), spaceshipShape);
            new AttachedImage(spaceship, new BodyImage("data/playerSpaceship.png", 8), 1.5f, 0, new Vec2(0, 0));
            spaceship.setPosition(new Vec2(-145, 40));
            spaceship.applyForce(new Vec2(1000000, 200000));
            spaceship.setGravityScale(15);
            spaceship.setName("PlayerSpaceship");
            spaceship.addCollisionListener(new CollisionHandler(game, spawnPlayer));
            timer1.stop();
        } else if (getState() == STATE.MENU){
            timer1.stop();
        }
    };

}
