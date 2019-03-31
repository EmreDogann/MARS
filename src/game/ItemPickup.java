package game;

import city.cs.engine.*;

/**
 * The items that are able to be picked up by the player such as health, armour, ammo, gold, etc.
 */
public class ItemPickup extends StaticBody {
    private int itemValue = 0;
    private String type;

    /**
     * Constructor for ItemPickup. Sets up all the item values and attaches a sensor to the item.
     * @param world Current world.
     * @param shape Shape which defines the hit box of sensor for the item.
     * @param type The type of item it is (health, gold, dash boots, etc.)
     * @param game Instance of Game.
     */
    public ItemPickup(SuperLevel world, Shape shape, String type, Game game) {
        super(world);
        //Add a sensor to the item which is handled in CollisionHandler.
        Sensor sensor = new Sensor(this, shape);
        sensor.addSensorListener(new CollisionHandler(game));
        this.type = type;
        //Set the value of the item depending on the item type.
        switch (this.type) {
            case "Health":
                this.itemValue = 30;
                break;
            case "Shield":
                this.itemValue = 10;
                break;
            case "Ammo":
                this.itemValue = 8;
                break;
            case "Loot Bag":
                this.itemValue = 25;
                break;
            case "Loot Chest":
                this.itemValue = 60;
                break;
            case "Loot Coins":
                this.itemValue = 25;
                break;
            case "Loot Crystal":
                this.itemValue = 75;
                break;
            case "Loot Goblet":
                this.itemValue = 60;
                break;
            case "Loot Treasure Sack":
                this.itemValue = 45;
                break;
        }
    }

    /**
     * @return the type of the item as a string.
     */
    public String getType() {
        return this.type;
    }

    /**
     * @return the value of the item as an integer.
     */
    public int getItemValue() {
        return this.itemValue;
    }
}
