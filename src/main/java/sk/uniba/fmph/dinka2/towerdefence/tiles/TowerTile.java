package sk.uniba.fmph.dinka2.towerdefence.tiles;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sk.uniba.fmph.dinka2.towerdefence.towers.Tower;

/**
 * A tile upon which tower can be placed
 */
public class TowerTile extends Tile {
    Tower t = null;

    public TowerTile(double x, double y, double size, GraphicsContext gc) {
        super(x, y, size, gc);
    }

    /**
     * Place tower upon this tile
     * @param t Tower to be placed
     */
    public void setTower(Tower t) {
        this.t = t;
    }

    /**
     * @return Tower on this tile
     */
    public Tower getTower() {
        return t;
    }

    @Override
    public void paint() {
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.GREY);
        gc.fillRect(x, y, size, size);
        gc.setFill(Color.BLACK);
        gc.strokeRect(x, y, size, size);
        if (t != null) {
            t.paint();
        }
    }
}
