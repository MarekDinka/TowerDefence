package sk.uniba.fmph.dinka2.towerdefence.towers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sk.uniba.fmph.dinka2.towerdefence.Chooser;
import sk.uniba.fmph.dinka2.towerdefence.Game;
import sk.uniba.fmph.dinka2.towerdefence.Tooltip;

import java.util.ArrayList;
import java.util.List;

/**
 * A chooser for upgrading tower
 */
public class TowerActionChooser implements Chooser {
    private List<Choice> choices = new ArrayList<>();
    final double X, Y, CELL_SIZE = 30;
    final GraphicsContext gc;
    final Tower tower;

    private final int COLOR_UPGRADE_COST = 20, SPEED_UPGRADE_COST = 50, RANGE_UPGRADE_COST = 30;

    /**
     * initialize global parameters and decide which upgrades to display
     * @param x coordinate
     * @param y coordinate
     * @param gc graphics context
     * @param t tower to upgrade
     */
    public TowerActionChooser(double x, double y, GraphicsContext gc, Tower t) {
        X = x;
        Y = y;
        this.gc = gc;
        tower = t;
        if (t.rDamage < 255) {
            choices.add(new UpgradeColor((short) 255, (short) 0, (short) 0));
        }
        if (t.gDamage < 255) {
            choices.add(new UpgradeColor((short) 0, (short) 255, (short) 0));
        }
        if (t.bDamage < 255) {
            choices.add(new UpgradeColor((short) 0, (short) 0, (short) 255));
        }
        if (t.shootDelay > 500) {
            choices.add(new UpgradeSpeed());
        }
        if (t.range < 200) {
            choices.add(new UpgradeRange());
        }
    }

    /**
     * Decide if player has clicked on upgrade, if yes -> upgrade the tower
     * @param x click coordinate
     * @param y click coordinate
     * @return true if player has chosen a upgrade
     */
    @Override
    public boolean choose(double x, double y) {
        if (!(y < Y+CELL_SIZE && y > Y && x < X+(CELL_SIZE*(choices.size()/2.0)) && x > X-(CELL_SIZE*(choices.size()/2.0)))) {
            return false;
        }
        for (int i = 0; i < choices.size(); i++) {
            if (x > X + CELL_SIZE * i - CELL_SIZE * (choices.size() / 2.0) && x < X + CELL_SIZE * (i+1) - CELL_SIZE * (choices.size() / 2.0)) {
                choices.get(i).resolve();
                return true;
            }
        }
        return false;
    }

    /**
     * if mouse is hovering over upgrade, display its tooltip
     * @param x mouse coordinate
     * @param y mouse coordinate
     */
    @Override
    public void checkForTooltip(double x, double y) {
        if (!(y < Y+CELL_SIZE && y > Y && x < X+(CELL_SIZE*(choices.size()/2.0)) && x > X-(CELL_SIZE*(choices.size()/2.0)))) {
            Tooltip.getInstance().hide();
            return;
        }
        for (int i = 0; i < choices.size(); i++) {
            if (x > X + CELL_SIZE * i - CELL_SIZE * (choices.size() / 2.0) && x < X + CELL_SIZE * (i+1) - CELL_SIZE * (choices.size() / 2.0)) {
                Tooltip.getInstance().show(choices.get(i).getTooltipText(),
                        X + CELL_SIZE * i - CELL_SIZE * (choices.size() / 2.0) + CELL_SIZE/2,
                        Y-CELL_SIZE/2);
            }
        }
    }

    /**
     * paint tower action chooser
     */
    @Override
    public void paint() {
        double position;
        for (int i = 0; i < choices.size(); i++) {
            position = X + CELL_SIZE * i - CELL_SIZE * (choices.size() / 2.0);
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.WHITE);
            gc.strokeRect(position, Y, CELL_SIZE, CELL_SIZE);
            gc.fillRect(position, Y, CELL_SIZE, CELL_SIZE);
            choices.get(i).paint(position+CELL_SIZE/2, Y+CELL_SIZE/2);
        }
    }

    /**
     * a upgrade
     */
    private interface Choice {
        /**
         * apply effect of this upgrade to tower
         */
        void resolve();
        void paint(double x, double y);
        String getTooltipText();
    }

    /**
     * upgrade damage of one specific color
     */
    private class UpgradeColor implements Choice {
        private final short R, G, B;

        UpgradeColor(short r, short g, short b) {
            R = r;
            G = g;
            B = b;
        }

        @Override
        public String getTooltipText() {
            return (R > 0) ? "Add red damage" : (G > 0) ? "Add green damage" : "Add blue damage";
        }

        @Override
        public void resolve() {
            if (!Game.getInstance().buy(COLOR_UPGRADE_COST)) {
                return;
            }
            if (R > 0) {
                tower.addR();
            } else if (G > 0) {
                tower.addG();
            } else if (B > 0) {
                tower.addB();
            }
        }

        @Override
        public void paint(double x, double y) {
            final double S = 2;
            gc.setFill(Color.rgb(R, G, B));
            gc.fillRect(x-2*S, y-5*S, 4*S, 11*S);
            gc.fillRect(x-4*S, y-3*S, 2*S, 2*S);
            gc.fillRect(x+2*S, y-3*S, 2*S, 2*S);
            gc.fillRect(x-3*S, y-4*S, S, S);
            gc.fillRect(x+2*S, y-4*S, S, S);
            gc.fillRect(x-S, y-6*S, S*2, S);

            Game.getInstance().paintText(COLOR_UPGRADE_COST+"g", x, y+(CELL_SIZE/3));
        }
    }

    /**
     * decrease delay between tower shots
     */
    private class UpgradeSpeed implements Choice {
        @Override
        public String getTooltipText() {
            return "Decrease delay between shots";
        }

        @Override
        public void resolve() {
            if (Game.getInstance().buy(SPEED_UPGRADE_COST)) {
                tower.addSpeed();
            }
        }

        @Override
        public void paint(double x, double y) {
            gc.drawImage(Game.upgradeSpeedImage, x-(CELL_SIZE-1)/2, y-(CELL_SIZE-1)/2, (CELL_SIZE-1), (CELL_SIZE-1));
            Game.getInstance().paintText(SPEED_UPGRADE_COST+"g", x, y+(CELL_SIZE/3));
        }
    }

    /**
     * increase tower range
     */
    private class UpgradeRange implements Choice {
        @Override
        public String getTooltipText() {
            return "Add range";
        }

        @Override
        public void resolve() {
            if (Game.getInstance().buy(RANGE_UPGRADE_COST)) {
                tower.addRange();
            }
        }

        @Override
        public void paint(double x, double y) {
            gc.drawImage(Game.upgradeRangeImage, x-(CELL_SIZE-1)/2, y-(CELL_SIZE-1)/2, (CELL_SIZE-1), (CELL_SIZE-1));
            Game.getInstance().paintText(RANGE_UPGRADE_COST+"g", x, y+(CELL_SIZE/3));
        }
    }
}