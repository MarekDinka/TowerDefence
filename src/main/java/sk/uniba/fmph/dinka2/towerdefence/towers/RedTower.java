package sk.uniba.fmph.dinka2.towerdefence.towers;

import javafx.scene.canvas.GraphicsContext;
import sk.uniba.fmph.dinka2.towerdefence.monsters.Monster;
import sk.uniba.fmph.dinka2.towerdefence.tiles.TowerTile;

import java.util.List;

/**
 * Tower that deals mainly Red damage
 */
public class RedTower extends Tower {
    protected static final int PRICE = 100;
    public static String getTooltip() {return "Red tower";}

    public RedTower(TowerTile tile, List<Monster> monsters, GraphicsContext gc, boolean willItWork) {
        super(tile, monsters, gc, (short) 120, (short) 20, (short) 20, 150, 1000, willItWork);
    }

    public RedTower(TowerTile tile, List<Monster> monsters, GraphicsContext gc) {
        this(tile, monsters, gc, true);
    }
}
