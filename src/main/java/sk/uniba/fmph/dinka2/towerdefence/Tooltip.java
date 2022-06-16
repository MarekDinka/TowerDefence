package sk.uniba.fmph.dinka2.towerdefence;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Text that can be shown upon moving mouse over specific location in canvas
 */
public class Tooltip {
    private static final Tooltip INSTANCE = new Tooltip();
    private Tooltip() {}
    public static Tooltip getInstance() {return INSTANCE;}

    private String text;
    private double x, y;
    private boolean isShowing = false;

    private GraphicsContext gc;

    /**
     * Constructor
     * @param gc Graphics Context
     */
    public void init(GraphicsContext gc) {
        this.gc = gc;
    }

    /**
     * @return if tooltip is currently showing on canvas
     */
    public boolean isShowing() {return isShowing;}

    /**
     * show tooltip on coordinates x, y with text
     * @param text text to show
     * @param x coordinate
     * @param y coordinate
     */
    public void show(String text, double x, double y) {
        this.text = text;
        this.x = x;
        if (y < 0) {
            y = 75;
        }
        this.y = y;
        isShowing = true;
    }

    /**
     * stop showing tooltip
     */
    public void hide() {
        isShowing = false;
    }

    /**
     * paint the tooltip
     */
    public void paint() {
        if (isShowing) {
            Text t = new Text(text);
            t.setFont(Font.font("Consolas", 10));
            double w = t.getLayoutBounds().getWidth(), h = t.getLayoutBounds().getHeight();
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.WHITE);
            gc.strokeRect(x-w/2, y-h/2, w, h);
            gc.fillRect(x-w/2, y-h/2, w, h);
            Game.getInstance().paintText(text, x, y);
        }
    }
}
