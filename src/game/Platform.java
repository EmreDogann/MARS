package game;

import city.cs.engine.BoxShape;
import city.cs.engine.Shape;
import city.cs.engine.StaticBody;
import city.cs.engine.World;

public class Platform extends StaticBody {
    private String type;

    public Platform(SuperLevel w, Shape shape, String type) {
        super(w, shape);
        this.type = type;
    }

    String getType() {
        return type;
    }
}
