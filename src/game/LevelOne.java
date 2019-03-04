package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static game.Game.getWorld;

public class LevelOne extends SuperLevel {
    private DynamicBody spaceship;
    private Timer timer1;

    LevelOne(ArrayList<String> availableLevels) {
        super(STATE.GAME, availableLevels);
        timer1 = new Timer(3000, spawnSpaceship);
        addTimer(timer1);
        timer1.start();
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
            getPlayer().addCollisionListener(new CollisionHandler());

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
            spaceship.addCollisionListener(new CollisionHandler(spawnPlayer));
            timer1.stop();
        } else if (getState() == STATE.MENU){
            timer1.stop();
        }
    };

}
