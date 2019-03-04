package game;

import city.cs.engine.BodyImage;
import city.cs.engine.DynamicBody;
import city.cs.engine.Shape;
import city.cs.engine.World;

public class Enemy extends DynamicBody {

    private int damage;

    public Enemy(World world, Shape shape, int damage) {
        super(world, shape);
        this.damage = damage;

        this.addImage(new BodyImage("data/enemy.png", 1.5f));
        this.setName("Enemy");
        this.addCollisionListener(new CollisionHandler());
    }

    int getDamage() { return this.damage; }
}