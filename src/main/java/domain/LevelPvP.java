package domain;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Nivel especial para el modo PvP.
 * Usa {@link BoardPvP} con zonas de inicio y final opuestas para cada jugador.
 * Jugador 1 inicia izquierda → gana llegando a la derecha.
 * Jugador 2 inicia derecha  → gana llegando a la izquierda.
 */
public class LevelPvP {

    /** Número identificador del nivel. */
    private final int levelNumber;

    /** Tablero PvP con zonas opuestas. */
    private final BoardPvP board;

    /** Lista de enemigos básicos. */
    private final List<Enemy> enemies;

    /** Lista de enemigos patrulleros. */
    private final List<PatrolEnemy> patrolEnemies;

    /** Lista de monedas normales compartidas. */
    private final List<Coin> coins;

    /** Lista de monedas de skin. */
    private final List<SkinCoin> skinCoins;

    /** Lista de checkpoints del nivel. */
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
     * Crea un nivel PvP completo.
     */
    public LevelPvP(int levelNumber, BoardPvP board,
                    List<Enemy> enemies, List<PatrolEnemy> patrolEnemies,
                    List<Coin> coins, List<SkinCoin> skinCoins) {
        this(levelNumber, board, enemies, patrolEnemies, coins, skinCoins, new ArrayList<>());
    }

    /**
     * Crea un nivel PvP completo con checkpoints.
     */
    public LevelPvP(int levelNumber, BoardPvP board,
                    List<Enemy> enemies, List<PatrolEnemy> patrolEnemies,
                    List<Coin> coins, List<SkinCoin> skinCoins,
                    List<CheckpointZone> checkpoints) {
        this(levelNumber, board, enemies, patrolEnemies, coins, skinCoins, checkpoints,
             new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);
    }

    /**
     * Crea un nivel PvP completo con todos los elementos nuevos.
     */
    public LevelPvP(int levelNumber, BoardPvP board,
                    List<Enemy> enemies, List<PatrolEnemy> patrolEnemies,
                    List<Coin> coins, List<SkinCoin> skinCoins,
                    List<CheckpointZone> checkpoints,
                    List<SliderEnemy> sliderEnemies, List<AcceleratedEnemy> acceleratedEnemies,
                    List<Bomb> bombs, GreenCoin greenCoin) {
        this(levelNumber, board, enemies, patrolEnemies, coins, skinCoins, checkpoints,
             sliderEnemies, acceleratedEnemies, bombs, greenCoin, new ArrayList<>());
    }

    public LevelPvP(int levelNumber, BoardPvP board,
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

    /** Constructor simplificado sin patrulleros ni skinCoins. */
    public LevelPvP(int levelNumber, BoardPvP board,
                    List<Enemy> enemies, List<Coin> coins) {
        this(levelNumber, board, enemies, new ArrayList<>(), coins, new ArrayList<>());
    }

    /** Actualiza todos los enemigos y checkpoints. */
    public void update() {
        for (Enemy e : enemies)                       e.move();
        for (PatrolEnemy e : patrolEnemies)           e.move();
        for (SliderEnemy e : sliderEnemies)           e.move();
        for (AcceleratedEnemy e : acceleratedEnemies) e.move();
    }

    /** Verifica y activa checkpoints para el jugador dado. */
    public void updateCheckpoints(Player player) {
        for (CheckpointZone cp : checkpoints) cp.checkAndActivate(player);
    }

    /**
     * Verifica si todas las monedas normales fueron recogidas.
     *
     * @return true si todas las monedas están recogidas.
     */
    public boolean isCompleted() {
        return !coins.isEmpty() && coins.stream().allMatch(Coin::isCollected);
    }

    /**
     * Reinicia solo la posición del jugador sin resetear monedas.
     * Usado en modo PvM para que la muerte de uno no afecte las monedas del otro.
     */
    public void resetPositionOnly(Player player, StartZone startZone) {
        if (player.getCheckpointX() >= 0) {
            player.respawnAtCheckpoint();
        } else {
            startZone.resetPlayer(player);
        }
        player.applyType(PlayerType.RED);
        player.grantRespawnInvincibility();
    }

    /**
     * Reinicia solo la posición del jugador al checkpoint o al inicio.
     * NO resetea las monedas del nivel (cada jugador conserva las suyas).
     *
     * @param player    El jugador a reiniciar.
     * @param startZone La zona de inicio de ese jugador.
     */
    public void resetPlayer(Player player, StartZone startZone) {
        if (player.getCheckpointX() >= 0) {
            player.respawnAtCheckpoint();
        } else {
            startZone.resetPlayer(player);
        }
        player.applyType(PlayerType.RED);
        player.grantRespawnInvincibility();
    }

    /**
     * Reinicio completo del nivel para un jugador.
     * Resetea posición, monedas, checkpoints y tipo.
     * Solo se usa al reiniciar toda la partida.
     *
     * @param player    El jugador a reiniciar.
     * @param startZone La zona de inicio de ese jugador.
     */
    public void fullReset(Player player, StartZone startZone) {
        startZone.resetPlayer(player);
        coins.forEach(Coin::reset);
        skinCoins.forEach(SkinCoin::reset);
        checkpoints.forEach(CheckpointZone::reset);
        if (greenCoin != null) greenCoin.reset();
        lifeSources.forEach(LifeSource::reset);
        player.resetCheckpoint();
        player.applyType(PlayerType.RED);
    }

    /**
     * Dibuja el tablero, monedas y enemigos.
     *
     * @param g El objeto Graphics.
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

    /** @return El tablero PvP. */
    public BoardPvP getBoard()                     { return board; }

    /** @return Lista de enemigos básicos. */
    public List<Enemy> getEnemies()                { return enemies; }

    /** @return Lista de enemigos patrulleros. */
    public List<PatrolEnemy> getPatrolEnemies()    { return patrolEnemies; }

    /** @return Lista de monedas normales. */
    public List<Coin> getCoins()                   { return coins; }

    /** @return Lista de monedas de skin. */
    public List<SkinCoin> getSkinCoins()           { return skinCoins; }

    /** @return Número del nivel. */
    public int getLevelNumber()                    { return levelNumber; }

    /** @return Lista de enemigos deslizadores verticales. */
    public List<SliderEnemy> getSliderEnemies()    { return sliderEnemies; }

    /** @return Lista de enemigos acelerados. */
    public List<AcceleratedEnemy> getAcceleratedEnemies() { return acceleratedEnemies; }

    /** @return Lista de bombas. */
    public List<Bomb> getBombs()                   { return bombs; }

    /** @return La moneda verde, o null si no hay. */
    public GreenCoin getGreenCoin()                { return greenCoin; }

    /** @return La lista de fuentes de vida. */
    public List<LifeSource> getLifeSources()       { return lifeSources; }
}
