package sk.uniba.fmph.dinka2.towerdefence.towers;

import javafx.scene.canvas.GraphicsContext;
import sk.uniba.fmph.dinka2.towerdefence.monsters.Monster;
import sk.uniba.fmph.dinka2.towerdefence.tiles.TowerTile;

import java.util.List;

/**
 * Tower that deals mainly Blue damage
 */
public class BlueTower extends Tower {
    protected static final int PRICE = 100;
    protected static final String TOOLTIP = "Blue tower";

    public static String getTooltip() {return "Blue tower";}

    public BlueTower(TowerTile tile, List<Monster> monsters, GraphicsContext gc, boolean willItWork) {
        super(tile, monsters, gc, (short) 20, (short) 20, (short) 120, 150, 1000, willItWork);
    }

    public BlueTower(TowerTile tile, List<Monster> monsters, GraphicsContext gc) {
        this(tile, monsters, gc, true);
    }
}
