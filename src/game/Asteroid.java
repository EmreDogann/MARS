package game;

import city.cs.engine.BodyImage;
import city.cs.engine.CircleShape;
import city.cs.engine.DynamicBody;
import org.jbox2d.common.Vec2;

import java.util.Random;

class Asteroid extends DynamicBody{

    private int damage;
    private Random rand = new Random();
    private MainCharacter astronaut;

    Asteroid(CircleShape shape, int damage, MainCharacter astronaut) {
        super(Game.getWorld(), shape);
        this.damage = damage;
        this.astronaut = astronaut;

        this.addCollisionListener(new CollisionHandler());
    }

    int getDamage() { return this.damage; }

    /*Whenever an asteroid collides with another game object, the collision handler will call this method respawn
    the asteroid back at the top of the view.*/
    void spawn() {
        this.removeAllImages();
        this.setPosition(new Vec2(astronaut.getPosition().x + (rand.nextInt(20)-10), 15));
        this.setLinearVelocity(new Vec2(rand.nextInt(50) - 25, -rand.nextInt(5) - 10));
        this.addImage(new BodyImage("data/Asteroid/asteroid" + (rand.nextInt(3) + 1) + ".png"));
    }
}
