package game;

import city.cs.engine.BodyImage;
import city.cs.engine.CircleShape;
import city.cs.engine.DynamicBody;
import org.jbox2d.common.Vec2;

import java.util.Random;

/**
 * Asteroids will be used to create an asteroid and store information such as damage.
 * @author Emre, Dogan, emre.dogan@city.ac.uk
 */
public class Asteroid extends DynamicBody{
    private int damage;
    private Random rand = new Random();
    private MainCharacter player;

    /**
     * Constructor for the Asteroid class.
     * @param world The instance of the current world.
     * @param shape A instance of type CircleShape which is used along with the world parameter to create an instance of the DynamicBody class.
     * @param damage Defines how much damage an asteroid will deal when colliding with another object that can take damage.
     * @param player The instance of the player used to define the x position at which the asteroids respawn.
     * @param game The instance of the game which is passed into the CollisionHandler constructor when adding a new collision listener to this asteroid.
     */
    public Asteroid(SuperLevel world, CircleShape shape, int damage, MainCharacter player, Game game) {
        super(world, shape);
        this.damage = damage;
        this.player = player;

        this.addCollisionListener(new CollisionHandler(game));
    }

    /**
     * Gets the damage value of the asteroid.
     * @return the amount of damage this asteroid deals.
     */
    public int getDamage() { return this.damage; }

    /**
     * Whenever an asteroid collides with another game object, the collision handler will call this method respawn the asteroid back at the top of the view. At the x position of the player.
     */
    public void spawn() {
        this.removeAllImages();
        this.setPosition(new Vec2(player.getPosition().x + (rand.nextInt(20)-10), 15));
        this.setLinearVelocity(new Vec2(rand.nextInt(50) - 25, -rand.nextInt(5) - 10));
        this.addImage(new BodyImage("data/Asteroid/asteroid" + (rand.nextInt(3) + 1) + ".png"));
    }
}
