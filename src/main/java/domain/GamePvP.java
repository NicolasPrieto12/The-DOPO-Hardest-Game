package domain;

import java.util.List;

/**
 * Controlador del modo Player vs Player (PvP).
 * Jugador 1 inicia en la izquierda y gana llegando a la derecha.
 * Jugador 2 inicia en la derecha y gana llegando a la izquierda.
 * Gana quien complete el nivel en menor tiempo.
 * Jugador 1 usa flechas, Jugador 2 usa WASD.
 *
 * <p>Las monedas son compartidas pero la muerte de un jugador NO reinicia las monedas del otro.</p>
 * <p>Ambos jugadores pueden estar en el tablero simultáneamente y se mueven de forma independiente.</p>
 * <p>El tiempo se mide desde el inicio del nivel hasta que el primer jugador llega a su EndZone.</p>
 */
public class GamePvP {

    /** Tiempo límite por nivel en segundos (3 minutos). */
    private static final int TIME_LIMIT = 180;

    /** Jugador 1 (flechas, inicia izquierda). */
    private final Player player1;

    /** Jugador 2 (WASD, inicia derecha). */
    private final Player player2;

    /** Lista de niveles PvP de la partida. */
    private final List<LevelPvP> levels;

    /** Índice del nivel actual. */
    private int currentLevelIndex;

    /** Estado actual del juego. */
    private String state;

    /** Contador de muertes del jugador 1. */
    private int deaths1;

    /** Contador de muertes del jugador 2. */
    private int deaths2;

    /** Contador de ticks para el tiempo. */
    private int tickCount;

    /** Segundos restantes del nivel. */
    private int secondsLeft;

    /** Tiempo en segundos que tardó el jugador 1 en completar el nivel. -1 si no terminó. */
    private int timePlayer1;

    /** Tiempo en segundos que tardó el jugador 2 en completar el nivel. -1 si no terminó. */
    private int timePlayer2;

    /** Indica si el jugador 1 ya completó el nivel actual. */
    private boolean player1Finished;

    /** Indica si el jugador 2 ya completó el nivel actual. */
    private boolean player2Finished;

    /**
     * Crea una partida PvP.
     *
     * @param player1 Jugador 1 (flechas, izquierda).
     * @param player2 Jugador 2 (WASD, derecha).
     * @param levels  Lista de niveles PvP.
     */
    public GamePvP(Player player1, Player player2, List<LevelPvP> levels) {
        this.player1           = player1;
        this.player2           = player2;
        this.levels            = levels;
        this.currentLevelIndex = 0;
        this.state             = GameState.PLAYING;
        this.deaths1           = 0;
        this.deaths2           = 0;
        this.tickCount         = 0;
        this.secondsLeft       = TIME_LIMIT;
        this.timePlayer1       = -1;
        this.timePlayer2       = -1;
        this.player1Finished   = false;
        this.player2Finished   = false;
    }

    /**
     * Inicia la partida: P1 en zona izquierda, P2 en zona derecha.
     */
    public void start() {
        state = GameState.PLAYING;
        LevelPvP level = getCurrentLevel();
        player1.setWalls(level.getBoard().getWalls());
        player2.setWalls(level.getBoard().getWalls());
        level.getBoard().getStartZone1().resetPlayer(player1);
        level.getBoard().getStartZone2().resetPlayer(player2);
    }

    /**
     * Actualiza el estado del juego en cada tick.
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

        LevelPvP level = getCurrentLevel();
        level.update();

        if (!player1Finished) {
            player1.move();
            player1.tickInvincibility();
            level.updateCheckpoints(player1);
            handleCoins(level, player1);
            handleSkinCoins(level, player1);
            handleGreenCoin(level, player1);
            handleLifeSources(level, player1);
            if (checkEnemyCollision(level, player1)) {
                if (!player1.absorbHit()) {
                    deaths1++;
                    level.resetPlayer(player1, level.getBoard().getStartZone1());
                }
            }
            if (level.isCompleted() && level.getBoard().getEndZone1().checkLevelComplete(player1)) {
                player1Finished = true;
                timePlayer1 = TIME_LIMIT - secondsLeft;
                advanceOrWin();
                return;
            }
        }

        if (!player2Finished) {
            player2.move();
            player2.tickInvincibility();
            level.updateCheckpoints(player2);
            handleCoins(level, player2);
            handleSkinCoins(level, player2);
            handleGreenCoin(level, player2);
            handleLifeSources(level, player2);
            if (checkEnemyCollision(level, player2)) {
                if (!player2.absorbHit()) {
                    deaths2++;
                    level.resetPlayer(player2, level.getBoard().getStartZone2());
                }
            }
            if (level.isCompleted() && level.getBoard().getEndZone2().checkLevelComplete(player2)) {
                player2Finished = true;
                timePlayer2 = TIME_LIMIT - secondsLeft;
                advanceOrWin();
                return;
            }
        }
    }

    private void handleCoins(LevelPvP level, Player player) {
        for (Coin coin : level.getCoins()) {
            if (coin.collidesWith(player)) coin.collect();
        }
    }

    private void handleSkinCoins(LevelPvP level, Player player) {
        for (SkinCoin sc : level.getSkinCoins()) {
            if (sc.collidesWith(player)) sc.collect(player);
        }
    }

    private void handleGreenCoin(LevelPvP level, Player player) {
        if (level.getGreenCoin() != null && level.getGreenCoin().collidesWith(player)) {
            level.getGreenCoin().collect(player);
        }
    }

    private void handleLifeSources(LevelPvP level, Player player) {
        for (LifeSource ls : level.getLifeSources()) {
            if (ls.collidesWith(player)) ls.collect(player);
        }
    }

    private boolean checkEnemyCollision(LevelPvP level, Player player) {
        for (Bomb b : level.getBombs())                        if (b.collidesWith(player)) return true;
        for (Enemy e : level.getEnemies())                     if (e.collidesWith(player)) return true;
        for (PatrolEnemy e : level.getPatrolEnemies())         if (e.collidesWith(player)) return true;
        for (SliderEnemy e : level.getSliderEnemies())         if (e.collidesWith(player)) return true;
        for (AcceleratedEnemy e : level.getAcceleratedEnemies()) if (e.collidesWith(player)) return true;
        return false;
    }

    private void advanceOrWin() {
        if (currentLevelIndex + 1 < levels.size()) {
            currentLevelIndex++;
            secondsLeft     = TIME_LIMIT;
            tickCount       = 0;
            player1Finished = false;
            player2Finished = false;
            timePlayer1     = -1;
            timePlayer2     = -1;
            LevelPvP next = getCurrentLevel();
            player1.setWalls(next.getBoard().getWalls());
            player2.setWalls(next.getBoard().getWalls());
            next.getBoard().getStartZone1().resetPlayer(player1);
            next.getBoard().getStartZone2().resetPlayer(player2);
            player1.applyType(PlayerType.RED);
            player2.applyType(PlayerType.RED);
            player1.resetCheckpoint();
            player2.resetCheckpoint();
        } else {
            state = GameState.WIN;
        }
    }

    /** Fuerza el avance al siguiente nivel (usado por el botón de saltar nivel). */
    public void skipLevel() { advanceOrWin(); }

    /** Retorna el nombre del ganador comparando tiempos.
     *
     * @return "Jugador 1", "Jugador 2" o "Empate".
     */
    public String getWinner() {
        if (player1Finished) return "Jugador 1";
        if (player2Finished) return "Jugador 2";
        if (timePlayer1 >= 0 && timePlayer2 >= 0) {
            if (timePlayer1 < timePlayer2) return "Jugador 1";
            if (timePlayer2 < timePlayer1) return "Jugador 2";
        }
        return "Empate";
    }

    /** Reinicia solo el nivel actual. No cambia de nivel. */
    public void restart() {
        state           = GameState.PLAYING;
        deaths1         = 0;
        deaths2         = 0;
        tickCount       = 0;
        secondsLeft     = TIME_LIMIT;
        timePlayer1     = -1;
        timePlayer2     = -1;
        player1Finished = false;
        player2Finished = false;
        LevelPvP level = getCurrentLevel();
        player1.setWalls(level.getBoard().getWalls());
        player2.setWalls(level.getBoard().getWalls());
        level.fullReset(player1, level.getBoard().getStartZone1());
        level.fullReset(player2, level.getBoard().getStartZone2());
    }

    /** Alterna entre PLAYING y PAUSED. */
    public void pause() {
        if (state.equals(GameState.PLAYING))     state = GameState.PAUSED;
        else if (state.equals(GameState.PAUSED)) state = GameState.PLAYING;
    }

    /** @return El nivel PvP actual. */
    public LevelPvP getCurrentLevel()     { return levels.get(currentLevelIndex); }

    /** @return El jugador 1. */
    public Player getPlayer1()            { return player1; }

    /** @return El jugador 2. */
    public Player getPlayer2()            { return player2; }

    /** @return El estado actual. */
    public String getState()              { return state; }

    /** @return Muertes del jugador 1. */
    public int getDeaths1()               { return deaths1; }

    /** @return Muertes del jugador 2. */
    public int getDeaths2()               { return deaths2; }

    /** @return Segundos restantes. */
    public int getSecondsLeft()           { return secondsLeft; }

    /** @return true si el jugador 1 terminó. */
    public boolean isPlayer1Finished()    { return player1Finished; }

    /** @return true si el jugador 2 terminó. */
    public boolean isPlayer2Finished()    { return player2Finished; }

    /** @return Índice del nivel actual. */
    public int getCurrentLevelIndex()     { return currentLevelIndex; }
}
