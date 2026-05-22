package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para GamePvP, GamePvM y LevelPvP.
 * Cubren flujos de pausa, colision con enemigos, bombas,
 * SkinCoin, GreenCoin, LifeSource y checkpoint en modos multijugador.
 */
class MultiplayerTest {

    private StartZone start1;
    private StartZone start2;
    private EndZone   end1;
    private EndZone   end2;
    private BoardPvP  board;

    @BeforeEach
    void setUp() {
        start1 = new StartZone(20,  200, 80, 100);
        end1   = new EndZone(700,  200, 80, 100);
        start2 = new StartZone(700, 200, 80, 100);
        end2   = new EndZone(20,   200, 80, 100);
        board  = new BoardPvP(start1, start2, end1, end2, List.of());
    }

    // ─── GamePvP ────────────────────────────────────────────────

    /** GamePvP deberia iniciar con estado PLAYING. */
    @Test
    void shouldPvPStartWithPlayingState() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** GamePvP deberia pausar y reanudar correctamente. */
    @Test
    void shouldPvPTogglePause() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.pause();
        assertEquals(GameState.PAUSED, game.getState());
        game.pause();
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** GamePvP no deberia actualizar cuando esta pausado. */
    @Test
    void shouldPvPNotUpdateWhenPaused() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Coin coin = new Coin(400, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.pause();
        int xBefore = p1.getX();
        p1.setMovingRight(true);
        game.update();
        assertEquals(xBefore, p1.getX());
    }

    /** GamePvP deberia matar al jugador 1 al colisionar con enemigo. */
    @Test
    void shouldPvPKillPlayer1WhenCollidesWithEnemy() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Enemy enemy = new Enemy(50, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths1());
    }

    /** GamePvP deberia matar al jugador 2 al colisionar con enemigo. */
    @Test
    void shouldPvPKillPlayer2WhenCollidesWithEnemy() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Enemy enemy = new Enemy(730, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths2());
    }

    /** GamePvP deberia recoger SkinCoin al colisionar. */
    @Test
    void shouldPvPCollectSkinCoinOnCollision() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        SkinCoin sc = new SkinCoin(50, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(sc));
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertTrue(sc.isCollected());
        assertEquals(PlayerType.BLUE, p1.getType());
    }

    /** GamePvP deberia recoger GreenCoin al colisionar. */
    @Test
    void shouldPvPCollectGreenCoinOnCollision() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        GreenCoin gc = new GreenCoin(50, 240);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), gc);
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertTrue(gc.isCollected());
    }

    /** GamePvP deberia matar al jugador 1 al colisionar con bomba. */
    @Test
    void shouldPvPKillPlayer1WhenCollidesWithBomb() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Bomb bomb = new Bomb(50, 240);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            List.of(bomb), null);
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths1());
    }

    /** GamePvP deberia recoger LifeSource al colisionar. */
    @Test
    void shouldPvPCollectLifeSourceOnCollision() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LifeSource ls = new LifeSource(50, 240);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), null, List.of(ls));
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertTrue(ls.isCollected());
    }

    /** GamePvP deberia activar checkpoint cuando el jugador lo pisa. */
    @Test
    void shouldPvPActivateCheckpointWhenPlayerEnters() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        CheckpointZone cp = new CheckpointZone(40, 230, 80, 60);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            List.of(cp));
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertTrue(cp.isActivated());
    }

    /** GamePvP getWinner deberia retornar Jugador 1 cuando player1Finished es true. */
    @Test
    void shouldPvPReturnPlayer1AsWinner() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Coin coin = new Coin(400, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        coin.collect();
        p1.setPosition(720, 240);
        game.update();
        assertEquals("Jugador 1", game.getWinner());
    }

    /** GamePvP getWinner deberia retornar Jugador 2 cuando player2Finished es true. */
    @Test
    void shouldPvPReturnPlayer2AsWinner() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Coin coin = new Coin(400, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        coin.collect();
        p2.setPosition(30, 240);
        game.update();
        assertEquals("Jugador 2", game.getWinner());
    }

    // ─── GamePvM ────────────────────────────────────────────────

    /** GamePvM deberia iniciar con estado PLAYING. */
    @Test
    void shouldPvMStartWithPlayingState() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** GamePvM deberia pausar y reanudar correctamente. */
    @Test
    void shouldPvMTogglePause() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.pause();
        assertEquals(GameState.PAUSED, game.getState());
        game.pause();
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** GamePvM deberia matar al jugador al colisionar con enemigo. */
    @Test
    void shouldPvMKillPlayerWhenCollidesWithEnemy() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        Enemy enemy = new Enemy(50, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeathsPlayer());
    }

    /** GamePvM deberia recoger moneda al colisionar. */
    @Test
    void shouldPvMCollectCoinOnCollision() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        Coin coin = new Coin(50, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertTrue(coin.isCollected());
    }

    /** GamePvM deberia retornar segundos restantes correctamente. */
    @Test
    void shouldPvMReturnCorrectSecondsLeft() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        assertEquals(180, game.getSecondsLeft());
    }

    // ─── LevelPvP ───────────────────────────────────────────────

    /** LevelPvP deberia retornar numero de nivel correcto. */
    @Test
    void shouldLevelPvPReturnCorrectLevelNumber() {
        LevelPvP level = new LevelPvP(3, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        assertEquals(3, level.getLevelNumber());
    }

    /** LevelPvP deberia estar completado cuando todas las monedas estan recogidas. */
    @Test
    void shouldLevelPvPBeCompletedWhenAllCoinsCollected() {
        Coin coin = new Coin(400, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        coin.collect();
        assertTrue(level.isCompleted());
    }

    /** LevelPvP no deberia estar completado cuando hay monedas sin recoger. */
    @Test
    void shouldLevelPvPNotBeCompletedWhenCoinsRemain() {
        Coin coin = new Coin(400, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        assertFalse(level.isCompleted());
    }

    /** LevelPvP fullReset deberia resetear monedas y reposicionar jugador. */
    @Test
    void shouldLevelPvPFullResetCoinsAndPlayer() {
        Coin coin = new Coin(400, 240);
        Player player = new Player(500, 300);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        coin.collect();
        level.fullReset(player, start1);
        assertFalse(coin.isCollected());
        assertTrue(player.getX() >= 20 && player.getX() <= 100);
    }

    /** LevelPvP resetPositionOnly deberia reposicionar sin resetear monedas. */
    @Test
    void shouldLevelPvPResetPositionOnlyWithoutResettingCoins() {
        Coin coin = new Coin(400, 240);
        Player player = new Player(500, 300);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        coin.collect();
        level.resetPositionOnly(player, start1);
        assertTrue(coin.isCollected());
    }

    /** LevelPvP update deberia mover los enemigos. */
    @Test
    void shouldLevelPvPUpdateMoveEnemies() {
        Enemy enemy = new Enemy(300, 240, 3, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        int xBefore = enemy.getBounds().x;
        level.update();
        assertNotEquals(xBefore, enemy.getBounds().x);
    }

    /** LevelPvP update deberia mover PatrolEnemy. */
    @Test
    void shouldLevelPvPUpdateMovePatrolEnemy() {
        PatrolEnemy patrol = new PatrolEnemy(300, 240, 3, new int[][]{{400, 240}, {300, 240}});
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), List.of(patrol),
            new ArrayList<>(), new ArrayList<>());
        int xBefore = patrol.getBounds().x;
        level.update();
        assertNotEquals(xBefore, patrol.getBounds().x);
    }

    /** GamePvP deberia respetar checkpoint al morir el jugador 1. */
    @Test
    void shouldPvPRespawnPlayer1AtCheckpointWhenDies() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        p1.saveCheckpoint(390, 230);
        Enemy enemy = new Enemy(50, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(390, p1.getX());
        assertEquals(230, p1.getY());
    }

    /** GamePvM deberia respetar checkpoint al morir el jugador. */
    @Test
    void shouldPvMRespawnPlayerAtCheckpointWhenDies() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        player.saveCheckpoint(390, 230);
        Enemy enemy = new Enemy(50, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(390, player.getX());
        assertEquals(230, player.getY());
    }

    /** GamePvP skipLevel deberia avanzar al siguiente nivel. */
    @Test
    void shouldPvPSkipLevelAdvanceToNextLevel() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level1 = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        StartZone s1b = new StartZone(20, 200, 80, 100);
        EndZone   e1b = new EndZone(700, 200, 80, 100);
        StartZone s2b = new StartZone(700, 200, 80, 100);
        EndZone   e2b = new EndZone(20, 200, 80, 100);
        BoardPvP board2 = new BoardPvP(s1b, s2b, e1b, e2b, List.of());
        LevelPvP level2 = new LevelPvP(2, board2, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level1, level2));
        game.start();
        game.skipLevel();
        assertEquals(1, game.getCurrentLevelIndex());
    }

    /** GamePvM skipLevel deberia avanzar al siguiente nivel. */
    @Test
    void shouldPvMSkipLevelAdvanceToNextLevel() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LevelPvP level1 = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        StartZone s1b = new StartZone(20, 200, 80, 100);
        EndZone   e1b = new EndZone(700, 200, 80, 100);
        StartZone s2b = new StartZone(700, 200, 80, 100);
        EndZone   e2b = new EndZone(20, 200, 80, 100);
        BoardPvP board2 = new BoardPvP(s1b, s2b, e1b, e2b, List.of());
        LevelPvP level2 = new LevelPvP(2, board2, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level1, level2));
        game.start();
        game.skipLevel();
        assertEquals(1, game.getCurrentLevelIndex());
    }

    /** GamePvP no deberia actualizar cuando esta en estado WIN. */
    @Test
    void shouldPvPNotUpdateWhenStateIsWin() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Coin coin = new Coin(400, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        coin.collect();
        p1.setPosition(720, 240);
        game.update(); // WIN
        int deaths = game.getDeaths1();
        game.update(); // no deberia actualizar
        assertEquals(deaths, game.getDeaths1());
    }

    /** GamePvM deberia matar a la maquina al colisionar con enemigo. */
    @Test
    void shouldPvMKillMachineWhenCollidesWithEnemy() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        Enemy enemy = new Enemy(730, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeathsMachine());
    }
}
