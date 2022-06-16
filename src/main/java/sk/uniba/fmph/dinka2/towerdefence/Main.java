package sk.uniba.fmph.dinka2.towerdefence;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application
 */
public class Main extends Application {

    /**
     * Initialize the game using Game class
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(Game.getInstance().init());
        stage.setTitle("Tower Defence");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
