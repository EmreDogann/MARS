package game;

import org.jbox2d.common.Vec2;
import java.util.ArrayList;

public class LevelTwo extends SuperLevel {

    LevelTwo(ArrayList<String> availableLevels) {
        super(STATE.GAME, availableLevels);
    }

    @Override
    public Vec2 startPosition() {
        return new Vec2(0, 0);
    }
}
