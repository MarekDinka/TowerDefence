package sk.uniba.fmph.dinka2.towerdefence.monsters;

import javafx.scene.canvas.GraphicsContext;
import sk.uniba.fmph.dinka2.towerdefence.tiles.PathTile;

import java.util.List;

/**
 * A monster that has less Green health
 */
public class GreenMonster extends Monster {
    public static final int KILL_REWARD = 20, DAMAGE = 10;

    public GreenMonster(List<PathTile> path, GraphicsContext gc) {
        super(path, gc, (short) 50, (short) 200, (short) 50);
    }
}
