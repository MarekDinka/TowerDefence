package sk.uniba.fmph.dinka2.towerdefence.towers;

import javafx.scene.canvas.GraphicsContext;
import sk.uniba.fmph.dinka2.towerdefence.monsters.Monster;
import sk.uniba.fmph.dinka2.towerdefence.tiles.TowerTile;

import java.util.List;

/**
 * A tower that deals almost no damage but is also cheap
 */
public class BlankTower extends Tower {
    protected static final int PRICE = 10;
    public static String getTooltip() {return "Blank tower (Deals almost no damage)";}

    public BlankTower(TowerTile tile, List<Monster> monsters, GraphicsContext gc, boolean willItWork) {
        super(tile, monsters, gc, (short) 5, (short) 5, (short) 5, 100, 1000, willItWork);
    }

    public BlankTower(TowerTile tile, List<Monster> monsters, GraphicsContext gc) {
        this(tile, monsters, gc, true);
    }
}
