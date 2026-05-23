package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase {@link Game}.
 * Verifica el estado inicial, la lógica de muerte, victoria, pausa,
 * reinicio, progresión de niveles, tiempo agotado y el patrón Singleton.
 * Los métodos siguen el estándar should/shouldNot para describir
 * el comportamiento esperado de forma clara y legible.
 */
class GameTest {

    private Game game;
    private Player player;
    private Coin coin;
    private Enemy enemy;
    private StartZone startZone;
    private EndZone endZone;

    @BeforeEach
    void setUp() {
        Game.resetInstance();

        startZone = new StartZone(20, 200, 80, 100);
        endZone   = new EndZone(700, 200, 80, 100);
        Board board = new Board(startZone, endZone, List.of());

        player = new Player(50, 240);
        coin   = new Coin(400, 240);
        enemy  = new Enemy(600, 400, 3, 0, 800, 500);

        Level level = new Level(1, board, List.of(enemy), List.of(coin));
        game = Game.getInstance(player, List.of(level));
    }

    @AfterEach
    void tearDown() {
        Game.resetInstance();
    }

    /** El juego debería iniciar con estado PLAYING. */
    @Test
    void shouldStartWithPlayingState() {
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** El juego debería iniciar con cero muertes. */
    @Test
    void shouldStartWithZeroDeaths() {
        assertEquals(0, game.getDeaths());
    }

    /** El juego debería iniciar con 180 segundos (3 minutos) de tiempo. */
    @Test
    void shouldStartWithThreeMinutesOfTime() {
        assertEquals(180, game.getSecondsLeft());
    }

    /** El contador de muertes debería incrementarse en 1 al llamar checkDeath(). */
    @Test
    void shouldIncrementDeathCountWhenCheckDeathIsCalled() {
        game.checkDeath();
        assertEquals(1, game.getDeaths());
    }

    /** Las monedas deberían restablecerse a no recogidas al llamar checkDeath(). */
    @Test
    void shouldResetCoinsAfterCheckDeath() {
        coin.collect();
        game.checkDeath();
        assertFalse(coin.isCollected());
    }

    /** El jugador debería reposicionarse en la StartZone al llamar checkDeath(). */
    @Test
    void shouldRepositionPlayerInStartZoneAfterCheckDeath() {
        player.setPosition(600, 300);
        game.checkDeath();
        assertTrue(player.getX() >= 20 && player.getX() <= 100);
    }

    /** checkWin() debería retornar false cuando no se han recogido monedas. */
    @Test
    void shouldNotWinWhenNoCoinsAreCollected() {
        assertFalse(game.checkWin());
    }

    /** checkWin() debería retornar false cuando las monedas están recogidas pero el jugador no está en la EndZone. */
    @Test
    void shouldNotWinWhenCoinsCollectedButPlayerNotInEndZone() {
        coin.collect();
        assertFalse(game.checkWin());
    }

    /** checkWin() debería retornar true cuando todas las monedas están recogidas y el jugador está en la EndZone. */
    @Test
    void shouldWinWhenAllCoinsCollectedAndPlayerInEndZone() {
        coin.collect();
        player.setPosition(720, 240);
        assertTrue(game.checkWin());
    }

    /** El juego debería alternar entre PLAYING y PAUSED al llamar pause(). */
    @Test
    void shouldToggleBetweenPlayingAndPausedWhenPauseIsCalled() {
        game.pause();
        assertEquals(GameState.PAUSED, game.getState());
        game.pause();
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** El juego debería reiniciar muertes y tiempo a sus valores iniciales al llamar restart(). */
    @Test
    void shouldResetDeathsAndTimeAfterRestart() {
        game.checkDeath();
        game.checkDeath();
        game.restart();
        assertEquals(0, game.getDeaths());
        assertEquals(180, game.getSecondsLeft());
    }

    /** El juego debería cambiar a estado PLAYING al llamar restart() sin importar el estado anterior. */
    @Test
    void shouldReturnToPlayingStateAfterRestart() {
        game.pause();
        game.restart();
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** El juego debería cambiar a estado WIN al llamar nextLevel() cuando no hay más niveles. */
    @Test
    void shouldSetWinStateWhenNoMoreLevelsExist() {
        game.nextLevel();
        assertEquals(GameState.WIN, game.getState());
    }

    /** El juego debería cambiar a estado TIMEOUT cuando el tiempo del nivel se agota. */
    @Test
    void shouldSetTimeoutStateWhenTimerReachesZero() {
        for (int i = 0; i < 180 * 60; i++) {
            game.update();
        }
        assertEquals(GameState.TIMEOUT, game.getState());
    }

    /** getInstance() debería retornar siempre la misma instancia (patrón Singleton). */
    @Test
    void shouldReturnSameInstanceEveryTime() {
        Game mismaInstancia = Game.getInstance();
        assertSame(game, mismaInstancia);
    }

    /** nextLevel() con dos niveles deberia avanzar al nivel 2 sin terminar el juego. */
    @Test
    void shouldAdvanceToNextLevelWhenMoreLevelsExist() {
        Game.resetInstance();
        StartZone s1 = new StartZone(20, 200, 80, 100);
        EndZone   e1 = new EndZone(700, 200, 80, 100);
        StartZone s2 = new StartZone(20, 200, 80, 100);
        EndZone   e2 = new EndZone(700, 200, 80, 100);
        Board b1 = new Board(s1, e1, List.of());
        Board b2 = new Board(s2, e2, List.of());
        Player p = new Player(50, 240);
        Coin c1 = new Coin(400, 240);
        Coin c2 = new Coin(400, 240);
        Level lv1 = new Level(1, b1, List.of(), List.of(c1));
        Level lv2 = new Level(2, b2, List.of(), List.of(c2));
        Game g = Game.getInstance(p, List.of(lv1, lv2));
        g.nextLevel();
        assertEquals(1, g.getCurrentLevelIndex());
        assertEquals(GameState.PLAYING, g.getState());
    }

    /** update() deberia detectar colision con enemigo y llamar checkDeath(). */
    @Test
    void shouldUpdateDetectEnemyCollisionAndCallCheckDeath() {
        game.start();
        player.setPosition(enemy.getBounds().x, enemy.getBounds().y);
        game.update();
        assertEquals(1, game.getDeaths());
    }

    /** update() deberia recoger moneda cuando el jugador colisiona con ella. */
    @Test
    void shouldUpdateCollectCoinOnPlayerCollision() {
        game.start();
        player.setPosition(coin.getBounds().x, coin.getBounds().y);
        game.update();
        assertTrue(coin.isCollected());
    }

    /** update() deberia pasar a WIN cuando el jugador completa el nivel. */
    @Test
    void shouldUpdateSetWinWhenLevelCompleted() {
        game.start();
        coin.collect();
        player.setPosition(720, 240);
        game.update();
        assertEquals(GameState.WIN, game.getState());
    }

    /** checkDeath() con escudo activo no deberia incrementar muertes. */
    @Test
    void shouldCheckDeathNotIncrementDeathsWhenShielded() {
        player.applyType(PlayerType.GREEN);
        game.checkDeath();
        assertEquals(0, game.getDeaths());
    }

    /** update() no deberia actualizar cuando el estado es WIN. */
    @Test
    void shouldUpdateNotRunWhenStateIsWin() {
        game.nextLevel(); // WIN porque solo hay un nivel
        int deathsBefore = game.getDeaths();
        game.update();
        assertEquals(deathsBefore, game.getDeaths());
    }

    /** setters de carga deberian actualizar los valores correctamente. */
    @Test
    void shouldSettersUpdateValuesCorrectly() {
        game.setState(GameState.PAUSED);
        game.setDeaths(5);
        game.setSecondsLeft(90);
        game.setCurrentLevelIndex(0);
        assertEquals(GameState.PAUSED, game.getState());
        assertEquals(5, game.getDeaths());
        assertEquals(90, game.getSecondsLeft());
        assertEquals(0, game.getCurrentLevelIndex());
    }

    /** update() no deberia actualizar cuando el estado es PAUSED. */
    @Test
    void shouldUpdateNotRunWhenStateIsPaused() {
        game.pause();
        int deathsBefore = game.getDeaths();
        game.update();
        assertEquals(deathsBefore, game.getDeaths());
    }

    /** update() no deberia actualizar cuando el estado es TIMEOUT. */
    @Test
    void shouldUpdateNotRunWhenStateIsTimeout() {
        game.setState(GameState.TIMEOUT);
        int deathsBefore = game.getDeaths();
        game.update();
        assertEquals(deathsBefore, game.getDeaths());
    }

    /** getPlayer deberia retornar el jugador del juego. */
    @Test
    void shouldGetPlayerReturnCorrectPlayer() {
        assertSame(player, game.getPlayer());
    }

    /** getCurrentLevel deberia retornar el nivel actual. */
    @Test
    void shouldGetCurrentLevelReturnCurrentLevel() {
        assertNotNull(game.getCurrentLevel());
    }

    /** start() deberia establecer estado PLAYING. */
    @Test
    void shouldStartSetPlayingState() {
        game.setState(GameState.PAUSED);
        game.start();
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** checkDeath() con checkpoint activo deberia respetar el checkpoint. */
    @Test
    void shouldCheckDeathRespawnAtCheckpointWhenActive() {
        player.saveCheckpoint(390, 230);
        coin.collect();
        game.checkDeath();
        assertEquals(390, player.getX());
        assertEquals(230, player.getY());
        assertTrue(coin.isCollected());
    }

    /** update() deberia matar al jugador al colisionar con SliderEnemy. */
    @Test
    void shouldUpdateKillPlayerOnSliderEnemyCollision() {
        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        SliderEnemy slider = new SliderEnemy(50, 240, 3, 500);
        Level lv = new Level(1, board, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            List.of(c), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            List.of(slider), new java.util.ArrayList<>(), new java.util.ArrayList<>(), null);
        Game g = Game.getInstance(p, List.of(lv));
        g.start();
        g.update();
        assertEquals(1, g.getDeaths());
    }

    /** update() deberia matar al jugador al colisionar con AcceleratedEnemy. */
    @Test
    void shouldUpdateKillPlayerOnAcceleratedEnemyCollision() {
        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        AcceleratedEnemy ae = new AcceleratedEnemy(50, 240, 0, 0, 800, 500);
        Level lv = new Level(1, board, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            List.of(c), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            new java.util.ArrayList<>(), List.of(ae), new java.util.ArrayList<>(), null);
        Game g = Game.getInstance(p, List.of(lv));
        g.start();
        g.update();
        assertEquals(1, g.getDeaths());
    }

    /** update() deberia matar al jugador al colisionar con Bomb. */
    @Test
    void shouldUpdateKillPlayerOnBombCollision() {
        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        Bomb bomb = new Bomb(50, 240);
        Level lv = new Level(1, board, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            List.of(c), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            new java.util.ArrayList<>(), new java.util.ArrayList<>(), List.of(bomb), null);
        Game g = Game.getInstance(p, List.of(lv));
        g.start();
        g.update();
        assertEquals(1, g.getDeaths());
    }

    /** update() deberia recoger SkinCoin al colisionar. */
    @Test
    void shouldUpdateCollectSkinCoinOnCollision() {
        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        SkinCoin sc = new SkinCoin(50, 240);
        Level lv = new Level(1, board, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            List.of(c), List.of(sc), new java.util.ArrayList<>());
        Game g = Game.getInstance(p, List.of(lv));
        g.start();
        g.update();
        assertTrue(sc.isCollected());
        assertEquals(PlayerType.BLUE, p.getType());
    }

    /** update() deberia recoger GreenCoin al colisionar. */
    @Test
    void shouldUpdateCollectGreenCoinOnCollision() {
        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        GreenCoin gc = new GreenCoin(50, 240);
        Level lv = new Level(1, board, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            List.of(c), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(), gc);
        Game g = Game.getInstance(p, List.of(lv));
        g.start();
        g.update();
        assertTrue(gc.isCollected());
    }

    /** update() deberia recoger LifeSource al colisionar. */
    @Test
    void shouldUpdateCollectLifeSourceOnCollision() {
        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        LifeSource ls = new LifeSource(50, 240);
        Level lv = new Level(1, board, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            List.of(c), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            null, List.of(ls));
        Game g = Game.getInstance(p, List.of(lv));
        g.start();
        g.update();
        assertTrue(ls.isCollected());
    }

    /** nextLevel() deberia resetear tipo y checkpoint del jugador al avanzar. */
    @Test
    void shouldNextLevelResetPlayerTypeAndCheckpointWhenAdvancing() {
        Game.resetInstance();
        StartZone s1 = new StartZone(20, 200, 80, 100);
        EndZone   e1 = new EndZone(700, 200, 80, 100);
        StartZone s2 = new StartZone(20, 200, 80, 100);
        EndZone   e2 = new EndZone(700, 200, 80, 100);
        Board b1 = new Board(s1, e1, List.of());
        Board b2 = new Board(s2, e2, List.of());
        Player p = new Player(50, 240);
        p.applyType(PlayerType.BLUE);
        p.saveCheckpoint(300, 200);
        Level lv1 = new Level(1, b1, List.of(), List.of(new Coin(400, 240)));
        Level lv2 = new Level(2, b2, List.of(), List.of(new Coin(400, 240)));
        Game g = Game.getInstance(p, List.of(lv1, lv2));
        g.nextLevel();
        assertEquals(PlayerType.RED, p.getType());
        assertEquals(-1, p.getCheckpointX());
    }

    /** Level.reset() con GreenCoin deberia resetearla. */
    @Test
    void shouldLevelResetGreenCoinWhenPresent() {
        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        GreenCoin gc = new GreenCoin(400, 240);
        Player p = new Player(50, 240);
        gc.collect(p);
        Level lv = new Level(1, board, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            List.of(new Coin(300, 240)), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(), gc);
        lv.reset(p);
        assertFalse(gc.isCollected());
    }

    /** Level.reset() con LifeSource deberia resetearla. */
    @Test
    void shouldLevelResetLifeSourceWhenPresent() {
        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        LifeSource ls = new LifeSource(400, 240);
        Player p = new Player(50, 240);
        ls.collect(p);
        Level lv = new Level(1, board, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            List.of(new Coin(300, 240)), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            null, List.of(ls));
        lv.reset(p);
        assertFalse(ls.isCollected());
    }

    /** update() deberia matar al jugador al colisionar con PatrolEnemy. */
    @Test
    void shouldUpdateKillPlayerOnPatrolEnemyCollision() {
        Game.resetInstance();
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Player p = new Player(50, 240);
        Coin c = new Coin(400, 240);
        PatrolEnemy patrol = new PatrolEnemy(50, 240, 3, new int[][]{{51, 240}});
        Level lv = new Level(1, board, new java.util.ArrayList<>(), List.of(patrol),
            List.of(c), new java.util.ArrayList<>(), new java.util.ArrayList<>());
        Game g = Game.getInstance(p, List.of(lv));
        g.start();
        g.update();
        assertEquals(1, g.getDeaths());
    }

    /** update() con GreenCoin null no deberia lanzar excepcion. */
    @Test
    void shouldUpdateNotThrowWhenGreenCoinIsNull() {
        game.start();
        assertDoesNotThrow(() -> game.update());
    }

    /** Game getInstance deberia retornar instancia existente sin crear nueva. */
    @Test
    void shouldGetInstanceReturnExistingInstanceWithoutCreatingNew() {
        Game first = Game.getInstance();
        Game second = Game.getInstance(player, List.of(game.getCurrentLevel()));
        assertSame(first, second);
    }

    /** update() deberia decrementar secondsLeft cada 60 ticks. */
    @Test
    void shouldUpdateDecrementSecondsLeftEvery60Ticks() {
        game.start();
        for (int i = 0; i < 60; i++) game.update();
        assertEquals(179, game.getSecondsLeft());
    }

    /** checkDeath() con escudo LifeSource no deberia incrementar muertes. */
    @Test
    void shouldCheckDeathNotIncrementDeathsWhenLifeShieldActive() {
        player.activateLifeShield();
        game.checkDeath();
        assertEquals(0, game.getDeaths());
    }

    /** pause() en estado WIN no deberia cambiar el estado. */
    @Test
    void shouldPauseNotChangeStateWhenStateIsWin() {
        game.nextLevel();
        assertEquals(GameState.WIN, game.getState());
        game.pause();
        assertEquals(GameState.WIN, game.getState());
    }

    /** pause() en estado TIMEOUT no deberia cambiar el estado. */
    @Test
    void shouldPauseNotChangeStateWhenStateIsTimeout() {
        game.setState(GameState.TIMEOUT);
        game.pause();
        assertEquals(GameState.TIMEOUT, game.getState());
    }
}
