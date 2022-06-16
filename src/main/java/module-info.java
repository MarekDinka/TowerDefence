module sk.uniba.fmph.dinka2.towerdefence {
    requires javafx.controls;
    requires javafx.fxml;


    opens sk.uniba.fmph.dinka2.towerdefence to javafx.fxml;
    exports sk.uniba.fmph.dinka2.towerdefence;
    exports sk.uniba.fmph.dinka2.towerdefence.tiles;
    exports sk.uniba.fmph.dinka2.towerdefence.monsters;
    opens sk.uniba.fmph.dinka2.towerdefence.tiles to javafx.fxml;
}