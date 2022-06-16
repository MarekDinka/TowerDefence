package sk.uniba.fmph.dinka2.towerdefence.tiles;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A default green tile, does nothing special
 */
public class DefaultTile extends Tile{
    public DefaultTile(double x, double y, double size, GraphicsContext gc) {
        super(x, y, size, gc);
    }

    @Override
    public void paint() {
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.DARKSEAGREEN);
        gc.fillRect(x, y, size, size);
        gc.setFill(Color.BLACK);
        gc.strokeRect(x, y, size, size);
    }
}
