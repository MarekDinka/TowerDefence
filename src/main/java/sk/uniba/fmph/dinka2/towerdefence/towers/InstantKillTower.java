package sk.uniba.fmph.dinka2.towerdefence.towers;

import javafx.scene.canvas.GraphicsContext;
import sk.uniba.fmph.dinka2.towerdefence.monsters.Monster;
import sk.uniba.fmph.dinka2.towerdefence.tiles.TowerTile;

import java.util.List;

/**
 * A tower that can kill most monsters with one projectile but shoots slowly
 */
public class InstantKillTower extends Tower {
    protected static final int PRICE = 250;
    public static String getTooltip() {return "White tower (kills with one shot)";}

    public InstantKillTower(TowerTile tile, List<Monster> monsters, GraphicsContext gc, boolean willItWork) {
        super(tile, monsters, gc, (short) 255, (short) 255, (short) 255, 100, 3000, willItWork);
    }

    public InstantKillTower(TowerTile tile, List<Monster> monsters, GraphicsContext gc) {
        this(tile, monsters, gc, true);
    }
}
