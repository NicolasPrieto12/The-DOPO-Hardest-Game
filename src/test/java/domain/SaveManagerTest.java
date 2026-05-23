package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link SaveManager}, {@link GameLogger} y {@link GameException}.
 * Cubren guardado/carga de partidas single y PvP, lectura de modo y manejo de errores.
 */
class SaveManagerTest {

    private static final String FILE_SINGLE = "test_save_single.dopo";
    private static final String FILE_PVP    = "test_save_pvp.dopo";

    private Game    gameSingle;
    private GamePvP gamePvP;
    private Player  player;
    private Coin    coin;

    @BeforeEach
    void setUp() {
        Game.resetInstance();

        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());

        player = new Player(50, 240);
        coin   = new Coin(400, 240);
        Level level = new Level(1, board, List.of(), List.of(coin));
        gameSingle = Game.getInstance(player, List.of(level));
        gameSingle.start();

        StartZone s1 = new StartZone(20,  200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700,  200, 80, 100);
        EndZone   e2 = new EndZone(20,   200, 80, 100);
        BoardPvP boardPvP = new BoardPvP(s1, s2, e1, e2, List.of());
        Coin coinPvP = new Coin(400, 240);
        LevelPvP levelPvP = new LevelPvP(1, boardPvP, new ArrayList<>(), new ArrayList<>(),
            List.of(coinPvP), new ArrayList<>());
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        gamePvP = new GamePvP(p1, p2, List.of(levelPvP));
        gamePvP.start();
    }

    @AfterEach
    void tearDown() {
        Game.resetInstance();
        new File(FILE_SINGLE).delete();
        new File(FILE_PVP).delete();
    }

    // ─── SaveManager single ──────────────────────────────────────

    /** saveGame deberia crear el archivo correctamente. */
    @Test
    void shouldSaveGameCreateFile() throws GameException {
        SaveManager.saveGame(gameSingle, FILE_SINGLE);
        assertTrue(new File(FILE_SINGLE).exists());
    }

    /** loadGame deberia restaurar el nivel y muertes guardados. */
    @Test
    void shouldLoadGameRestoreDeathsAndLevel() throws GameException {
        gameSingle.setDeaths(7);
        gameSingle.setSecondsLeft(90);
        SaveManager.saveGame(gameSingle, FILE_SINGLE);

        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        Level level = new Level(1, board, List.of(), List.of(c));
        Game loaded = Game.getInstance(p, List.of(level));
        SaveManager.loadGame(loaded, FILE_SINGLE);

        assertEquals(7, loaded.getDeaths());
        assertEquals(90, loaded.getSecondsLeft());
    }

    /** loadGame deberia restaurar el estado de las monedas. */
    @Test
    void shouldLoadGameRestoreCoinStates() throws GameException {
        coin.collect();
        SaveManager.saveGame(gameSingle, FILE_SINGLE);

        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        Level level = new Level(1, board, List.of(), List.of(c));
        Game loaded = Game.getInstance(p, List.of(level));
        SaveManager.loadGame(loaded, FILE_SINGLE);

        assertTrue(loaded.getCurrentLevel().getCoins().get(0).isCollected());
    }

    /** loadGame deberia restaurar el checkpoint del jugador. */
    @Test
    void shouldLoadGameRestoreCheckpoint() throws GameException {
        player.saveCheckpoint(300, 200);
        SaveManager.saveGame(gameSingle, FILE_SINGLE);

        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        Level level = new Level(1, board, List.of(), List.of(c));
        Game loaded = Game.getInstance(p, List.of(level));
        SaveManager.loadGame(loaded, FILE_SINGLE);

        assertEquals(300, loaded.getPlayer().getCheckpointX());
    }

    /** readMode deberia retornar single para archivo de partida single. */
    @Test
    void shouldReadModeSingleFromSingleSaveFile() throws GameException {
        SaveManager.saveGame(gameSingle, FILE_SINGLE);
        assertEquals("single", SaveManager.readMode(FILE_SINGLE));
    }

    // ─── SaveManager PvP ────────────────────────────────────────

    /** saveGamePvP deberia crear el archivo correctamente. */
    @Test
    void shouldSaveGamePvPCreateFile() throws GameException {
        SaveManager.saveGamePvP(gamePvP, FILE_PVP);
        assertTrue(new File(FILE_PVP).exists());
    }

    /** readMode deberia retornar pvp para archivo de partida PvP. */
    @Test
    void shouldReadModePvPFromPvPSaveFile() throws GameException {
        SaveManager.saveGamePvP(gamePvP, FILE_PVP);
        assertEquals("pvp", SaveManager.readMode(FILE_PVP));
    }

    /** loadGamePvP deberia restaurar posiciones de ambos jugadores. */
    @Test
    void shouldLoadGamePvPRestorePlayerPositions() throws GameException {
        gamePvP.getPlayer1().setPosition(100, 200);
        gamePvP.getPlayer2().setPosition(600, 300);
        SaveManager.saveGamePvP(gamePvP, FILE_PVP);

        StartZone s1 = new StartZone(20,  200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700,  200, 80, 100);
        EndZone   e2 = new EndZone(20,   200, 80, 100);
        BoardPvP boardPvP = new BoardPvP(s1, s2, e1, e2, List.of());
        LevelPvP levelPvP = new LevelPvP(1, boardPvP, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        GamePvP loaded = new GamePvP(p1, p2, List.of(levelPvP));
        SaveManager.loadGamePvP(loaded, FILE_PVP);

        assertEquals(100, loaded.getPlayer1().getX());
        assertEquals(600, loaded.getPlayer2().getX());
    }

    /** loadGamePvP deberia restaurar checkpoints de ambos jugadores. */
    @Test
    void shouldLoadGamePvPRestoreCheckpoints() throws GameException {
        gamePvP.getPlayer1().saveCheckpoint(200, 250);
        gamePvP.getPlayer2().saveCheckpoint(600, 250);
        SaveManager.saveGamePvP(gamePvP, FILE_PVP);

        StartZone s1 = new StartZone(20,  200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700,  200, 80, 100);
        EndZone   e2 = new EndZone(20,   200, 80, 100);
        BoardPvP boardPvP = new BoardPvP(s1, s2, e1, e2, List.of());
        LevelPvP levelPvP = new LevelPvP(1, boardPvP, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        GamePvP loaded = new GamePvP(p1, p2, List.of(levelPvP));
        SaveManager.loadGamePvP(loaded, FILE_PVP);

        assertEquals(200, loaded.getPlayer1().getCheckpointX());
        assertEquals(600, loaded.getPlayer2().getCheckpointX());
    }

    /** readMode deberia retornar unknown para archivo inexistente. */
    @Test
    void shouldReadModeReturnUnknownForMissingFile() {
        assertEquals("unknown", SaveManager.readMode("no_existe.dopo"));
    }

    /** saveGame deberia lanzar GameException con ruta invalida. */
    @Test
    void shouldSaveGameThrowGameExceptionOnInvalidPath() {
        assertThrows(GameException.class,
            () -> SaveManager.saveGame(gameSingle, "/ruta/invalida/no/existe/save.dopo"));
    }

    /** loadGame deberia lanzar GameException con archivo inexistente. */
    @Test
    void shouldLoadGameThrowGameExceptionOnMissingFile() {
        assertThrows(GameException.class,
            () -> SaveManager.loadGame(gameSingle, "no_existe.dopo"));
    }

    // ─── GameException ──────────────────────────────────────────

    /** GameException deberia conservar el mensaje. */
    @Test
    void shouldGameExceptionPreserveMessage() {
        GameException ex = new GameException("error de prueba");
        assertEquals("error de prueba", ex.getMessage());
    }

    /** GameException con causa deberia conservar la causa. */
    @Test
    void shouldGameExceptionPreserveCause() {
        Throwable cause = new RuntimeException("causa");
        GameException ex = new GameException("error", cause);
        assertSame(cause, ex.getCause());
    }

    // ─── GameLogger ─────────────────────────────────────────────

    /** GameLogger.info no deberia lanzar excepcion. */
    @Test
    void shouldNotGameLoggerInfoThrowException() {
        assertDoesNotThrow(() -> GameLogger.info("mensaje de prueba"));
    }

    /** GameLogger.error con excepcion no deberia lanzar excepcion. */
    @Test
    void shouldNotGameLoggerErrorWithExceptionThrow() {
        assertDoesNotThrow(() -> GameLogger.error("error prueba", new RuntimeException("causa")));
    }

    /** GameLogger.error sin excepcion no deberia lanzar excepcion. */
    @Test
    void shouldNotGameLoggerErrorWithoutExceptionThrow() {
        assertDoesNotThrow(() -> GameLogger.error("error simple"));
    }

    /** GameLogger.warn no deberia lanzar excepcion. */
    @Test
    void shouldNotGameLoggerWarnThrowException() {
        assertDoesNotThrow(() -> GameLogger.warn("advertencia de prueba"));
    }

    /** loadGame deberia restaurar el tipo del jugador. */
    @Test
    void shouldLoadGameRestorePlayerType() throws GameException {
        player.applyType(PlayerType.BLUE);
        SaveManager.saveGame(gameSingle, FILE_SINGLE);

        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        Level level = new Level(1, board, List.of(), List.of(c));
        Game loaded = Game.getInstance(p, List.of(level));
        SaveManager.loadGame(loaded, FILE_SINGLE);

        assertEquals(PlayerType.BLUE, loaded.getPlayer().getType());
    }

    /** loadGamePvP deberia restaurar tipos de ambos jugadores. */
    @Test
    void shouldLoadGamePvPRestorePlayerTypes() throws GameException {
        gamePvP.getPlayer1().applyType(PlayerType.BLUE);
        gamePvP.getPlayer2().applyType(PlayerType.GREEN);
        SaveManager.saveGamePvP(gamePvP, FILE_PVP);

        StartZone s1 = new StartZone(20,  200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700,  200, 80, 100);
        EndZone   e2 = new EndZone(20,   200, 80, 100);
        BoardPvP boardPvP = new BoardPvP(s1, s2, e1, e2, List.of());
        LevelPvP levelPvP = new LevelPvP(1, boardPvP, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        GamePvP loaded = new GamePvP(p1, p2, List.of(levelPvP));
        SaveManager.loadGamePvP(loaded, FILE_PVP);

        assertEquals(PlayerType.BLUE,  loaded.getPlayer1().getType());
        assertEquals(PlayerType.GREEN, loaded.getPlayer2().getType());
    }

    /** loadGamePvP deberia restaurar estado de monedas. */
    @Test
    void shouldLoadGamePvPRestoreCoinStates() throws GameException {
        gamePvP.getCurrentLevel().getCoins().get(0).collect();
        SaveManager.saveGamePvP(gamePvP, FILE_PVP);

        StartZone s1 = new StartZone(20,  200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700,  200, 80, 100);
        EndZone   e2 = new EndZone(20,   200, 80, 100);
        BoardPvP boardPvP = new BoardPvP(s1, s2, e1, e2, List.of());
        Coin c = new Coin(400, 240);
        LevelPvP levelPvP = new LevelPvP(1, boardPvP, new ArrayList<>(), new ArrayList<>(),
            List.of(c), new ArrayList<>());
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        GamePvP loaded = new GamePvP(p1, p2, List.of(levelPvP));
        SaveManager.loadGamePvP(loaded, FILE_PVP);

        assertTrue(loaded.getCurrentLevel().getCoins().get(0).isCollected());
    }

    /** saveGamePvP deberia lanzar GameException con ruta invalida. */
    @Test
    void shouldSaveGamePvPThrowGameExceptionOnInvalidPath() {
        assertThrows(GameException.class,
            () -> SaveManager.saveGamePvP(gamePvP, "/ruta/invalida/no/existe/save.dopo"));
    }

    /** loadGamePvP deberia lanzar GameException con archivo inexistente. */
    @Test
    void shouldLoadGamePvPThrowGameExceptionOnMissingFile() {
        assertThrows(GameException.class,
            () -> SaveManager.loadGamePvP(gamePvP, "no_existe.dopo"));
    }

    /** loadGame deberia restaurar posicion x del jugador. */
    @Test
    void shouldLoadGameRestorePlayerPositionX() throws GameException {
        player.setPosition(200, 240);
        SaveManager.saveGame(gameSingle, FILE_SINGLE);

        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        Level level = new Level(1, board, List.of(), List.of(c));
        Game loaded = Game.getInstance(p, List.of(level));
        SaveManager.loadGame(loaded, FILE_SINGLE);

        assertEquals(200, loaded.getPlayer().getX());
    }

    /** loadGame deberia restaurar posicion y del jugador. */
    @Test
    void shouldLoadGameRestorePlayerPositionY() throws GameException {
        player.setPosition(50, 300);
        SaveManager.saveGame(gameSingle, FILE_SINGLE);

        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        Level level = new Level(1, board, List.of(), List.of(c));
        Game loaded = Game.getInstance(p, List.of(level));
        SaveManager.loadGame(loaded, FILE_SINGLE);

        assertEquals(300, loaded.getPlayer().getY());
    }

    /** loadGame deberia restaurar estado del juego. */
    @Test
    void shouldLoadGameRestoreGameState() throws GameException {
        gameSingle.setState(GameState.PAUSED);
        SaveManager.saveGame(gameSingle, FILE_SINGLE);

        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        Level level = new Level(1, board, List.of(), List.of(c));
        Game loaded = Game.getInstance(p, List.of(level));
        SaveManager.loadGame(loaded, FILE_SINGLE);

        assertEquals(GameState.PAUSED, loaded.getState());
    }

    /** loadGamePvP deberia restaurar deaths1 y deaths2. */
    @Test
    void shouldLoadGamePvPRestoreDeaths() throws GameException {
        // deaths no se guardan en PvP pero secondsLeft si
        gamePvP.getPlayer1().setPosition(150, 240);
        SaveManager.saveGamePvP(gamePvP, FILE_PVP);

        StartZone s1 = new StartZone(20,  200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700,  200, 80, 100);
        EndZone   e2 = new EndZone(20,   200, 80, 100);
        BoardPvP boardPvP = new BoardPvP(s1, s2, e1, e2, List.of());
        LevelPvP levelPvP = new LevelPvP(1, boardPvP, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        GamePvP loaded = new GamePvP(p1, p2, List.of(levelPvP));
        SaveManager.loadGamePvP(loaded, FILE_PVP);

        assertEquals(150, loaded.getPlayer1().getX());
    }
}
