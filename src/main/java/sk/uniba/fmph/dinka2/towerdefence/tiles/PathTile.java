package sk.uniba.fmph.dinka2.towerdefence.tiles;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A path tile, all Path tiles together create path along which monsters will go
 */
public class PathTile extends Tile {
    public PathTile(double x, double y, double size, GraphicsContext gc) {
        super(x, y, size, gc);
    }

    @Override
    public void paint() {
//        gc.setFill(Color.DARKGREEN);
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.WHITE);
        gc.fillRect(x, y, size, size);
        gc.setFill(Color.BLACK);
        gc.strokeRect(x, y, size, size);
    }
}
