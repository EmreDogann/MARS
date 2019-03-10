package game;

import city.cs.engine.*;

public class Enemy extends Walker {
    private int health = 100;
    private int damage;

    public Enemy(SuperLevel world, Shape shape, int damage, Game game) {
        super(world, shape);
        this.damage = damage;

        this.addImage(new BodyImage("data/enemy.png", 1.5f));
        this.setName("Enemy");
        this.addCollisionListener(new CollisionHandler(game));
    }

    int getDamage() { return this.damage; }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}