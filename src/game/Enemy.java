package game;

import city.cs.engine.*;

/**
 * Creates enemies in the game and stores the necessary information about them (health, damage).
 */
public class Enemy extends Walker {
    private int health = 100;
    private int damage;

    /**
     * Constructor for Enemy.
     * @param world Copy of the current world.
     * @param shape Shape which defines the hit box of the enemy, passed in with world when calling the super constructor of class Walker.
     * @param damage Number which defines how much damage the enemy will do when the player collides with it.
     * @param game Instance Game.
     */
    public Enemy(SuperLevel world, Shape shape, int damage, Game game) {
        super(world, shape);
        this.damage = damage;

        this.addImage(new BodyImage("data/enemy.png", 1.5f));
        this.setName("Enemy");
        this.addCollisionListener(new CollisionHandler(game));
    }

    /**
     * @return the damage done by the enemy to the player as an int.
     */
    public int getDamage() { return this.damage; }

    /**
     * @return the current health of the enemy as an int.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Updates the health of the enemy with the new health specified with the parameter 'health'.
     * @param health The new health of the enemy.
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Updates the damage dealt by the enemy with the new damage specified with the damage parameter.
     * @param damage The damage to be dealt by the enemy.
     */
    public void setDamage(int damage) { this.damage = damage; }
}