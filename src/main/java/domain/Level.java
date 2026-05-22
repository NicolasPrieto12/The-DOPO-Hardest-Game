package domain;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un nivel del juego.
 * Contiene el tablero, los enemigos básicos, los enemigos patrulleros,
 * las monedas normales, las monedas de skin y las zonas de checkpoint.
 * Es responsable de actualizar y renderizar todos sus elementos.
 */
public class Level {

    /** Número identificador del nivel. */
    private final int levelNumber;

    /** Tablero que define el espacio físico del nivel. */
    private final Board board;

    /** Lista de enemigos básicos del nivel. */
    private final List<Enemy> enemies;

    /** Lista de enemigos patrulleros del nivel. */
    private final List<PatrolEnemy> patrolEnemies;

    /** Lista de monedas normales del nivel. */
    private final List<Coin> coins;

    /** Lista de monedas de skin del nivel. */
    private final List<SkinCoin> skinCoins;

    /** Lista de zonas de checkpoint del nivel. */
    private final List<CheckpointZone> checkpoints;

    /** Lista de enemigos deslizadores verticales. */
    private final List<SliderEnemy> sliderEnemies;

    /** Lista de enemigos acelerados. */
    private final List<AcceleratedEnemy> acceleratedEnemies;

    /** Lista de bombas estáticas. */
    private final List<Bomb> bombs;

    /** Moneda verde (Clyde). Puede ser null si el nivel no tiene. */
    private final GreenCoin greenCoin;

    /** Lista de fuentes de vida. */
    private final List<LifeSource> lifeSources;

    /**
     * Crea un nivel completo con todos sus elementos.
     *
     * @param levelNumber    Número identificador del nivel.
     * @param board          Tablero con zonas y paredes.
     * @param enemies        Lista de enemigos básicos.
     * @param patrolEnemies  Lista de enemigos patrulleros.
     * @param coins          Lista de monedas normales.
     * @param skinCoins      Lista de monedas de skin.
     * @param checkpoints    Lista de zonas de checkpoint.
     */
    public Level(int levelNumber, Board board,
                 List<Enemy> enemies, List<PatrolEnemy> patrolEnemies,
                 List<Coin> coins, List<SkinCoin> skinCoins,
                 List<CheckpointZone> checkpoints) {
        this(levelNumber, board, enemies, patrolEnemies, coins, skinCoins, checkpoints,
             new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);
    }

    /**
     * Crea un nivel completo con todos sus elementos incluyendo los nuevos tipos.
     */
    public Level(int levelNumber, Board board,
                 List<Enemy> enemies, List<PatrolEnemy> patrolEnemies,
                 List<Coin> coins, List<SkinCoin> skinCoins,
                 List<CheckpointZone> checkpoints,
                 List<SliderEnemy> sliderEnemies, List<AcceleratedEnemy> acceleratedEnemies,
                 List<Bomb> bombs, GreenCoin greenCoin) {
        this(levelNumber, board, enemies, patrolEnemies, coins, skinCoins, checkpoints,
             sliderEnemies, acceleratedEnemies, bombs, greenCoin, new ArrayList<>());
    }

    public Level(int levelNumber, Board board,
                 List<Enemy> enemies, List<PatrolEnemy> patrolEnemies,
                 List<Coin> coins, List<SkinCoin> skinCoins,
                 List<CheckpointZone> checkpoints,
                 List<SliderEnemy> sliderEnemies, List<AcceleratedEnemy> acceleratedEnemies,
                 List<Bomb> bombs, GreenCoin greenCoin, List<LifeSource> lifeSources) {
        this.levelNumber         = levelNumber;
        this.board               = board;
        this.enemies             = enemies;
        this.patrolEnemies       = patrolEnemies;
        this.coins               = coins;
        this.skinCoins           = skinCoins;
        this.checkpoints         = checkpoints;
        this.sliderEnemies       = sliderEnemies;
        this.acceleratedEnemies  = acceleratedEnemies;
        this.bombs               = bombs;
        this.greenCoin           = greenCoin;
        this.lifeSources         = lifeSources;
    }

    /**
     * Constructor simplificado sin patrulleros, skinCoins ni checkpoints.
     * Útil para niveles básicos.
     *
     * @param levelNumber Número identificador del nivel.
     * @param board       Tablero con zonas y paredes.
     * @param enemies     Lista de enemigos básicos.
     * @param coins       Lista de monedas normales.
     */
    public Level(int levelNumber, Board board, List<Enemy> enemies, List<Coin> coins) {
        this(levelNumber, board, enemies, new ArrayList<>(), coins, new ArrayList<>(), new ArrayList<>());
    }

    /** Actualiza todos los enemigos y verifica checkpoints para cada jugador. */
    public void update() {
        for (Enemy e : enemies)                    e.move();
        for (PatrolEnemy e : patrolEnemies)        e.move();
        for (SliderEnemy e : sliderEnemies)        e.move();
        for (AcceleratedEnemy e : acceleratedEnemies) e.move();
    }

    /**
     * Verifica y activa checkpoints para el jugador dado.
     *
     * @param player El jugador a verificar contra los checkpoints.
     */
    public void updateCheckpoints(Player player) {
        for (CheckpointZone cp : checkpoints) cp.checkAndActivate(player);
    }

    /**
     * Verifica si el nivel está completado (todas las monedas normales recogidas).
     *
     * @return true si todas las monedas normales están recogidas.
     */
    public boolean isCompleted() {
        return !coins.isEmpty() && coins.stream().allMatch(Coin::isCollected);
    }

    /**
     * Reinicia el nivel: reposiciona al jugador en el inicio y restablece
     * monedas y checkpoints. Las monedas ya recogidas antes del checkpoint
     * se mantienen si el jugador tiene checkpoint activo.
     *
     * @param player El jugador a reposicionar.
     */
    public void reset(Player player) {
        if (player.getCheckpointX() >= 0) {
            player.respawnAtCheckpoint();
        } else {
            board.getStartZone().resetPlayer(player);
            coins.forEach(Coin::reset);
            skinCoins.forEach(SkinCoin::reset);
            if (greenCoin != null) greenCoin.reset();
            lifeSources.forEach(LifeSource::reset);
        }
        player.applyType(PlayerType.RED);
        player.grantRespawnInvincibility();
    }

    /**
     * Reinicio completo del nivel, ignorando checkpoints.
     * Se usa al reiniciar la partida desde el botón reiniciar.
     *
     * @param player El jugador a reposicionar.
     */
    public void fullReset(Player player) {
        board.getStartZone().resetPlayer(player);
        coins.forEach(Coin::reset);
        skinCoins.forEach(SkinCoin::reset);
        checkpoints.forEach(CheckpointZone::reset);
        if (greenCoin != null) greenCoin.reset();
        lifeSources.forEach(LifeSource::reset);
        player.resetCheckpoint();
        player.applyType(PlayerType.RED);
    }

    /**
     * Dibuja en pantalla el tablero, checkpoints, monedas, skinCoins y enemigos.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    public void render(Graphics g) {
        board.render(g);
        for (CheckpointZone cp : checkpoints) cp.render(g);
        for (Bomb b : bombs)                  b.render(g);
        for (LifeSource ls : lifeSources)     ls.render(g);
        if (greenCoin != null)                greenCoin.render(g);
        for (Coin c : coins)                  c.render(g);
        for (SkinCoin sc : skinCoins)         sc.render(g);
        for (Enemy e : enemies)               e.render(g);
        for (PatrolEnemy e : patrolEnemies)   e.render(g);
        for (SliderEnemy e : sliderEnemies)   e.render(g);
        for (AcceleratedEnemy e : acceleratedEnemies) e.render(g);
    }

    /** @return El tablero del nivel. */
    public Board getBoard()                       { return board; }

    /** @return La lista de enemigos básicos. */
    public List<Enemy> getEnemies()               { return enemies; }

    /** @return La lista de enemigos patrulleros. */
    public List<PatrolEnemy> getPatrolEnemies()   { return patrolEnemies; }

    /** @return La lista de monedas normales. */
    public List<Coin> getCoins()                  { return coins; }

    /** @return La lista de monedas de skin. */
    public List<SkinCoin> getSkinCoins()          { return skinCoins; }

    /** @return La lista de checkpoints. */
    public List<CheckpointZone> getCheckpoints()  { return checkpoints; }

    /** @return La lista de enemigos deslizadores verticales. */
    public List<SliderEnemy> getSliderEnemies()   { return sliderEnemies; }

    /** @return La lista de enemigos acelerados. */
    public List<AcceleratedEnemy> getAcceleratedEnemies() { return acceleratedEnemies; }

    /** @return La lista de bombas. */
    public List<Bomb> getBombs()                  { return bombs; }

    /** @return La moneda verde, o null si no hay. */
    public GreenCoin getGreenCoin()               { return greenCoin; }

    /** @return La lista de fuentes de vida. */
    public List<LifeSource> getLifeSources()      { return lifeSources; }

    /** @return El número identificador del nivel. */
    public int getLevelNumber()                   { return levelNumber; }
}
