package sk.uniba.fmph.dinka2.towerdefence.towers;

import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sk.uniba.fmph.dinka2.towerdefence.Game;
import sk.uniba.fmph.dinka2.towerdefence.monsters.Monster;

/**
 * A projectile that can be fired by towers and can damage monsters
 */
public class Projectile {
    private final int gameId;
    double x, y;
    Monster target;
    final Tower tower;
    final double STEP = 1D;
    final GraphicsContext gc;
    final Color c;
    Timeline t;

    /**
     * Initializes global variables
     * @param x current location
     * @param y current location
     * @param target target which this projectile is trying to hit
     * @param gc Graphics Context
     * @param c Color of the projectile
     * @param t tower that fired this projectile
     */
    public Projectile(double x, double y, Monster target, GraphicsContext gc, Color c, Tower t) {
        gameId = Game.getInstance().getId();
        this.x = x;
        this.y = y;
        this.target = target;
        this.gc = gc;
        this.c = c;
        this.tower = t;
    }

    /**
     * @param t Timeline that makes this projectile move
     */
    public void setTimeline(Timeline t) {
        this.t = t;
    }

    /**
     * Move towards target, if it is dead, find a new one
     * @return Timeline t if projectile hit its target, else null
     */
    public Timeline move() {
        if (gameId != Game.getInstance().getId()) {
            t.stop();
            return null;
        }
        if (!target.isAlive()) {
            target = tower.findNewTarget(target.getX(), target.getY());
        }
        if (target == null) {
            return t;
        }
        double dx = (STEP/(Math.abs(target.getX()-x)+Math.abs(target.getY()-y)))*(target.getX()-x);
        double dy = (STEP/(Math.abs(target.getX()-x)+Math.abs(target.getY()-y)))*(target.getY()-y);
        x += dx;
        y += dy;
        return (target.isHit(x, y)) ? t : null;
    }

    /**
     * paint the projectile
     */
    public void paint() {
        gc.setFill(c);
        gc.fillOval(x-2, y-2, 4, 4);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(x-2, y-2, 4, 4);
    }
}
