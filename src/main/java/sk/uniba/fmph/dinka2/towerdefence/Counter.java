package sk.uniba.fmph.dinka2.towerdefence;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * class managing countdown until next round
 */
public class Counter {
    private int count, gameId;
    private Timeline counting;

    /**
     * start the countdown from number c, after it is finished run function f
     * @param c countdown from
     * @param f function to run after countdown is finished
     */
    Counter(int c, Func f) {
        gameId = Game.getInstance().getId();
        this.count = c;
        counting = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (gameId != Game.getInstance().getId()) {
                counting.stop();
                return;
            }
            count--;
            if (count <= 0) {
                f.apply();
                counting.stop();
            }
        }));
        counting.setCycleCount(Timeline.INDEFINITE);
        counting.play();
    }

    /**
     * paint the countdown
     */
    public void paint() {
        Game.getInstance().paintBorderedText(String.valueOf(count), 30, 300);
    }

    @FunctionalInterface
    public interface Func {
        void apply();
    }
}
