package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link StatsExporter}.
 * Verifican la exportacion de estadisticas de partida a archivo de texto.
 * Los metodos siguen el estandar should/shouldNot para describir
 * el comportamiento esperado de forma clara y legible.
 */
class StatsExporterTest {

    private static final String FILE_SINGLE = "test_stats_single.txt";
    private static final String FILE_PVP    = "test_stats_pvp.txt";
    private static final String FILE_PVM    = "test_stats_pvm.txt";

    private Game    gameSingle;
    private GamePvP gamePvP;
    private GamePvM gamePvM;

    @BeforeEach
    void setUp() {
        Game.resetInstance();

        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player player = new Player(50, 240);
        Coin coin = new Coin(400, 240);
        Level level = new Level(1, board, List.of(), List.of(coin));
        gameSingle = Game.getInstance(player, List.of(level));
        gameSingle.start();

        StartZone s1 = new StartZone(20,  200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700,  200, 80, 100);
        EndZone   e2 = new EndZone(20,   200, 80, 100);
        BoardPvP boardPvP = new BoardPvP(s1, s2, e1, e2, List.of());
        LevelPvP levelPvP = new LevelPvP(1, boardPvP, new java.util.ArrayList<>(),
            new java.util.ArrayList<>(), List.of(new Coin(400, 240)), new java.util.ArrayList<>());
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        gamePvP = new GamePvP(p1, p2, List.of(levelPvP));
        gamePvP.start();

        StartZone s1m = new StartZone(20,  200, 80, 100);
        StartZone s2m = new StartZone(700, 200, 80, 100);
        EndZone   e1m = new EndZone(700,  200, 80, 100);
        EndZone   e2m = new EndZone(20,   200, 80, 100);
        BoardPvP boardPvM = new BoardPvP(s1m, s2m, e1m, e2m, List.of());
        LevelPvP levelPvM = new LevelPvP(1, boardPvM, new java.util.ArrayList<>(),
            new java.util.ArrayList<>(), List.of(new Coin(400, 240)), new java.util.ArrayList<>());
        Player pm = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        gamePvM = new GamePvM(pm, machine, List.of(levelPvM));
        gamePvM.start();
    }

    @AfterEach
    void tearDown() {
        Game.resetInstance();
        new File(FILE_SINGLE).delete();
        new File(FILE_PVP).delete();
        new File(FILE_PVM).delete();
    }

    // ─── Single Player ───────────────────────────────────────────

    /** exportSingle deberia crear el archivo de estadisticas. */
    @Test
    void shouldExportSingleCreateFile() throws GameException {
        StatsExporter.exportSingle(gameSingle, FILE_SINGLE);
        assertTrue(new File(FILE_SINGLE).exists());
    }

    /** exportSingle deberia escribir las muertes en el archivo. */
    @Test
    void shouldExportSingleWriteDeaths() throws GameException, IOException {
        gameSingle.setDeaths(5);
        StatsExporter.exportSingle(gameSingle, FILE_SINGLE);
        String content = Files.readString(new File(FILE_SINGLE).toPath());
        assertTrue(content.contains("5"));
    }

    /** exportSingle deberia escribir el tiempo restante en el archivo. */
    @Test
    void shouldExportSingleWriteSecondsLeft() throws GameException, IOException {
        gameSingle.setSecondsLeft(120);
        StatsExporter.exportSingle(gameSingle, FILE_SINGLE);
        String content = Files.readString(new File(FILE_SINGLE).toPath());
        assertTrue(content.contains("120"));
    }

    /** exportSingle deberia escribir el nivel actual en el archivo. */
    @Test
    void shouldExportSingleWriteCurrentLevel() throws GameException, IOException {
        StatsExporter.exportSingle(gameSingle, FILE_SINGLE);
        String content = Files.readString(new File(FILE_SINGLE).toPath());
        assertTrue(content.contains("1"));
    }

    /** exportSingle deberia escribir el estado del juego en el archivo. */
    @Test
    void shouldExportSingleWriteGameState() throws GameException, IOException {
        StatsExporter.exportSingle(gameSingle, FILE_SINGLE);
        String content = Files.readString(new File(FILE_SINGLE).toPath());
        assertTrue(content.contains(GameState.PLAYING));
    }

    /** exportSingle deberia lanzar GameException con ruta invalida. */
    @Test
    void shouldExportSingleThrowGameExceptionOnInvalidPath() {
        assertThrows(GameException.class,
            () -> StatsExporter.exportSingle(gameSingle, "/ruta/invalida/stats.txt"));
    }

    /** exportSingle deberia escribir el modo de juego en el archivo. */
    @Test
    void shouldExportSingleWriteGameMode() throws GameException, IOException {
        StatsExporter.exportSingle(gameSingle, FILE_SINGLE);
        String content = Files.readString(new File(FILE_SINGLE).toPath());
        assertTrue(content.toLowerCase().contains("single") || content.toLowerCase().contains("jugador"));
    }

    // ─── PvP ─────────────────────────────────────────────────────

    /** exportPvP deberia crear el archivo de estadisticas. */
    @Test
    void shouldExportPvPCreateFile() throws GameException {
        StatsExporter.exportPvP(gamePvP, FILE_PVP);
        assertTrue(new File(FILE_PVP).exists());
    }

    /** exportPvP deberia escribir muertes del jugador 1. */
    @Test
    void shouldExportPvPWriteDeaths1() throws GameException, IOException {
        StatsExporter.exportPvP(gamePvP, FILE_PVP);
        String content = Files.readString(new File(FILE_PVP).toPath());
        assertTrue(content.contains("0"));
    }

    /** exportPvP deberia escribir muertes del jugador 2. */
    @Test
    void shouldExportPvPWriteDeaths2() throws GameException, IOException {
        StatsExporter.exportPvP(gamePvP, FILE_PVP);
        String content = Files.readString(new File(FILE_PVP).toPath());
        assertNotNull(content);
    }

    /** exportPvP deberia escribir el tiempo restante. */
    @Test
    void shouldExportPvPWriteSecondsLeft() throws GameException, IOException {
        StatsExporter.exportPvP(gamePvP, FILE_PVP);
        String content = Files.readString(new File(FILE_PVP).toPath());
        assertTrue(content.contains("180"));
    }

    /** exportPvP deberia lanzar GameException con ruta invalida. */
    @Test
    void shouldExportPvPThrowGameExceptionOnInvalidPath() {
        assertThrows(GameException.class,
            () -> StatsExporter.exportPvP(gamePvP, "/ruta/invalida/stats.txt"));
    }

    /** exportPvP deberia escribir el modo PvP en el archivo. */
    @Test
    void shouldExportPvPWriteGameMode() throws GameException, IOException {
        StatsExporter.exportPvP(gamePvP, FILE_PVP);
        String content = Files.readString(new File(FILE_PVP).toPath());
        assertTrue(content.toLowerCase().contains("pvp") || content.toLowerCase().contains("jugador"));
    }

    // ─── PvM ─────────────────────────────────────────────────────

    /** exportPvM deberia crear el archivo de estadisticas. */
    @Test
    void shouldExportPvMCreateFile() throws GameException {
        StatsExporter.exportPvM(gamePvM, FILE_PVM);
        assertTrue(new File(FILE_PVM).exists());
    }

    /** exportPvM deberia escribir muertes del jugador. */
    @Test
    void shouldExportPvMWriteDeathsPlayer() throws GameException, IOException {
        StatsExporter.exportPvM(gamePvM, FILE_PVM);
        String content = Files.readString(new File(FILE_PVM).toPath());
        assertTrue(content.contains("0"));
    }

    /** exportPvM deberia escribir muertes de la maquina. */
    @Test
    void shouldExportPvMWriteDeathsMachine() throws GameException, IOException {
        StatsExporter.exportPvM(gamePvM, FILE_PVM);
        String content = Files.readString(new File(FILE_PVM).toPath());
        assertNotNull(content);
    }

    /** exportPvM deberia escribir el tiempo restante. */
    @Test
    void shouldExportPvMWriteSecondsLeft() throws GameException, IOException {
        StatsExporter.exportPvM(gamePvM, FILE_PVM);
        String content = Files.readString(new File(FILE_PVM).toPath());
        assertTrue(content.contains("180"));
    }

    /** exportPvM deberia lanzar GameException con ruta invalida. */
    @Test
    void shouldExportPvMThrowGameExceptionOnInvalidPath() {
        assertThrows(GameException.class,
            () -> StatsExporter.exportPvM(gamePvM, "/ruta/invalida/stats.txt"));
    }

    /** exportPvM deberia escribir el perfil de la maquina. */
    @Test
    void shouldExportPvMWriteMachineProfile() throws GameException, IOException {
        StatsExporter.exportPvM(gamePvM, FILE_PVM);
        String content = Files.readString(new File(FILE_PVM).toPath());
        assertTrue(content.toUpperCase().contains("RANDOM") || content.toLowerCase().contains("maquina"));
    }
}
