package sk.uniba.fmph.dinka2.towerdefence.towers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import sk.uniba.fmph.dinka2.towerdefence.Game;
import sk.uniba.fmph.dinka2.towerdefence.monsters.Monster;
import sk.uniba.fmph.dinka2.towerdefence.tiles.TowerTile;

import java.util.*;

/**
 * Abstract class that handles all Tower functionality
 */
public abstract class Tower {
    protected final List<Projectile> projectiles;

    protected final int gameId;

    protected double range;
    protected short rDamage, gDamage, bDamage;
    protected int shootDelay;
    protected final int PROJECTILE_MOVE_DELAY = 7;
    protected final short DAMAGE_ADDED_ON_UPGRADE = 30;

    protected static final int PRICE = 100; //default price

    /**
     * @return String that is to be displayed in tooltip if player moves his mouse over said tower
     */
    public static String getTooltip() {return "Error";}

    protected final GraphicsContext gc;
    protected final List<Monster> monsters;
    protected final double x, y;
    protected Timeline drawRange = null;

    /**
     * Initialize global variables and start shooting
     * @param tile tile upon which this tower will be
     * @param monsters pointer to all list of all monsters
     * @param gc Graphics Context
     * @param rDamage Red damage
     * @param gDamage Green damage
     * @param bDamage Blue damage
     * @param range range of the tower
     * @param shootDelay delay between each shot
     * @param willWork will the tower shoot or will it only be for display (in TowerChooser)
     */
    public Tower(TowerTile tile, List<Monster> monsters, GraphicsContext gc, short rDamage, short gDamage,
                 short bDamage, double range, int shootDelay, boolean willWork) {
        gameId = Game.getInstance().getId();
        this.range = range;
        this.rDamage = rDamage;
        this.gDamage = gDamage;
        this.bDamage = bDamage;
        this.shootDelay = shootDelay;

        x = tile.getX()+tile.getSize()/2;
        y = tile.getY()+tile.getSize()/2;
        this.monsters = monsters;
        this.gc = gc;
        projectiles = new ArrayList<>();

        if (willWork) {
            new Timeline(new KeyFrame(Duration.millis(shootDelay), e -> shoot())).play();
        }
    }

    /**
     * Add red damage
     */
    public void addR() {
        rDamage += DAMAGE_ADDED_ON_UPGRADE;
        if (rDamage > 255) {
            rDamage = 255;
        }
    }

    /**
     * Add green damage
     */
    public void addG() {
        gDamage += DAMAGE_ADDED_ON_UPGRADE;
        if (gDamage > 255) {
            gDamage = 255;
        }
    }

    /**
     * Add blue damage
     */
    public void addB() {
        bDamage += DAMAGE_ADDED_ON_UPGRADE;
        if (bDamage > 255) {
            bDamage = 255;
        }
    }

    /**
     * Reduce delay between shots
     */
    public void addSpeed() {
        if (shootDelay > 500) {
            shootDelay -= 100;
        }
    }

    /**
     * increase range
     */
    public void addRange() {
        if (range < 200) {
            range += 20;
            if (drawRange == null) {
                drawRange = new Timeline(new KeyFrame(Duration.millis(1000), e -> drawRange = null));
                drawRange.play();
            }
        }
    }

    protected double sqrtDistance(double x1, double y1, double x2, double y2) {
        return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
    }

    /**
     * Find the best target -> the one to which this tower will deal the most damage from top 10 closest alive monsters
     * in range
     * @return Target or null if no target was found
     */
    protected Monster getTarget() {
        return monsters.stream().filter(m -> m.isAlive() && sqrtDistance(x, y, m.getX(), m.getY()) < range*range).
                sorted(Comparator.comparingDouble(m -> sqrtDistance(x, y, m.getX(), m.getY()))).limit(10).
                max(Comparator.comparingInt(m -> m.howMuchDamageWillIDealToYou(rDamage, gDamage, bDamage))).orElse(null);
    }

    /**
     * gives new target to projectile whose target has died before it was hit by said projectile
     * @param X coordinate of projectile
     * @param Y coordinate of projectile
     * @return new Target or null if no target was found
     */
    public Monster findNewTarget(double X, double Y) {
        return monsters.stream().filter(m -> sqrtDistance(m.getX(), m.getY(), X, Y) < 100*100).
                min(Comparator.comparingDouble(m -> sqrtDistance(m.getX(), m.getY(), X, Y))).orElse(null);
    }

    protected void shoot() {
        if (gameId != Game.getInstance().getId()) {
            return;
        }
        Monster m = getTarget();
        if (m == null) {
            new Timeline(new KeyFrame(Duration.millis(shootDelay), e -> shoot())).play();
            return;
        }
        Projectile p = new Projectile(x, y, m, gc, Color.rgb(rDamage, gDamage, bDamage), this);
        Timeline t = new Timeline(new KeyFrame(Duration.millis(PROJECTILE_MOVE_DELAY), e -> {
            Timeline projectile = p.move();
            if (projectile != null) {
                projectile.stop();
                if (p.target != null) {
                    p.target.hit(rDamage, gDamage, bDamage);
                    if (!p.target.isAlive()) {
                        Game.getInstance().killMonster(p.target, true);
                    }
                }
                projectiles.remove(p);
            }
        }));
        p.setTimeline(t);
        projectiles.add(p);
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
        new Timeline(new KeyFrame(Duration.millis(shootDelay), e -> shoot())).play();
    }

    /**
     * paint the tower
     */
    public void paint() {
        gc.setFill(Color.rgb(rDamage, gDamage, bDamage));
//        gc.fillRect(tile.getX()+15, tile.getY()+15, tile.getSize()-30, tile.getSize()-30);
        gc.fillRect(x-15, y-15, 30, 30);
        gc.setFill(Color.BLACK);
        gc.strokeRect(x-15, y-15, 30, 30);
        new Timeline(new KeyFrame(Duration.millis(5), e -> {
            for (Projectile p : projectiles) {
                p.paint();
            }
            if (drawRange != null) {
                gc.setStroke(Color.RED);
                gc.strokeOval(x-range, y-range, range*2, range*2);
            }
        })).play();
    }

    /**
     * paint display tower that will not shoot
     * @param x coordinate
     * @param y coordinate
     */
    public void paintForTowerChooser(double x, double y) {
        gc.setFill(Color.rgb(rDamage, gDamage, bDamage));
        gc.fillRect(x-10, y-10, 20, 20);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x-10, y-10, 20, 20);
    }
}
