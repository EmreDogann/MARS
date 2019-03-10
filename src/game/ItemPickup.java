package game;

import city.cs.engine.*;

class ItemPickup extends StaticBody {
    private int restoreAmount = 0;
    private String type;

    ItemPickup(SuperLevel world, Shape shape, String type, Game game) {
        super(world);
        Sensor sensor = new Sensor(this, shape);
        sensor.addSensorListener(new CollisionHandler(game));
        this.type = type;
        switch (this.type) {
            case "Health":
                this.restoreAmount = 30;
                break;
            case "Shield":
                this.restoreAmount = 10;
                break;
            case "Ammo":
                this.restoreAmount = 8;
                break;
            case "Loot Bag":
                this.restoreAmount = 25;
                break;
            case "Loot Chest":
                this.restoreAmount = 60;
                break;
            case "Loot Coins":
                this.restoreAmount = 25;
                break;
            case "Loot Crystal":
                this.restoreAmount = 75;
                break;
            case "Loot Goblet":
                this.restoreAmount = 60;
                break;
            case "Loot Treasure Sack":
                this.restoreAmount = 45;
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
