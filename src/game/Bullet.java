package game;

import city.cs.engine.DynamicBody;
import city.cs.engine.Shape;
import city.cs.engine.World;

public class Bullet extends DynamicBody {

    private int damage;

    Bullet(World world, Shape shape, int damage) {
        super(world, shape);
        this.damage = damage;
        this.setBullet(true);
    }

    public int getDamage() { return this.damage; }

    public void setDamage(int damage) { this.damage = damage; }
}
