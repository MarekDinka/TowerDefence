package sk.uniba.fmph.dinka2.towerdefence.monsters;

import javafx.scene.canvas.GraphicsContext;
import sk.uniba.fmph.dinka2.towerdefence.tiles.PathTile;

import java.util.List;

/**
 * A monster that has less Blue health
 */
public class BlueMonster extends Monster {
    public static final int KILL_REWARD = 20, DAMAGE = 10;

    public BlueMonster(List<PathTile> path, GraphicsContext gc) {
        super(path, gc, (short) 50, (short) 50, (short) 200);
    }
}
