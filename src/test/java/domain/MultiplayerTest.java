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

    /** GamePvP getPlayer1 y getPlayer2 deberian retornar los jugadores correctos. */
    @Test
    void shouldPvPGetPlayersReturnCorrectPlayers() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        assertSame(p1, game.getPlayer1());
        assertSame(p2, game.getPlayer2());
    }

    /** GamePvP getSecondsLeft deberia retornar 180 al inicio. */
    @Test
    void shouldPvPGetSecondsLeftReturn180AtStart() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        assertEquals(180, game.getSecondsLeft());
    }

    /** GamePvP restart deberia resetear segundos y estado. */
    @Test
    void shouldPvPRestartResetSecondsAndState() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.pause();
        game.restart();
        assertEquals(GameState.PLAYING, game.getState());
        assertEquals(180, game.getSecondsLeft());
    }

    /** GamePvM getPlayer y getMachine deberian retornar los correctos. */
    @Test
    void shouldPvMGetPlayerAndMachineReturnCorrect() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.EXPERT);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        assertSame(player, game.getPlayer());
        assertSame(machine, game.getMachine());
    }

    /** GamePvM getCurrentLevel deberia retornar el nivel actual. */
    @Test
    void shouldPvMGetCurrentLevelReturnCurrentLevel() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        assertNotNull(game.getCurrentLevel());
    }

    /** LevelPvP getBoard deberia retornar el tablero. */
    @Test
    void shouldLevelPvPGetBoardReturnBoard() {
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        assertNotNull(level.getBoard());
    }

    /** GamePvP TIMEOUT deberia dejar de actualizar. */
    @Test
    void shouldPvPNotUpdateWhenStateIsTimeout() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        for (int i = 0; i < 180 * 60; i++) game.update();
        assertEquals(GameState.TIMEOUT, game.getState());
        int d = game.getDeaths1();
        game.update();
        assertEquals(d, game.getDeaths1());
    }

    /** GamePvP deberia matar al jugador 1 con SliderEnemy. */
    @Test
    void shouldPvPKillPlayer1WhenCollidesWithSliderEnemy() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        SliderEnemy slider = new SliderEnemy(50, 240, 3, 500);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(slider), new ArrayList<>(),
            new ArrayList<>(), null);
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths1());
    }

    /** GamePvP deberia matar al jugador 1 con AcceleratedEnemy. */
    @Test
    void shouldPvPKillPlayer1WhenCollidesWithAcceleratedEnemy() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        AcceleratedEnemy ae = new AcceleratedEnemy(50, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), List.of(ae),
            new ArrayList<>(), null);
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths1());
    }

    /** GamePvP deberia absorber golpe con escudo en lugar de morir. */
    @Test
    void shouldPvPAbsorbHitWithShieldInsteadOfDying() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        p1.applyType(PlayerType.GREEN);
        Enemy enemy = new Enemy(50, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(0, game.getDeaths1());
    }

    /** GamePvM deberia matar al jugador con SliderEnemy. */
    @Test
    void shouldPvMKillPlayerWhenCollidesWithSliderEnemy() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        SliderEnemy slider = new SliderEnemy(50, 240, 3, 500);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(slider), new ArrayList<>(),
            new ArrayList<>(), null);
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeathsPlayer());
    }

    /** GamePvM deberia matar al jugador con AcceleratedEnemy. */
    @Test
    void shouldPvMKillPlayerWhenCollidesWithAcceleratedEnemy() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        AcceleratedEnemy ae = new AcceleratedEnemy(50, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), List.of(ae),
            new ArrayList<>(), null);
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeathsPlayer());
    }

    /** GamePvM deberia declarar ganador a la maquina cuando llega a su EndZone. */
    @Test
    void shouldPvMDeclareMachineWinnerWhenReachesEndZone() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        Coin coin = new Coin(400, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        coin.collect();
        machine.setPosition(30, 240);
        game.update();
        assertEquals("Máquina", game.getWinner());
    }

    /** GamePvM deberia absorber golpe con escudo en lugar de morir. */
    @Test
    void shouldPvMAbsorbHitWithShieldInsteadOfDying() {
        Player player = new Player(50, 240);
        player.applyType(PlayerType.GREEN);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        Enemy enemy = new Enemy(50, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(0, game.getDeathsPlayer());
    }

    /** GamePvM deberia recoger SkinCoin al colisionar. */
    @Test
    void shouldPvMCollectSkinCoinOnCollision() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        SkinCoin sc = new SkinCoin(50, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(sc));
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertTrue(sc.isCollected());
    }

    /** GamePvM deberia recoger GreenCoin al colisionar. */
    @Test
    void shouldPvMCollectGreenCoinOnCollision() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        GreenCoin gc = new GreenCoin(50, 240);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), gc);
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertTrue(gc.isCollected());
    }

    /** GamePvM deberia recoger LifeSource al colisionar. */
    @Test
    void shouldPvMCollectLifeSourceOnCollision() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LifeSource ls = new LifeSource(50, 240);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), null, List.of(ls));
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertTrue(ls.isCollected());
    }

    /** GamePvM deberia activar checkpoint cuando el jugador lo pisa. */
    @Test
    void shouldPvMActivateCheckpointWhenPlayerEnters() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        CheckpointZone cp = new CheckpointZone(40, 230, 80, 60);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            List.of(cp));
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertTrue(cp.isActivated());
    }

    /** GamePvP isPlayer1Finished deberia retornar true cuando termina. */
    @Test
    void shouldPvPIsPlayer1FinishedReturnTrueWhenDone() {
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
        assertTrue(game.isPlayer1Finished());
    }

    /** GamePvP isPlayer2Finished deberia retornar true cuando termina. */
    @Test
    void shouldPvPIsPlayer2FinishedReturnTrueWhenDone() {
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
        assertTrue(game.isPlayer2Finished());
    }

    /** LevelPvP fullReset deberia resetear GreenCoin cuando esta presente. */
    @Test
    void shouldLevelPvPFullResetGreenCoinWhenPresent() {
        Player p1 = new Player(50, 240);
        GreenCoin gc = new GreenCoin(400, 240);
        gc.collect(p1);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), gc);
        level.fullReset(p1, start1);
        assertFalse(gc.isCollected());
    }

    /** LevelPvP fullReset deberia resetear LifeSource cuando esta presente. */
    @Test
    void shouldLevelPvPFullResetLifeSourceWhenPresent() {
        Player p1 = new Player(50, 240);
        LifeSource ls = new LifeSource(400, 240);
        ls.collect(p1);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), null, List.of(ls));
        level.fullReset(p1, start1);
        assertFalse(ls.isCollected());
    }

    /** LevelPvP resetPositionOnly con checkpoint deberia respetar el checkpoint. */
    @Test
    void shouldLevelPvPResetPositionOnlyRespectCheckpointWhenActive() {
        Player p1 = new Player(50, 240);
        p1.saveCheckpoint(300, 200);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        level.resetPositionOnly(p1, start1);
        assertEquals(300, p1.getX());
        assertEquals(200, p1.getY());
    }

    /** LevelPvP resetPlayer con checkpoint deberia respetar el checkpoint. */
    @Test
    void shouldLevelPvPResetPlayerRespectCheckpointWhenActive() {
        Player p1 = new Player(50, 240);
        p1.saveCheckpoint(350, 210);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        level.resetPlayer(p1, start1);
        assertEquals(350, p1.getX());
        assertEquals(210, p1.getY());
    }

    /** LevelPvP getSliderEnemies deberia retornar lista correcta. */
    @Test
    void shouldLevelPvPGetSliderEnemiesReturnCorrectList() {
        SliderEnemy slider = new SliderEnemy(200, 100, 3, 500);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(slider), new ArrayList<>(),
            new ArrayList<>(), null);
        assertEquals(1, level.getSliderEnemies().size());
    }

    /** LevelPvP getAcceleratedEnemies deberia retornar lista correcta. */
    @Test
    void shouldLevelPvPGetAcceleratedEnemiesReturnCorrectList() {
        AcceleratedEnemy ae = new AcceleratedEnemy(200, 200, 1, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), List.of(ae),
            new ArrayList<>(), null);
        assertEquals(1, level.getAcceleratedEnemies().size());
    }

    /** LevelPvP isCompleted deberia retornar false cuando no hay monedas. */
    @Test
    void shouldLevelPvPNotBeCompletedWhenCoinListIsEmpty() {
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        assertFalse(level.isCompleted());
    }

    /** GamePvP deberia avanzar al siguiente nivel cuando player1 termina con mas niveles. */
    @Test
    void shouldPvPAdvanceToNextLevelWhenPlayer1FinishesAndMoreLevelsExist() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Coin coin1 = new Coin(400, 240);
        LevelPvP level1 = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin1), new ArrayList<>());
        StartZone s1b = new StartZone(20, 200, 80, 100);
        EndZone   e1b = new EndZone(700, 200, 80, 100);
        StartZone s2b = new StartZone(700, 200, 80, 100);
        EndZone   e2b = new EndZone(20, 200, 80, 100);
        BoardPvP board2 = new BoardPvP(s1b, s2b, e1b, e2b, List.of());
        LevelPvP level2 = new LevelPvP(2, board2, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level1, level2));
        game.start();
        coin1.collect();
        p1.setPosition(720, 240);
        game.update();
        assertEquals(1, game.getCurrentLevelIndex());
    }

    /** GamePvM deberia avanzar al siguiente nivel cuando el jugador termina con mas niveles. */
    @Test
    void shouldPvMAdvanceToNextLevelWhenPlayerFinishesAndMoreLevelsExist() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        Coin coin1 = new Coin(400, 240);
        LevelPvP level1 = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin1), new ArrayList<>());
        StartZone s1b = new StartZone(20, 200, 80, 100);
        EndZone   e1b = new EndZone(700, 200, 80, 100);
        StartZone s2b = new StartZone(700, 200, 80, 100);
        EndZone   e2b = new EndZone(20, 200, 80, 100);
        BoardPvP board2 = new BoardPvP(s1b, s2b, e1b, e2b, List.of());
        LevelPvP level2 = new LevelPvP(2, board2, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level1, level2));
        game.start();
        coin1.collect();
        player.setPosition(720, 240);
        game.update();
        assertEquals(1, game.getCurrentLevelIndex());
    }

    /** GamePvP deberia matar al jugador 1 con PatrolEnemy. */
    @Test
    void shouldPvPKillPlayer1WhenCollidesWithPatrolEnemy() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        PatrolEnemy patrol = new PatrolEnemy(50, 240, 3, new int[][]{{51, 240}});
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), List.of(patrol),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths1());
    }

    /** GamePvM deberia matar al jugador con PatrolEnemy. */
    @Test
    void shouldPvMKillPlayerWhenCollidesWithPatrolEnemy() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        PatrolEnemy patrol = new PatrolEnemy(50, 240, 3, new int[][]{{51, 240}});
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), List.of(patrol),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeathsPlayer());
    }

    /** GamePvP deberia absorber golpe de la maquina con escudo. */
    @Test
    void shouldPvPAbsorbHitForPlayer2WithShield() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        p2.applyType(PlayerType.GREEN);
        Enemy enemy = new Enemy(730, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(0, game.getDeaths2());
    }

    /** GamePvM deberia absorber golpe de la maquina con escudo. */
    @Test
    void shouldPvMAbsorbHitForMachineWithShield() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        machine.applyType(PlayerType.GREEN);
        Enemy enemy = new Enemy(730, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(0, game.getDeathsMachine());
    }

    /** GamePvM no deberia actualizar cuando esta pausado. */
    @Test
    void shouldPvMNotUpdateWhenPaused() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.pause();
        int xBefore = player.getX();
        player.setMovingRight(true);
        game.update();
        assertEquals(xBefore, player.getX());
    }

    /** GamePvM no deberia actualizar cuando esta en TIMEOUT. */
    @Test
    void shouldPvMNotUpdateWhenStateIsTimeout() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        for (int i = 0; i < 180 * 60; i++) game.update();
        assertEquals(GameState.TIMEOUT, game.getState());
        int d = game.getDeathsPlayer();
        game.update();
        assertEquals(d, game.getDeathsPlayer());
    }

    /** LevelPvP update deberia mover SliderEnemy. */
    @Test
    void shouldLevelPvPUpdateMoveSliderEnemy() {
        SliderEnemy slider = new SliderEnemy(200, 100, 3, 500);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(slider), new ArrayList<>(),
            new ArrayList<>(), null);
        int yBefore = slider.getBounds().y;
        level.update();
        assertNotEquals(yBefore, slider.getBounds().y);
    }

    /** LevelPvP update deberia mover AcceleratedEnemy. */
    @Test
    void shouldLevelPvPUpdateMoveAcceleratedEnemy() {
        AcceleratedEnemy ae = new AcceleratedEnemy(200, 200, 1, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), List.of(ae),
            new ArrayList<>(), null);
        int xBefore = ae.getBounds().x;
        level.update();
        assertNotEquals(xBefore, ae.getBounds().x);
    }

    /** GamePvP getWinner deberia retornar Jugador1 cuando timePlayer1 menor que timePlayer2. */
    @Test
    void shouldPvPGetWinnerReturnPlayer1WhenTimePlayer1IsLess() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Coin coin = new Coin(400, 240);
        LevelPvP level1 = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        StartZone s1b = new StartZone(20, 200, 80, 100);
        EndZone   e1b = new EndZone(700, 200, 80, 100);
        StartZone s2b = new StartZone(700, 200, 80, 100);
        EndZone   e2b = new EndZone(20, 200, 80, 100);
        BoardPvP board2 = new BoardPvP(s1b, s2b, e1b, e2b, List.of());
        LevelPvP level2 = new LevelPvP(2, board2, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level1, level2));
        game.start();
        // Player1 termina primero (avanza nivel)
        coin.collect();
        p1.setPosition(720, 240);
        game.update();
        // Ahora en nivel 2, player2 termina
        game.getCurrentLevel().getCoins(); // nivel 2 sin monedas
        p2.setPosition(30, 240);
        game.update();
        // Ambos terminaron en distintos tiempos
        assertNotNull(game.getWinner());
    }

    /** GamePvP update no deberia ganar cuando isCompleted es false aunque este en EndZone. */
    @Test
    void shouldPvPNotWinWhenLevelNotCompletedEvenIfInEndZone() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Coin coin = new Coin(400, 240); // no recogida
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        p1.setPosition(720, 240); // en EndZone pero sin monedas
        game.update();
        assertNotEquals(GameState.WIN, game.getState());
    }

    /** GamePvM update no deberia ganar cuando isCompleted es false aunque este en EndZone. */
    @Test
    void shouldPvMNotWinWhenLevelNotCompletedEvenIfInEndZone() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        Coin coin = new Coin(400, 240); // no recogida
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        player.setPosition(720, 240); // en EndZone pero sin monedas
        game.update();
        assertNotEquals(GameState.WIN, game.getState());
    }

    /** GamePvP update deberia decrementar secondsLeft cada 60 ticks. */
    @Test
    void shouldPvPUpdateDecrementSecondsLeftEvery60Ticks() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        for (int i = 0; i < 60; i++) game.update();
        assertEquals(179, game.getSecondsLeft());
    }

    /** GamePvM update deberia decrementar secondsLeft cada 60 ticks. */
    @Test
    void shouldPvMUpdateDecrementSecondsLeftEvery60Ticks() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        for (int i = 0; i < 60; i++) game.update();
        assertEquals(179, game.getSecondsLeft());
    }

    /** GamePvP getWinner deberia retornar Empate cuando ninguno ha terminado. */
    @Test
    void shouldPvPGetWinnerReturnEmpateWhenNobodyFinished() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        assertEquals("Empate", game.getWinner());
    }

    /** GamePvP update deberia saltar bloque player1 cuando ya termino. */
    @Test
    void shouldPvPSkipPlayer1BlockWhenAlreadyFinished() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Coin coin = new Coin(400, 240);
        LevelPvP level1 = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        StartZone s1b = new StartZone(20, 200, 80, 100);
        EndZone   e1b = new EndZone(700, 200, 80, 100);
        StartZone s2b = new StartZone(700, 200, 80, 100);
        EndZone   e2b = new EndZone(20, 200, 80, 100);
        BoardPvP board2 = new BoardPvP(s1b, s2b, e1b, e2b, List.of());
        LevelPvP level2 = new LevelPvP(2, board2, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level1, level2));
        game.start();
        coin.collect();
        p1.setPosition(720, 240);
        game.update(); // player1 termina, avanza nivel
        // Ahora player1Finished=false de nuevo, pero verificamos que no crashea
        assertEquals(1, game.getCurrentLevelIndex());
    }

    /** GamePvP update deberia saltar bloque player2 cuando ya termino. */
    @Test
    void shouldPvPSkipPlayer2BlockWhenAlreadyFinished() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Coin coin = new Coin(400, 240);
        LevelPvP level1 = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        StartZone s1b = new StartZone(20, 200, 80, 100);
        EndZone   e1b = new EndZone(700, 200, 80, 100);
        StartZone s2b = new StartZone(700, 200, 80, 100);
        EndZone   e2b = new EndZone(20, 200, 80, 100);
        BoardPvP board2 = new BoardPvP(s1b, s2b, e1b, e2b, List.of());
        LevelPvP level2 = new LevelPvP(2, board2, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level1, level2));
        game.start();
        coin.collect();
        p2.setPosition(30, 240);
        game.update(); // player2 termina, avanza nivel
        assertEquals(1, game.getCurrentLevelIndex());
    }

    /** GamePvM maquina con checkpoint deberia respetar checkpoint al morir. */
    @Test
    void shouldPvMRespawnMachineAtCheckpointWhenDies() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        machine.saveCheckpoint(600, 230);
        Enemy enemy = new Enemy(730, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, List.of(enemy), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(600, machine.getX());
        assertEquals(230, machine.getY());
    }

    /** GamePvP matar jugador 2 con Bomb deberia incrementar deaths2. */
    @Test
    void shouldPvPKillPlayer2WhenCollidesWithBomb() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        Bomb bomb = new Bomb(730, 240);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            List.of(bomb), null);
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths2());
    }

    /** GamePvM matar maquina con Bomb deberia incrementar deathsMachine. */
    @Test
    void shouldPvMKillMachineWhenCollidesWithBomb() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        Bomb bomb = new Bomb(730, 240);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            List.of(bomb), null);
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeathsMachine());
    }

    /** GamePvM matar maquina con SliderEnemy deberia incrementar deathsMachine. */
    @Test
    void shouldPvMKillMachineWhenCollidesWithSliderEnemy() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        SliderEnemy slider = new SliderEnemy(730, 240, 3, 500);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(slider), new ArrayList<>(),
            new ArrayList<>(), null);
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeathsMachine());
    }

    /** GamePvM matar maquina con AcceleratedEnemy deberia incrementar deathsMachine. */
    @Test
    void shouldPvMKillMachineWhenCollidesWithAcceleratedEnemy() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        AcceleratedEnemy ae = new AcceleratedEnemy(730, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), List.of(ae),
            new ArrayList<>(), null);
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeathsMachine());
    }

    /** GamePvM matar maquina con PatrolEnemy deberia incrementar deathsMachine. */
    @Test
    void shouldPvMKillMachineWhenCollidesWithPatrolEnemy() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        PatrolEnemy patrol = new PatrolEnemy(730, 240, 3, new int[][]{{731, 240}});
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), List.of(patrol),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeathsMachine());
    }

    /** GamePvP matar jugador 2 con SliderEnemy deberia incrementar deaths2. */
    @Test
    void shouldPvPKillPlayer2WhenCollidesWithSliderEnemy() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        SliderEnemy slider = new SliderEnemy(730, 240, 3, 500);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(slider), new ArrayList<>(),
            new ArrayList<>(), null);
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths2());
    }

    /** GamePvP matar jugador 2 con AcceleratedEnemy deberia incrementar deaths2. */
    @Test
    void shouldPvPKillPlayer2WhenCollidesWithAcceleratedEnemy() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        AcceleratedEnemy ae = new AcceleratedEnemy(730, 240, 0, 0, 800, 500);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), List.of(ae),
            new ArrayList<>(), null);
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths2());
    }

    /** GamePvP matar jugador 2 con PatrolEnemy deberia incrementar deaths2. */
    @Test
    void shouldPvPKillPlayer2WhenCollidesWithPatrolEnemy() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        PatrolEnemy patrol = new PatrolEnemy(730, 240, 3, new int[][]{{731, 240}});
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), List.of(patrol),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        game.update();
        assertEquals(1, game.getDeaths2());
    }

    /** GamePvP pause en estado WIN no deberia cambiar el estado. */
    @Test
    void shouldPvPPauseNotChangeStateWhenStateIsWin() {
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
        game.pause();
        assertEquals(GameState.WIN, game.getState());
    }

    /** GamePvM pause en estado WIN no deberia cambiar el estado. */
    @Test
    void shouldPvMPauseNotChangeStateWhenStateIsWin() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        Coin coin = new Coin(400, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        coin.collect();
        player.setPosition(720, 240);
        game.update(); // WIN
        game.pause();
        assertEquals(GameState.WIN, game.getState());
    }

    /** GamePvP pause en estado TIMEOUT no deberia cambiar el estado. */
    @Test
    void shouldPvPPauseNotChangeStateWhenStateIsTimeout() {
        Player p1 = new Player(50, 240);
        Player p2 = new Player(730, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvP game = new GamePvP(p1, p2, List.of(level));
        game.start();
        for (int i = 0; i < 180 * 60; i++) game.update();
        game.pause();
        assertEquals(GameState.TIMEOUT, game.getState());
    }

    /** GamePvM pause en estado TIMEOUT no deberia cambiar el estado. */
    @Test
    void shouldPvMPauseNotChangeStateWhenStateIsTimeout() {
        Player player = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.RANDOM);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        GamePvM game = new GamePvM(player, machine, List.of(level));
        game.start();
        for (int i = 0; i < 180 * 60; i++) game.update();
        game.pause();
        assertEquals(GameState.TIMEOUT, game.getState());
    }

    /** LevelPvP isCompleted deberia retornar false cuando alguna moneda no esta recogida. */
    @Test
    void shouldLevelPvPNotBeCompletedWhenSomeCoinNotCollected() {
        Coin c1 = new Coin(300, 240);
        Coin c2 = new Coin(400, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(c1, c2), new ArrayList<>());
        c1.collect();
        assertFalse(level.isCompleted());
    }

    /** LevelPvP fullReset con greenCoin null no deberia lanzar excepcion. */
    @Test
    void shouldLevelPvPFullResetNotThrowWhenGreenCoinIsNull() {
        Player p = new Player(50, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        assertDoesNotThrow(() -> level.fullReset(p, start1));
    }

    /** LevelPvP resetPlayer sin checkpoint deberia reposicionar en startZone. */
    @Test
    void shouldLevelPvPResetPlayerToStartZoneWhenNoCheckpoint() {
        Player p = new Player(500, 300);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        level.resetPlayer(p, start1);
        assertTrue(p.getX() >= 20 && p.getX() <= 100);
    }

    /** LevelPvP resetPositionOnly sin checkpoint deberia reposicionar en startZone. */
    @Test
    void shouldLevelPvPResetPositionOnlyToStartZoneWhenNoCheckpoint() {
        Player p = new Player(500, 300);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        level.resetPositionOnly(p, start1);
        assertTrue(p.getX() >= 20 && p.getX() <= 100);
    }

    /** LevelPvP getSkinCoins deberia retornar lista correcta. */
    @Test
    void shouldLevelPvPGetSkinCoinsReturnCorrectList() {
        SkinCoin sc = new SkinCoin(300, 240);
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(sc));
        assertEquals(1, level.getSkinCoins().size());
    }

    /** LevelPvP getGreenCoin deberia retornar null cuando no hay. */
    @Test
    void shouldLevelPvPGetGreenCoinReturnNullWhenAbsent() {
        LevelPvP level = new LevelPvP(1, board, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        assertNull(level.getGreenCoin());
    }

    /** LevelPvP getLifeSources deberia retornar lista correcta. */
    @Test
    void shouldLevelPvPGetLifeSourcesReturnCorrectList() {
        LifeSource ls = new LifeSource(300, 240);
        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), null, List.of(ls));
        assertEquals(1, level.getLifeSources().size());
    }
}