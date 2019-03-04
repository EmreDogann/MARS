package game;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;

public class LevelEditor extends SuperLevel {

    LevelEditor(ArrayList<String> levels) {
        super(STATE.LEVEL_EDITOR, levels);
    }

    @Override
    public Vec2 startPosition() {
        return new Vec2(0, -9.5f);
    }
}
