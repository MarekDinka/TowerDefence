package sk.uniba.fmph.dinka2.towerdefence;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import sk.uniba.fmph.dinka2.towerdefence.monsters.*;
import sk.uniba.fmph.dinka2.towerdefence.tiles.PathTile;

import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Singleton class that handles deciding what monsters to create and summoning them
 */
public class MonsterHandler {
    private final int RGB_MONSTER_THREAT_LEVEL = 8, STRONG_MONSTER_THREAT_LEVEL = 20, WEAK_MONSTER_THREAT_LEVEL = 1;

    private int gameId;

    private List<PathTile> tilePath;
    private List<Monster> monsters;
    private final Stack<Monster> monstersToSummon = new Stack<>();
    private int threatLevel, threadAcceleration;
    private GraphicsContext gc;
    private Random rnd = new Random();
    private Timeline summoner;

    private static final MonsterHandler INSTANCE = new MonsterHandler();
    private MonsterHandler() {}
    public static MonsterHandler getInstance() {return INSTANCE;}

    /**
     * initialize global variables
     * @param tilePath path which the monsters will take
     * @param monsters reference to synchronized global list of all live monsters
     * @param gc GraphicsContext
     * @param threatLevel starting threat level that will decide what monsters to summon
     * @param threadAcceleration by how much will the threat increase
     */
    public void init(List<PathTile> tilePath, List<Monster> monsters, GraphicsContext gc, int threatLevel, int threadAcceleration) {
        gameId = Game.getInstance().getId();
        this.tilePath = tilePath;
        this.monsters = monsters;
        this.gc = gc;
        this.threatLevel = threatLevel;
        this.threadAcceleration = threadAcceleration;
    }

    /**
     * @return true if there are no monsters left to summon
     */
    public boolean haveAllMonstersBeenSummoned() {return monstersToSummon.empty();}

    private void generateMonsterWave(int threatLevel) {
        if (threatLevel <= 0) {
            return;
        }
        int bound = (threatLevel >= STRONG_MONSTER_THREAT_LEVEL) ? 5 : (threatLevel >= RGB_MONSTER_THREAT_LEVEL) ? 4 : 1;
        int j = rnd.nextInt(0, bound);
        switch (j) {
            case 0 -> {monstersToSummon.push(new WeakMonster(tilePath, gc)); threatLevel -= WEAK_MONSTER_THREAT_LEVEL;}
            case 1 -> {monstersToSummon.push(new RedMonster(tilePath, gc)); threatLevel -= RGB_MONSTER_THREAT_LEVEL;}
            case 2 -> {monstersToSummon.push(new GreenMonster(tilePath, gc)); threatLevel -= RGB_MONSTER_THREAT_LEVEL;}
            case 3 -> {monstersToSummon.push(new BlueMonster(tilePath, gc)); threatLevel -= RGB_MONSTER_THREAT_LEVEL;}
            case 4 -> {monstersToSummon.push(new StrongMonster(tilePath, gc)); threatLevel -= STRONG_MONSTER_THREAT_LEVEL;}
        }
        if (threatLevel > 0) {
            generateMonsterWave(threatLevel);
        }
    }

    /**
     * generate new wave of monsters and start summoning them
     */
    public void nextLevel() {
        if (monsters.size() > 0 || !haveAllMonstersBeenSummoned() || gameId != Game.getInstance().getId()) {
            return;
        }
        generateMonsterWave(threatLevel);
        threatLevel += threadAcceleration;
        summoner = new Timeline(new KeyFrame(Duration.millis(Math.random()*((threatLevel*3 < 1800) ? 2000 - threatLevel*3 : 200)), e -> {
            if (monstersToSummon.empty() || gameId != Game.getInstance().getId()) {
                summoner.stop();
                return;
            }
            Monster m = monstersToSummon.pop();
            m.begin();
            monsters.add(m);
        }));
        summoner.setCycleCount(Timeline.INDEFINITE);
        summoner.play();
    }
}
