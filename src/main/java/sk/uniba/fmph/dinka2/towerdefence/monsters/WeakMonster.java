package sk.uniba.fmph.dinka2.towerdefence.monsters;

import javafx.scene.canvas.GraphicsContext;
import sk.uniba.fmph.dinka2.towerdefence.tiles.PathTile;

import java.util.List;

/**
 * A monster that is almost dead
 */
public class WeakMonster extends Monster {
    public static final int KILL_REWARD = 5, DAMAGE = 5;

    public WeakMonster(List<PathTile> path, GraphicsContext gc) {
        super(path, gc, (short) 254, (short) 254, (short) 254);
    }
}
