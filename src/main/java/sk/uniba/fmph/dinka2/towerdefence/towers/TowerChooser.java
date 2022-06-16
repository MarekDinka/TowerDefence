package sk.uniba.fmph.dinka2.towerdefence.towers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sk.uniba.fmph.dinka2.towerdefence.Chooser;
import sk.uniba.fmph.dinka2.towerdefence.Game;
import sk.uniba.fmph.dinka2.towerdefence.Tooltip;
import sk.uniba.fmph.dinka2.towerdefence.monsters.Monster;
import sk.uniba.fmph.dinka2.towerdefence.tiles.TowerTile;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

/**
 * Handles buying a new tower
 */
public class TowerChooser implements Chooser {
    final List<Class<? extends Tower>> TOWERS = Arrays.asList(BlueTower.class, GreenTower.class, RedTower.class, InstantKillTower.class, BlankTower.class);
    final double X, Y, CELL_SIZE = 30;
    final GraphicsContext gc;
    final TowerTile towerTile;
    final List<Monster> monsters;

    /**
     * Initialize global variables
     * @param x coordinate
     * @param y coordinate
     * @param gc Graphics Context
     * @param towerTile Tile upon which new tower will be placed
     * @param monsters pointer to list of monsters that will be given to the new tower
     */
    public TowerChooser(double x, double y, GraphicsContext gc, TowerTile towerTile, List<Monster> monsters) {
        X = x;
        Y = y;
        this.gc = gc;
        this.towerTile = towerTile;
        this.monsters = monsters;
    }

    /**
     * Player has clicked, decide if he has clicked on one of the towers in the chooser, if yes place that tower
     * on towerTile
     * @param x click coordinate
     * @param y click coordinate
     * @return true if player has chosen a tower
     */
    @Override
    public boolean choose(double x, double y) {
        if (!(y < Y+CELL_SIZE && y > Y && x < X+(CELL_SIZE*(TOWERS.size()/2.0)) && x > X-(CELL_SIZE*(TOWERS.size()/2.0)))) {
            return false;
        }
        for (int i = 0; i < TOWERS.size(); i++) {
            if (x > X + CELL_SIZE * i - CELL_SIZE * (TOWERS.size() / 2.0) && x < X + CELL_SIZE * (i+1) - CELL_SIZE * (TOWERS.size() / 2.0)) {
                try {
                    if (Game.getInstance().buy(TOWERS.get(i).getDeclaredField("PRICE").getInt(null))) {
                        towerTile.setTower(TOWERS.get(i).getDeclaredConstructor(new Class[]{TowerTile.class, List.class,
                                GraphicsContext.class}).newInstance(towerTile, monsters, gc));
                        return true;
                    }
                    return false;
                } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * if mouse is hovering over one of the towers in chooser, display tooltip
     * @param x mouse coordinate
     * @param y mouse coordinate
     */
    @Override
    public void checkForTooltip(double x, double y) {
        if (!(y < Y+CELL_SIZE && y > Y && x < X+(CELL_SIZE*(TOWERS.size()/2.0)) && x > X-(CELL_SIZE*(TOWERS.size()/2.0)))) {
            Tooltip.getInstance().hide();
            return;
        }
        for (int i = 0; i < TOWERS.size(); i++) {
            if (x > X + CELL_SIZE * i - CELL_SIZE * (TOWERS.size() / 2.0) && x < X + CELL_SIZE * (i+1) - CELL_SIZE * (TOWERS.size() / 2.0)) {
                try {
                    Tooltip.getInstance().show((String)TOWERS.get(i).getDeclaredMethod("getTooltip").invoke(null),
                            X + CELL_SIZE * i - CELL_SIZE * (TOWERS.size() / 2.0) + CELL_SIZE / 2,
                            Y - CELL_SIZE / 2);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Paint Tower chooser
     */
    @Override
    public void paint() {
        double position;
        for (int i = 0; i < TOWERS.size(); i++) {
            position = X + CELL_SIZE * i - CELL_SIZE * (TOWERS.size() / 2.0);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(position, Y, CELL_SIZE, CELL_SIZE);
            gc.setFill(Color.WHITE);
            gc.fillRect(position, Y, CELL_SIZE, CELL_SIZE);
            try {
                TOWERS.get(i).getDeclaredConstructor(new Class[] {TowerTile.class, List.class, GraphicsContext.class,
                        boolean.class}).newInstance(new TowerTile(0, 0, 0, gc), null, gc, false).
                        paintForTowerChooser(position+CELL_SIZE/2, Y+CELL_SIZE/2);

                int price = TOWERS.get(i).getDeclaredField("PRICE").getInt(null);
                Game.getInstance().paintText(price+"g", position+CELL_SIZE/2, Y+CELL_SIZE/2+(CELL_SIZE/3));

            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }
}
