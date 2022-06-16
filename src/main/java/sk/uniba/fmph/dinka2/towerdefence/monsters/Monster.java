package sk.uniba.fmph.dinka2.towerdefence.monsters;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;
import sk.uniba.fmph.dinka2.towerdefence.Game;
import sk.uniba.fmph.dinka2.towerdefence.tiles.PathTile;

import java.util.List;

/**
 * Abstract class that gives all types of monsters their functionality in one place
 */
public abstract class Monster {
    protected final int gameId;
    Timeline t;
    protected double x, y;
    protected int currentTile;
    protected final List<PathTile> path;
    protected final double STEP = 1.5, SIZE = 10, RESISTANCE;
    protected final GraphicsContext gc;
    protected final Pair<Double, Double> desiredLoc;
    protected boolean isAlive;
    protected short r, g, b;
    /**
     * Reward for killing this type of monster and damage this type of monster can deal to player if it makes it to finish
     */
    public static final int KILL_REWARD = 20, DAMAGE = 5; //default

    /**
     * Initializes global variables and defines its movement (Timeline t)
     * @param path Path of the monster which it needs to take in order to finish and win
     * @param gc Graphics Context
     * @param r amount of red health it has
     * @param g amount of green health it has
     * @param b amount of blue health it has
     */
    public Monster(List<PathTile> path, GraphicsContext gc, short r, short g, short b) {
        gameId = Game.getInstance().getId();
        RESISTANCE = Game.getInstance().getResistance();
        isAlive = false;
        this.r = r;
        this.g = g;
        this.b = b;
        this.x = 0;
        this.y = path.get(0).getY()+(Math.random()*(path.get(0).getSize()-10)+5);
        this.currentTile = 0;
        this.path = path;
        this.gc = gc;
        this.desiredLoc = new Pair<>(Math.random()*path.get(0).getSize()-path.get(0).getX(), this.y-path.get(0).getY());
        t = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            if (gameId != Game.getInstance().getId()) {
                t.stop();
                isAlive = false;
                return;
            }
            if (currentTile >= path.size() || !isAlive) {
                t.stop();
                if (currentTile >= path.size()) {
                    Game.getInstance().killMonster(this, false);
                    try {
                        Game.getInstance().damagePlayer(this.getClass().getDeclaredField("DAMAGE").getInt(null));
                    } catch (NoSuchFieldException | IllegalAccessException ignored) {}
                }
                return;
            }
            move();
        }));
        t.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * tells the monster that it is alive now and can start moving
     */
    public void begin() {
        isAlive = true;
        t.play();
    }

    protected double sqrtDistance(double x1, double y1, double x2, double y2) {
        return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
    }

    public double getX() {return x;}
    public double getY() {return y;}

    /**
     * Something has hit the monster and dealt damage to it, if the damage is big enough, monster will die
     * @param rDamage red damage
     * @param gDamage green damage
     * @param bDamage blue damage
     */
    public void hit(short rDamage, short gDamage, short bDamage) {
        rDamage -= rDamage*RESISTANCE;
        gDamage -= gDamage*RESISTANCE;
        bDamage -= bDamage*RESISTANCE;
        if (r + rDamage > 255) {
            r = 255;
        } else {
            r += rDamage;
        }
        if (g + gDamage > 255) {
            g = 255;
        } else {
            g += gDamage;
        }
        if (b + bDamage > 255) {
            b = 255;
        } else {
            b += bDamage;
        }
        if (r == 255 & g == 255 && b == 255) {
            isAlive = false;
        }
    }

    /**
     * @return true if monster is alive
     */
    public boolean isAlive() {return isAlive;}

    /**
     * move monster in its desired direction by STEP
     */
    public void move() {
        if (currentTile + 1 >= path.size()) {
            x += STEP;
        } else {
            double dx = path.get(currentTile+1).getX()-x+desiredLoc.getKey(), dy = path.get(currentTile+1).getY()-y+desiredLoc.getValue();
            double tmp = STEP/(Math.abs(dx)+Math.abs(dy));
            if (Double.isFinite(dx) && Double.isFinite(tmp)) {
                x += tmp*dx;
            }
            if (Double.isFinite(dy) && Double.isFinite(tmp)) {
                y += tmp*dy;
            }
        }
        if (!path.get(currentTile).isInside(x, y)) {
            currentTile++;
        }
    }

    /**
     * @param x1 projectile coordinate
     * @param y1 projectile coordinate
     * @return true if this monster been hit by projectile at coordinates x1, x2
     */
    public boolean isHit(double x1, double y1) {
        return sqrtDistance(x, y, x1, y1) < (SIZE/2)*(SIZE/2);
    }

    /**
     * @param R red damage
     * @param G green damage
     * @param B blue damage
     * @return how much damage would be dealt to this monster if it were attacked by projectile with these damage values
     */
    public int howMuchDamageWillIDealToYou(short R, short G, short B) {
        R -= R*RESISTANCE;
        G -= G*RESISTANCE;
        B -= B*RESISTANCE;
        if (r+R >= 255 && g+G >= 255 && b+B >= 255) {
            return 1000;
        }
        return R-((r+R > 255) ? (r+R-1)%255 : 0) +
                G-((g+G > 255) ? (g+G-1)%255 : 0) +
                B-((b+B > 255) ? (b+B-1)%255 : 0);
    }//                              ^because (255+255)%255 == 0

    /**
     * paint the monster
     */
    public void paint() {
        if (!isAlive) {return;}
        gc.setFill(Color.rgb(r, g, b));
        gc.fillOval(x-SIZE/2, y-SIZE/2, SIZE, SIZE);
        gc.setFill(Color.BLACK);
        gc.strokeOval(x-SIZE/2, y-SIZE/2, SIZE, SIZE);
    }
}
