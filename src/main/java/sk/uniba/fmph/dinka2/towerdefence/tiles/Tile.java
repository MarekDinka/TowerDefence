package sk.uniba.fmph.dinka2.towerdefence.tiles;

import javafx.scene.canvas.GraphicsContext;

/**
 * A tile in canvas
 */
public abstract class Tile {
    protected double x, y, size;
    GraphicsContext gc;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return size;
    }

    /**
     * Initialize global variables
     * @param x coordinate
     * @param y coordinate
     * @param size size of this tile
     * @param gc Graphics Context
     */
    Tile(double x, double y, double size, GraphicsContext gc) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.gc = gc;
    }

    public abstract void paint();

    /**
     * @param x1 Object coordinate
     * @param y1 Object coordinate
     * @return true if Object at coordinates x1, y1 is inside this tile
     */
    public boolean isInside(double x1, double y1) {
        return x1 >= x && x1 <= x+size && y1 >= y && y1 <= y+size;
    }
}
