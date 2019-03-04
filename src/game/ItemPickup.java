package game;

import city.cs.engine.*;

class ItemPickup extends StaticBody {
    private int restoreAmount;
    private String type;
    private Sensor sensor;

    ItemPickup(World world, Shape shape, String type) {
        super(world);
        sensor = new Sensor(this, shape);
        sensor.addSensorListener(new CollisionHandler());
        this.type = type;
        switch (this.type) {
            case "Health":
                this.restoreAmount = 25;
                break;
            case "Shield":
                this.restoreAmount = 10;
                break;
            case "Ammo":
                this.restoreAmount = 8;
                break;
        }
    }

    String getType() {
        return this.type;
    }

    int getRestoreAmount() {
        return this.restoreAmount;
    }
}
