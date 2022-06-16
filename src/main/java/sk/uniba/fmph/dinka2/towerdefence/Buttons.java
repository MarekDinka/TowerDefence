package sk.uniba.fmph.dinka2.towerdefence;

import javafx.application.Platform;
import javafx.scene.control.Button;

/**
 * class containing all buttons required for the game
 */
public class Buttons {
    /**
     * upon pressing exit the application
     */
    public static class ExitButton extends Button {
        ExitButton() {
            super("Quit");
            setStyle("-fx-border-color: black; -fx-font: 15px 'Arial';");
            setOnAction(e -> Platform.exit());
        }
    }

    /**
     * restart the game
     */
    public static class RestartButton extends Button {
        RestartButton() {
            super("Restart");
            setStyle("-fx-border-color: black; -fx-font: 15px 'Arial';");
            setOnAction(e -> Game.getInstance().restart(Game.GameEndText.NONE));
        }
    }

    /**
     * set difficulty to easy
     */
    public static class EasyButton extends Button {
        EasyButton() {
            super("Easy");
            setStyle("-fx-border-color: black; -fx-font: 15px 'Arial';");
            setOnAction(e -> Game.getInstance().begin(250, 10, 15, 0.0));
        }
    }

    /**
     * set difficulty to medium
     */
    public static class MediumButton extends Button {
        MediumButton() {
            super("Medium");
            setStyle("-fx-border-color: black; -fx-font: 15px 'Arial';");
            setOnAction(e -> Game.getInstance().begin(150, 15, 20, 0.15));
        }
    }

    /**
     * set difficulty to hard
     */
    public static class HardButton extends Button {
        HardButton() {
            super("Hard");
            setStyle("-fx-border-color: black; -fx-font: 15px 'Arial';");
            setOnAction(e -> Game.getInstance().begin(100, 20, 25, 0.30));
        }
    }
}
