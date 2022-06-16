package sk.uniba.fmph.dinka2.towerdefence;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;
import sk.uniba.fmph.dinka2.towerdefence.monsters.Monster;
import sk.uniba.fmph.dinka2.towerdefence.tiles.DefaultTile;
import sk.uniba.fmph.dinka2.towerdefence.tiles.PathTile;
import sk.uniba.fmph.dinka2.towerdefence.tiles.Tile;
import sk.uniba.fmph.dinka2.towerdefence.tiles.TowerTile;
import sk.uniba.fmph.dinka2.towerdefence.towers.TowerActionChooser;
import sk.uniba.fmph.dinka2.towerdefence.towers.TowerChooser;

import java.util.*;

/**
 * A singleton class that initializes the game and provides methods for handling game events (killMonster, reward, buy...)
 */
public class Game {
    private int id;

    private static final Game INSTANCE = new Game();
    public static Game getInstance() {return INSTANCE;}
    private Game() {}

    /**
     * Delay between each repainting session
     */
    public static final int PAINT_DELAY = 10;

    /**
     * width, height, size of all tiles
     */
    final static int w = 800, h = 600, TILE_SIZE = 50;
    private Board pg;
    private Tile[][] tiles;
    private List<Monster> monsters;
    private final Random rnd = new Random();
    private List<Pair<Integer, Integer>> path;
    private Chooser chooser = null;
    private Phase phase;
    private BorderPane bp;
    private Timeline paintTimeline;
    private Counter counter;
    private boolean showNotEnoughGold = false;
    private double resistance;

    private final int VICTORY_LEVEL = 10;
    private int level = 0, gold = 0, health = 100;
    private Label levelLabel, goldLabel, healthLabel;

    /**
     * Preloaded images for later use
     */
    public static final Image upgradeRangeImage = new Image("UpgradeRange.png"), upgradeSpeedImage = new Image("UpgradeSpeed.png");

    /**
     * types of tiles
     */
    public enum TileType {DEFAULT_TILE, PATH_TILE, TOWER_TILE};

    /**
     * game phases
     */
    public enum Phase {FIGHTING, BUILDING};

    /**
     * What to display when game has ended
     */
    public enum GameEndText {NONE, VICTORY, LOSS};

    /**
     * each time a new game is initialized (difficulty is selected), id is made so that all ongoing
     * processes can recognize they should no longer be active
     * @return GameId
     */
    public int getId() {return id;};
    private void changeId() {
        int newId;
        do {
             newId = rnd.nextInt(1000);
        } while (newId == id);
        id = newId;
    }

    /**
     * @return resistance for all monsters, which is based on game difficulty
     */
    public double getResistance() {return resistance;}

    private int changeDir(int oldDir) {
        switch (oldDir) {
            case -1, 1 -> {return 0;}
            default -> {return (rnd.nextInt(0, 2) == 1) ? 1 : -1;}
        }
    }

    private TileType[][] generateMap() {
        TileType[][] result = new TileType[h/TILE_SIZE][w/TILE_SIZE];
        for (int i = 0; i < result.length; i++) {
            Arrays.fill(result[i], TileType.DEFAULT_TILE);
        }
        int x = 0, y = (int) Math.floor(result.length/2.0), dy = 0;
        path = new ArrayList<>();
        final int minStepsToTakeAfterChangeInDirection = 2;
        int toSkip = 2;
        Pair<Integer, Integer> p;
        while (x < w/TILE_SIZE) {
            result[y][x] = TileType.PATH_TILE;
            p = new Pair<>(x, y);
            if (path.size() == 0 || !path.get(path.size()-1).equals(p)) {
                path.add(p);
            }
            if (x >= result[0].length-2) {
                dy = 0;
            } else if (toSkip != 0) {
                toSkip--;
            } else {
                dy = changeDir(dy);
                toSkip = rnd.nextInt(-1, 2)+minStepsToTakeAfterChangeInDirection;
            }
            if (dy == 0) {
                x++;
            }
            if (y + dy >= 1 && y+dy < result.length-1) {
                y += dy;
            }
        }
        int len = path.size();
        int howOftenToPutTowerSpot = (int)(len-4)/6, countDown = 0;
        for (int i = 2; i < path.size()-1; i++) {
            if (countDown == 0) {
                x = path.get(i).getKey();
                y = path.get(i).getValue();
                if (rnd.nextBoolean()) {
                    if (result[y-1][x] != TileType.PATH_TILE) {
                        result[y-1][x] = TileType.TOWER_TILE;
                    } else if (result[y+1][x] != TileType.PATH_TILE) {
                        result[y+1][x] = TileType.TOWER_TILE;
                    } else {
                        result[y][x+1] = TileType.TOWER_TILE;
                    }
                } else {
                    if (result[y+1][x] != TileType.PATH_TILE) {
                        result[y+1][x] = TileType.TOWER_TILE;
                    } else if (result[y-1][x] != TileType.PATH_TILE) {
                        result[y-1][x] = TileType.TOWER_TILE;
                    } else {
                        result[y][x+1] = TileType.TOWER_TILE;
                    }
                }
                countDown = howOftenToPutTowerSpot;
            }
            countDown--;
        }
        return result;
    }

    private GraphicsContext setUpBorderPane() {
        BlankBoard bb = new BlankBoard();
        bp.setCenter(bb);

        Label l1 = new Label("Level:"), l2 = new Label("Gold:"), l3 = new Label("Health:");
        levelLabel = new Label(level + "/" + VICTORY_LEVEL);
        goldLabel = new Label(String.valueOf(gold));
        healthLabel = new Label(String.valueOf(health) + "%");
        l1.setFont(Font.font(15));
        l2.setFont(Font.font(15));
        l3.setFont(Font.font(15));
        levelLabel.setFont(Font.font(15));
        goldLabel.setFont(Font.font(15));
        healthLabel.setFont(Font.font(15));

        HBox top = new HBox(l1, levelLabel, new Label("          "), l2, goldLabel, new Label("          "), l3, healthLabel);
        top.setAlignment(Pos.CENTER);
        top.setSpacing(8);
        bp.setTop(top);

        HBox bottom = new HBox(new Buttons.EasyButton(), new Buttons.MediumButton(), new Buttons.HardButton(), new Buttons.ExitButton());
        bottom.setAlignment(Pos.CENTER);
        bp.setBottom(bottom);
        return bb.getGraphicsContext2D();
    }

    /**
     * Initialize a new game
     * @return BorderPane consisting of HBox of labels on top, blank canvas in center and HBox of Buttons at bottom
     */
    public BorderPane init() {
        changeId();
        bp = new BorderPane();
        setUpBorderPane();
        return bp;
    }

    /**
     * Actually begin the game after difficulty has been chosen -> set bp center to actual canvas of the game, initialize
     * labels with right values, remove difficulty buttons, initialize required global variables, start painting, all
     * parameters are based on game difficulty
     * @param gold amount of gold that the player gets
     * @param threatLevel threat level of monsters at the start of game
     * @param threatAcceleration speed at which monster threat will be increasing
     * @param resistance resistance against towers that all monsters get
     */
    public void begin(int gold, int threatLevel, int threatAcceleration, double resistance) {
        this.resistance = resistance;

        pg = new Board();
        pg.setFocusTraversable(true);
        bp.setCenter(pg);

        HBox bottom = new HBox(new Buttons.RestartButton(), new Buttons.ExitButton());
        bottom.setAlignment(Pos.CENTER);
        bp.setBottom(bottom);

        phase = Phase.FIGHTING;
        this.gold = gold;
        goldLabel.setText(String.valueOf(this.gold));
        level = 1;
        levelLabel.setText(level + "/" + VICTORY_LEVEL);
        health = 100;
        healthLabel.setText(health + "%");

        TileType[][] map = generateMap();
        tiles = new Tile[h/TILE_SIZE][w/TILE_SIZE];
        for (int j = 0; j*TILE_SIZE < w; j++) {
            for (int i = 0; i*TILE_SIZE < h; i++) {
                switch (map[i][j]) {
                    case PATH_TILE -> tiles[i][j] = new PathTile(j*TILE_SIZE, i*TILE_SIZE, TILE_SIZE, pg.getGraphicsContext2D());
                    case TOWER_TILE -> tiles[i][j] = new TowerTile(j*TILE_SIZE, i*TILE_SIZE, TILE_SIZE, pg.getGraphicsContext2D());
                    case DEFAULT_TILE -> tiles[i][j] = new DefaultTile(j*TILE_SIZE, i*TILE_SIZE, TILE_SIZE, pg.getGraphicsContext2D());
                }
            }
        }
        List<PathTile> tilePath = new ArrayList<>();
        for (Pair<Integer, Integer> i : path) {
            tilePath.add((PathTile) tiles[i.getValue()][i.getKey()]);
        }
        monsters = Collections.synchronizedList(new ArrayList<>());
        MonsterHandler.getInstance().init(tilePath, monsters, pg.getGraphicsContext2D(), threatLevel, threatAcceleration);
        counter = new Counter(5, () -> {
            phase = Phase.FIGHTING;
            MonsterHandler.getInstance().nextLevel();
            counter = null;
        });
        paintTimeline = new Timeline(new KeyFrame(Duration.millis(PAINT_DELAY), e -> {
            pg.paint();
        }));
        paintTimeline.setCycleCount(Animation.INDEFINITE);
        paintTimeline.play();
    }

    /**
     * restart the game -> generate new id and display menu where player can choose difficulty
     * @param textType -> what text will be displayed, based on whether player won, lost or pressed restart button
     */
    public void restart(GameEndText textType) {
        changeId();
        if (paintTimeline != null) {
            paintTimeline.stop();
        }
        GraphicsContext gc = setUpBorderPane();
        if (textType != GameEndText.NONE) {
            gc.setFontSmoothingType(FontSmoothingType.LCD);
            gc.setFont(Font.font("Consolas", 50));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(Color.BLACK);
            gc.setStroke(Color.WHITE);
            gc.strokeText((textType == GameEndText.VICTORY) ? "You won!" : "You lost!", w/2.0, h/2.0);
            gc.fillText((textType == GameEndText.VICTORY) ? "You won!" : "You lost!", w/2.0, h/2.0);
        }
    }

    /**
     * A monster is killed, if all monsters are dead and no others need to be summoned building phase begins as well as
     * countdown towards the next fighting phase, player is also rewarded for the kill (if he killed it)
     * @param m the monster that died
     * @param killedByPlayer was it killed by player or has it arrived at the end of path?
     */
    public void killMonster(Monster m, boolean killedByPlayer) {
        if (phase == Phase.FIGHTING) {
            if (killedByPlayer) {
                try {
                    reward(m.getClass().getDeclaredField("KILL_REWARD").getInt(null));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            monsters.remove(m);
            if (monsters.size() == 0 && MonsterHandler.getInstance().haveAllMonstersBeenSummoned()) {
                if (level == VICTORY_LEVEL) {
                    restart(GameEndText.VICTORY);
                }
                phase = Phase.BUILDING;
                level++;
                levelLabel.setText(level + "/" + VICTORY_LEVEL);
                counter = new Counter(5, () -> {
                    phase = Phase.FIGHTING;
                    MonsterHandler.getInstance().nextLevel();
                    counter = null;
                });
            }
        }
    }

    /**
     * buy a tower or upgrade
     * @param sum how much it costs
     * @return true if purchase was successful
     */
    public boolean buy(int sum) {
        if (sum <= gold) {
            gold -= sum;
            goldLabel.setText(String.valueOf(gold));
            return true;
        } else {
            showNotEnoughGold = true;
            new Timeline(new KeyFrame(Duration.millis(1500), e -> showNotEnoughGold = false)).play();
        }
        return false;
    }

    /**
     * reward player for killing a monster
     * @param sum how much to reward
     */
    public void reward(int sum) {
        gold += sum;
        goldLabel.setText(String.valueOf(gold));
    }

    /**
     * monster has successfully arrived at the end of its path and dealt damage to player
     * @param damage how much damage was dealt
     */
    public void damagePlayer(int damage) {
        health -= damage;
        healthLabel.setText(health+"%");
        if (health <= 0) {
            restart(GameEndText.LOSS);
        }
    }

    /**
     * paint text at specific location in canvas
     * @param text text to paint
     * @param x coordinate
     * @param y coordinate
     */
    public void paintText(String text, double x, double y) {
        GraphicsContext gc = pg.getGraphicsContext2D();
        gc.setFontSmoothingType(FontSmoothingType.LCD);
        gc.setFont(Font.font("Consolas", 10));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.WHITE);
        gc.strokeText(text, x, y);
        gc.fillText(text, x, y);
    }

    /**
     * paint text at specific location in canvas, this time it will be in a box
     * @param text text to paint
     * @param x coordinate
     * @param y coordinate
     */
    public void paintBorderedText(String text, double x, double y) {
        GraphicsContext gc = pg.getGraphicsContext2D();
        Text t = new Text(text);
        t.setFont(Font.font("Consolas", 20));
        gc.setFontSmoothingType(FontSmoothingType.LCD);
        gc.setFont(Font.font("Consolas", 20));
        double w = t.getLayoutBounds().getWidth(), h = t.getLayoutBounds().getHeight();
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.WHITE);
        gc.strokeRect(x-w/2, y-h/2, w, h);
        gc.fillRect(x-w/2, y-h/2, w, h);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.WHITE);
        gc.strokeText(text, x, y);
        gc.fillText(text, x, y);
    }

    /**
     * a empty canvas
     */
    class BlankBoard extends Canvas {
        BlankBoard() {
            setWidth(w);
            setHeight(h);
        }
    }

    /**
     * Game canvas
     */
    class Board extends Canvas {
        Board() {
            setWidth(w);
            setHeight(h);
            Tooltip.getInstance().init(getGraphicsContext2D());

            setOnMouseMoved(e -> {
                if (chooser != null) {
                    chooser.checkForTooltip(e.getX(), e.getY());
                }
            });

            setOnMouseClicked(e -> {
                if (chooser != null) {
                    if (chooser.choose(e.getX(), e.getY())) {
                        chooser = null;
                        return;
                    }
                    chooser = null;
                }
                Tile t;
                try {
                    t = tiles[(int) Math.floor(e.getY() / TILE_SIZE)][(int) Math.floor(e.getX() / TILE_SIZE)];
                } catch (IndexOutOfBoundsException ex) {
                    return;
                }
                if (t instanceof TowerTile) {
                    if (((TowerTile) t).getTower() == null) {
                        if (t.getY() < 1) {
                            chooser = new TowerChooser(t.getX() + t.getSize() / 2, t.getY() + t.getSize() + 40, getGraphicsContext2D(), (TowerTile) t, monsters);
                        } else {
                            chooser = new TowerChooser(t.getX() + t.getSize() / 2, t.getY() - 40, getGraphicsContext2D(), (TowerTile) t, monsters);
                        }
                    } else {
                        if (t.getY() < 1) {
                            chooser = new TowerActionChooser(t.getX() + t.getSize() / 2, t.getY() + t.getSize() + 40, getGraphicsContext2D(), ((TowerTile) t).getTower());
                        } else {
                            chooser = new TowerActionChooser(t.getX() + t.getSize() / 2, t.getY() - 40, getGraphicsContext2D(), ((TowerTile) t).getTower());
                        }
                    }
                }
            });
        }

        /**
         * Paint tiles (and towers, projectiles), monsters, chooser if one is present, tooltip if one is present, counter
         * if present and text informing player that he does not have enough gold if such text is required
         */
        public void paint() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.setFill(Color.GREEN);
            gc.fillRect(0, 0, w, h);
            for (Tile[] tt : tiles) {
                for (Tile t : tt) {
                    t.paint();
                }
            }
            for (Monster m : monsters) {
                m.paint();
            }
            if (chooser != null) {
                chooser.paint();
            }
            if (Tooltip.getInstance().isShowing()) {
                if (chooser == null) {
                    Tooltip.getInstance().hide();
                } else {
                    Tooltip.getInstance().paint();
                }
            }
            if (counter != null) {
                counter.paint();
            }
            if (showNotEnoughGold) {
                paintBorderedText("Not enough gold!", w/2.0, h/2.0);
            }
        }
    }

}
