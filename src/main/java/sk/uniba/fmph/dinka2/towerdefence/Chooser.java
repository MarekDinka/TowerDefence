package sk.uniba.fmph.dinka2.towerdefence;

/**
 * a tool that handles choosing between multiple options
 */
public interface Chooser {
    boolean choose(double x, double y);
    void paint();
    void checkForTooltip(double x, double y);
}
