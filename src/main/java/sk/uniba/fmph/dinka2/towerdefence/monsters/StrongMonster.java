package sk.uniba.fmph.dinka2.towerdefence.monsters;

import javafx.scene.canvas.GraphicsContext;
import sk.uniba.fmph.dinka2.towerdefence.tiles.PathTile;

import java.util.List;

/**
 * A monster that starts with maximal posible amount of each health
 */
public class StrongMonster extends Monster {
    public static final int KILL_REWARD = 50, DAMAGE = 20;

    public StrongMonster(List<PathTile> path, GraphicsContext gc) {
        super(path, gc, (short) 0, (short) 0, (short) 0);
    }
}
