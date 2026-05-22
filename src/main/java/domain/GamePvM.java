package domain;

import java.util.List;

/**
 * Controlador del modo Player vs Machine (PvM).
 * El jugador humano usa flechas, inicia en la izquierda y gana llegando a la derecha.
 * La máquina inicia en la derecha y gana llegando a la izquierda.
 * El primero en llegar a su zona contraria gana el nivel.
 *
 * <p>La máquina soporta dos perfiles de comportamiento:</p>
 * <ul>
 *   <li>{@link MachineProfile#RANDOM}: movimiento aleatorio con tendencia al objetivo</li>
 *   <li>{@link MachineProfile#EXPERT}: siempre elige la dirección libre más cercana al objetivo</li>
 * </ul>
 *
 * <p>Las monedas son compartidas pero la muerte de un jugador NO reinicia las monedas del otro.</p>
 */
public class GamePvM {

    /** Tiempo límite por nivel en segundos (3 minutos). */
    private static final int TIME_LIMIT = 180;

    /** Jugador humano (flechas, inicia izquierda). */
    private final Player player;

    /** Jugador máquina (inicia derecha). */
    private final MachinePlayer machine;

    /** Lista de niveles PvM. */
    private final List<LevelPvP> levels;

    /** Índice del nivel actual. */
    private int currentLevelIndex;

    /** Estado actual del juego. */
    private String state;

    /** Contador de muertes del jugador humano. */
    private int deathsPlayer;

    /** Contador de muertes de la máquina. */
    private int deathsMachine;

    /** Contador de ticks para el tiempo. */
    private int tickCount;

    /** Segundos restantes del nivel. */
    private int secondsLeft;

    /** Nombre del ganador del nivel actual. */
    private String winner = "";

    /**
     * Crea una partida PvM.
     *
     * @param player  El jugador humano.
     * @param machine La máquina.
     * @param levels  Lista de niveles.
     */
    public GamePvM(Player player, MachinePlayer machine, List<LevelPvP> levels) {
        this.player            = player;
        this.machine           = machine;
        this.levels            = levels;
        this.currentLevelIndex = 0;
        this.state             = GameState.PLAYING;
        this.deathsPlayer      = 0;
        this.deathsMachine     = 0;
        this.tickCount         = 0;
        this.secondsLeft       = TIME_LIMIT;
    }

    /** Inicia la partida posicionando jugador y máquina en sus zonas. */
    public void start() {
        state = GameState.PLAYING;
        LevelPvP level = getCurrentLevel();
        player.setWalls(level.getBoard().getWalls());
        machine.setWalls(level.getBoard().getWalls());
        level.getBoard().getStartZone1().resetPlayer(player);
        level.getBoard().getStartZone2().resetPlayer(machine);
    }

    /** Actualiza el estado del juego en cada tick. */
    public void update() {
        if (!state.equals(GameState.PLAYING)) return;

        tickCount++;
        if (tickCount >= 60) {
            tickCount = 0;
            secondsLeft--;
            if (secondsLeft <= 0) {
                secondsLeft = 0;
                state = GameState.TIMEOUT;
                winner = "Empate";
                return;
            }
        }

        LevelPvP level = getCurrentLevel();
        level.update();

        // Movimiento del jugador humano
        player.move();
        player.tickInvincibility();
        level.updateCheckpoints(player);
        handleCoins(level, player);
        handleSkinCoins(level, player);
        handleGreenCoin(level, player);
        handleLifeSources(level, player);
        if (checkEnemyCollision(level, player)) {
            if (!player.absorbHit()) {
                deathsPlayer++;
                level.resetPositionOnly(player, level.getBoard().getStartZone1());
            }
        }

        // Movimiento de la máquina con IA
        int targetX = level.getBoard().getEndZone2().getX() + level.getBoard().getEndZone2().getWidth()  / 2;
        int targetY = level.getBoard().getEndZone2().getY() + level.getBoard().getEndZone2().getHeight() / 2;
        machine.updateAI(level.getCoins(), level.getSkinCoins(), targetX, targetY);
        machine.tickInvincibility();
        level.updateCheckpoints(machine);
        handleCoins(level, machine);
        handleSkinCoins(level, machine);
        handleGreenCoin(level, machine);
        handleLifeSources(level, machine);
        if (checkEnemyCollision(level, machine)) {
            if (!machine.absorbHit()) {
                deathsMachine++;
                level.resetPositionOnly(machine, level.getBoard().getStartZone2());
            }
        }

        // Verificar victoria: el primero que llegue gana
        if (level.isCompleted() && level.getBoard().getEndZone1().checkLevelComplete(player)) {
            winner = "Jugador";
            advanceOrWin();
            return;
        }
        if (level.isCompleted() && level.getBoard().getEndZone2().checkLevelComplete(machine)) {
            winner = "Máquina";
            advanceOrWin();
        }
    }

    private void handleCoins(LevelPvP level, Player p) {
        for (Coin coin : level.getCoins())     if (coin.collidesWith(p)) coin.collect();
    }

    private void handleSkinCoins(LevelPvP level, Player p) {
        for (SkinCoin sc : level.getSkinCoins()) if (sc.collidesWith(p)) sc.collect(p);
    }

    private void handleGreenCoin(LevelPvP level, Player p) {
        if (level.getGreenCoin() != null && level.getGreenCoin().collidesWith(p)) {
            level.getGreenCoin().collect(p);
        }
    }

    private void handleLifeSources(LevelPvP level, Player p) {
        for (LifeSource ls : level.getLifeSources()) {
            if (ls.collidesWith(p)) ls.collect(p);
        }
    }

    private boolean checkEnemyCollision(LevelPvP level, Player p) {
        for (Bomb b : level.getBombs())                          if (b.collidesWith(p)) return true;
        for (Enemy e : level.getEnemies())                       if (e.collidesWith(p)) return true;
        for (PatrolEnemy e : level.getPatrolEnemies())           if (e.collidesWith(p)) return true;
        for (SliderEnemy e : level.getSliderEnemies())           if (e.collidesWith(p)) return true;
        for (AcceleratedEnemy e : level.getAcceleratedEnemies()) if (e.collidesWith(p)) return true;
        return false;
    }

    private void advanceOrWin() {
        if (currentLevelIndex + 1 < levels.size()) {
            currentLevelIndex++;
            secondsLeft = TIME_LIMIT;
            tickCount   = 0;
            winner      = "";
            LevelPvP next = getCurrentLevel();
            player.setWalls(next.getBoard().getWalls());
            machine.setWalls(next.getBoard().getWalls());
            next.getBoard().getStartZone1().resetPlayer(player);
            next.getBoard().getStartZone2().resetPlayer(machine);
            player.applyType(PlayerType.RED);
            machine.applyType(PlayerType.RED);
            player.resetCheckpoint();
            machine.resetCheckpoint();
        } else {
            state = GameState.WIN;
        }
    }

    /** Fuerza el avance al siguiente nivel (usado por el botón de saltar nivel). */
    public void skipLevel() { advanceOrWin(); }

    /** Reinicia solo el nivel actual. No cambia de nivel. */
    public void restart() {
        state         = GameState.PLAYING;
        deathsPlayer  = 0;
        deathsMachine = 0;
        tickCount     = 0;
        secondsLeft   = TIME_LIMIT;
        winner        = "";
        LevelPvP level = getCurrentLevel();
        player.setWalls(level.getBoard().getWalls());
        machine.setWalls(level.getBoard().getWalls());
        level.fullReset(player,  level.getBoard().getStartZone1());
        level.fullReset(machine, level.getBoard().getStartZone2());
    }

    /** Alterna entre PLAYING y PAUSED. */
    public void pause() {
        if (state.equals(GameState.PLAYING))     state = GameState.PAUSED;
        else if (state.equals(GameState.PAUSED)) state = GameState.PLAYING;
    }

    /** @return El nivel PvM actual. */
    public LevelPvP getCurrentLevel()    { return levels.get(currentLevelIndex); }

    /** @return El jugador humano. */
    public Player getPlayer()            { return player; }

    /** @return La máquina. */
    public MachinePlayer getMachine()    { return machine; }

    /** @return El estado actual. */
    public String getState()             { return state; }

    /** @return Muertes del jugador humano. */
    public int getDeathsPlayer()         { return deathsPlayer; }

    /** @return Muertes de la máquina. */
    public int getDeathsMachine()        { return deathsMachine; }

    /** @return Segundos restantes. */
    public int getSecondsLeft()          { return secondsLeft; }

    /** @return Nombre del ganador del nivel ("Jugador", "Máquina" o "Empate"). */
    public String getWinner()            { return winner; }

    /** @return Índice del nivel actual. */
    public int getCurrentLevelIndex()    { return currentLevelIndex; }
}
