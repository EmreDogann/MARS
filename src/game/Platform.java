package game;

import city.cs.engine.Shape;
import city.cs.engine.StaticBody;

/**
 * Solid platforms (standard platforms, ground, or crumbing platforms) used in the game.
 */
public class Platform extends StaticBody {
    private String type;
    private String imageName;

    /**
     * Constructor for Platform.
     * @param world Current world.
     * @param shape The shape of the hit box of the platform.
     * @param type The type of platform it is (platform, crumbling platform, exit, ground).
     * @param imageName The name of the image file attached to the platform.
     */
    public Platform(SuperLevel world, Shape shape, String type, String imageName) {
        super(world, shape);
        this.type = type;
        this.imageName = imageName;
    }

    /**
     * @return the type of platform.
     */
    public String getType() { return type; }

    /**
     * @return the name of the image file.
     */
    public String getImageName() {
        return imageName;
    }
}
