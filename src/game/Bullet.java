package game;

import city.cs.engine.DynamicBody;
import city.cs.engine.Shape;
import city.cs.engine.World;

/**
 * Responsible for everything related to bullets. Stores damage value.
 */
public class Bullet extends DynamicBody {

    private int damage;

    /**
     * Constructor for the Bullet class.
     * @param world A copy of the current world.
     * @param shape A shape which defines the hit box of the bullet. This is used along with world to call the constructor of DynamicBody.
     * @param damage Number which defines the amount of damage the bullet will do.
     */
    public Bullet(World world, Shape shape, int damage) {
        super(world, shape);
        this.damage = damage;
        this.setBullet(true);
    }

    /**
     * @return the damage dealt by the bullet as an int.
     */
    public int getDamage() { return this.damage; }

    /**
     * Updates the damage value of the bullet with that of the damage parameter.
     * @param damage The new amount of damage that the bullet should do.
     */
    public void setDamage(int damage) { this.damage = damage; }
}
