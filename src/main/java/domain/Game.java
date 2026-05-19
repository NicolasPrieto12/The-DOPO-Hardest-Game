package domain;

import java.util.List;

/**
 * Clase principal del juego modo un jugador.
 * Implementa el patrón Singleton.
 * Coordina estado, tiempo, colisiones, checkpoints y progresión de niveles.
 */
public class Game {

    /** Tiempo límite por nivel en segundos (3 minutos). */
    private static final int TIME_LIMIT = 180;

    /** Instancia única del juego (patrón Singleton). */
    private static Game instance;

    /** Índice del nivel actual. */
    private int currentLevelIndex;

    /** Contador de muertes del jugador. */
    private int deaths;

    /** Estado actual del juego. Ver {@link GameState}. */
    private String state;

    /** El jugador de la partida. */
    private final Player player;

    /** Lista de niveles de la partida. */
    private final List<Level> levels;

    /** Contador de ticks, se reinicia cada 60 (1 segundo a ~60fps). */
    private int tickCount;

    /** Segundos restantes para completar el nivel. */
    private int secondsLeft;

    private Game(Player player, List<Level> levels) {
        this.player            = player;
        this.levels            = levels;
        this.currentLevelIndex = 0;
        this.deaths            = 0;
        this.state             = GameState.PLAYING;
        this.tickCount         = 0;
        this.secondsLeft       = TIME_LIMIT;
    }

    /** Retorna la instancia única, creándola si no existe. */
    public static Game getInstance(Player player, List<Level> levels) {
        if (instance == null) instance = new Game(player, levels);
        return instance;
    }

    /** Retorna la instancia existente sin crear una nueva. */
    public static Game getInstance() { return instance; }

    /** Destruye la instancia actual. */
    public static void resetInstance() { instance = null; }

    /** Inicia el juego colocando al jugador en la zona de inicio. */
    public void start() {
        state = GameState.PLAYING;
        player.setWalls(getCurrentLevel().getBoard().getWalls());
        getCurrentLevel().getBoard().getStartZone().resetPlayer(player);
    }

    /**
     * Actualiza el estado del juego en cada tick.
     * Gestiona tiempo, movimiento, colisiones, checkpoints y victoria.
     */
    public void update() {
        if (!state.equals(GameState.PLAYING)) return;

        tickCount++;
        if (tickCount >= 60) {
            tickCount = 0;
            secondsLeft--;
            if (secondsLeft <= 0) {
                secondsLeft = 0;
                state = GameState.TIMEOUT;
                return;
            }
        }

        player.move();
        Level level = getCurrentLevel();
        level.update();
        level.updateCheckpoints(player);

        for (Coin coin : level.getCoins()) {
            if (coin.collidesWith(player)) coin.collect();
        }

        for (SkinCoin sc : level.getSkinCoins()) {
            if (sc.collidesWith(player)) sc.collect(player);
        }

        for (Enemy enemy : level.getEnemies()) {
            if (enemy.collidesWith(player)) { checkDeath(); return; }
        }

        for (PatrolEnemy enemy : level.getPatrolEnemies()) {
            if (enemy.collidesWith(player)) { checkDeath(); return; }
        }

        if (checkWin()) nextLevel();
    }

    /**
     * Verifica si el jugador completó el nivel.
     *
     * @return true si todas las monedas recogidas y jugador en EndZone.
     */
    protected boolean checkWin() {
        Level level = getCurrentLevel();
        return level.isCompleted() && level.getBoard().getEndZone().checkLevelComplete(player);
    }

    /**
     * Registra la muerte, incrementa contador y reinicia desde checkpoint si existe.
     *
     * @return true siempre.
     */
    protected boolean checkDeath() {
        deaths++;
        getCurrentLevel().reset(player);
        return true;
    }

    /** Avanza al siguiente nivel o establece WIN si era el último. */
    public void nextLevel() {
        if (currentLevelIndex + 1 < levels.size()) {
            currentLevelIndex++;
            secondsLeft = TIME_LIMIT;
            tickCount   = 0;
            player.setWalls(getCurrentLevel().getBoard().getWalls());
            getCurrentLevel().getBoard().getStartZone().resetPlayer(player);
            player.applyType(PlayerType.RED);
            player.resetCheckpoint();
        } else {
            state = GameState.WIN;
        }
    }

    /** Reinicia completamente la partida. */
    public void restart() {
        deaths             = 0;
        currentLevelIndex  = 0;
        state              = GameState.PLAYING;
        tickCount          = 0;
        secondsLeft        = TIME_LIMIT;
        player.setWalls(getCurrentLevel().getBoard().getWalls());
        getCurrentLevel().fullReset(player);
    }

    /** Alterna entre PLAYING y PAUSED. */
    public void pause() {
        if (state.equals(GameState.PLAYING))     state = GameState.PAUSED;
        else if (state.equals(GameState.PAUSED)) state = GameState.PLAYING;
    }

    /** @return El nivel actual. */
    public Level getCurrentLevel()  { return levels.get(currentLevelIndex); }

    /** @return El jugador. */
    public Player getPlayer()       { return player; }

    /** @return El número de muertes. */
    public int getDeaths()          { return deaths; }

    /** @return El estado actual. */
    public String getState()        { return state; }

    /** @return Los segundos restantes. */
    public int getSecondsLeft()     { return secondsLeft; }

    /** @return El índice del nivel actual. */
    public int getCurrentLevelIndex() { return currentLevelIndex; }

    /** Establece el estado del juego (usado al cargar partida). */
    public void setState(String state)              { this.state = state; }

    /** Establece el número de muertes (usado al cargar partida). */
    public void setDeaths(int deaths)               { this.deaths = deaths; }

    /** Establece el nivel actual (usado al cargar partida). */
    public void setCurrentLevelIndex(int idx)       { this.currentLevelIndex = idx; }

    /** Establece los segundos restantes (usado al cargar partida). */
    public void setSecondsLeft(int seconds)         { this.secondsLeft = seconds; }
}
